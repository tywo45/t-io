package org.tio.websocket.client;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.ClientChannelContext;
import org.tio.core.Node;
import org.tio.core.Tio;
import org.tio.core.intf.Packet;
import org.tio.http.common.HeaderName;
import org.tio.http.common.HeaderValue;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.HttpResponseStatus;
import org.tio.http.common.Method;
import org.tio.utils.hutool.StrUtil;
import org.tio.websocket.client.event.CloseEvent;
import org.tio.websocket.client.event.ErrorEvent;
import org.tio.websocket.client.event.MessageEvent;
import org.tio.websocket.client.event.OpenEvent;
import org.tio.websocket.client.httpclient.ClientHttpRequest;
import org.tio.websocket.client.kit.ByteKit;
import org.tio.websocket.client.kit.ObjKit;
import org.tio.websocket.client.kit.TioKit;
import org.tio.websocket.common.Opcode;
import org.tio.websocket.common.WsPacket;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.common.WsSessionContext;
import org.tio.websocket.common.util.BASE64Util;
import org.tio.websocket.common.util.SHA1Util;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class WebSocketImpl implements WebSocket {
  @SuppressWarnings("unused")
  private static final Logger log = LoggerFactory.getLogger(WebSocketImpl.class);

  static final String packetPublisherKey = "__WS_PACKET_PUBLISHER__";
  static final String clientIntoCtxAttribute = "__WS_CLIENT__";
  private static final int maxBodyBytesLength = (int) (1024 * 1024 * 0.25); // 0.25 MB

  private volatile int readyState = WebSocket.CONNECTING;
  private String[] protocols = new String[] {};
  private WsClient wsClient;
  private Map<String, String> additionalHttpHeaders;
  private ClientChannelContext ctx;
  private Subject<Packet> publisher = PublishSubject.<Packet>create().toSerialized();
  private String secWebsocketKey = null;

  // concurrent hash set
  private Set<Consumer<OpenEvent>> onOpenListenerSet =
      Collections.newSetFromMap(new ConcurrentHashMap<>());
  private Set<Consumer<CloseEvent>> onCloseListenerSet =
      Collections.newSetFromMap(new ConcurrentHashMap<>());
  private Set<Consumer<ErrorEvent>> onErrorListenerSet =
      Collections.newSetFromMap(new ConcurrentHashMap<>());
  private Set<Consumer<Throwable>> onThrowsListenerSet =
      Collections.newSetFromMap(new ConcurrentHashMap<>());

  private Subject<WsPacket> sendWsPacketStream = PublishSubject.<WsPacket>create().toSerialized();
  private Subject<Object> sendNotifier = PublishSubject.create().toSerialized();

  WebSocketImpl(WsClient wsClient) {
    this(wsClient, null);
  }

  WebSocketImpl(WsClient wsClient, Map<String, String> additionalHttpHeaders) {
    this.wsClient = wsClient;
    this.additionalHttpHeaders = additionalHttpHeaders;
    bindInitStreamObserver();
  }

  @Override
  public synchronized void connect() throws Exception {
    CountDownLatch wg = new CountDownLatch(1);
    int i = 1;
    while (wsClient.clientChannelContext == null) {
      wsClient.clientChannelContext =
          wsClient.tioClient.connect(new Node(wsClient.uri.getHost(), wsClient.uri.getPort()));
      if (wsClient.clientChannelContext != null) break;
      wg.await(10 * i, TimeUnit.MILLISECONDS);
      i++;
    }

    ctx = wsClient.clientChannelContext;
    ctx.setAttribute(packetPublisherKey, publisher);
    ctx.setAttribute(clientIntoCtxAttribute, wsClient);
    WsSessionContext session = new WsSessionContext();
    ctx.set(session);

    handshake();
  }

  @Override
  public String getExtensions() {
    return null;
  }

  @Override
  public Runnable addOnClose(Consumer<CloseEvent> listener) {
    if (listener != null) onCloseListenerSet.add(listener);
    return () -> {
      if (listener != null) onCloseListenerSet.remove(listener);
    };
  }

  @Override
  public Runnable addOnError(Consumer<ErrorEvent> listener) {
    if (listener != null) onErrorListenerSet.add(listener);
    return () -> {
      if (listener != null) onErrorListenerSet.remove(listener);
    };
  }

  @Override
  public Runnable addOnMessage(Consumer<MessageEvent> listener) {
    Disposable disposable = getMessageStream().map(MessageEvent::new).subscribe(listener::accept);
    return disposable::dispose;
  }

  @Override
  public Runnable addOnOpen(Consumer<OpenEvent> listener) {
    if (listener != null) onOpenListenerSet.add(listener);
    return () -> {
      if (listener != null) onOpenListenerSet.remove(listener);
    };
  }

  @Override
  public Runnable addOnThrows(Consumer<Throwable> listener) {
    if (listener != null) onThrowsListenerSet.add(listener);
    return () -> {
      if (listener != null) onThrowsListenerSet.remove(listener);
    };
  }

  private void onOpen() {
    OpenEvent openEvent = new OpenEvent();
    Consumer<OpenEvent> onOpen = wsClient.config.getOnOpen();
    if (onOpen != null) {
      onOpen.accept(openEvent);
    }
    onOpenListenerSet.forEach(it -> it.accept(openEvent));
    sendNotifier.onNext(true);
  }

  private void onClose(int code, String reason) {
    sendWsPacketStream.onComplete();
    Consumer<CloseEvent> onClose = wsClient.config.getOnClose();
    if (onClose != null) {
      onClose.accept(new CloseEvent(code, reason, ctx.isRemoved));
    }
    onCloseListenerSet.forEach(it -> it.accept(new CloseEvent(code, reason, ctx.isRemoved)));
  }

  private void onError(String msg) {
    sendWsPacketStream.onComplete();
    ErrorEvent errorEvent = new ErrorEvent(msg);
    Consumer<ErrorEvent> onError = wsClient.config.getOnError();
    if (onError != null) {
      onError.accept(errorEvent);
    }
    onErrorListenerSet.forEach(it -> it.accept(errorEvent));
  }

  private void onThrows(Throwable e) {
    Consumer<Throwable> onThrows = wsClient.config.getOnThrows();
    if (onThrows != null) {
      onThrows.accept(e);
    }
    onThrowsListenerSet.forEach(it -> it.accept(e));
  }

  @Override
  public String getProtocol() {
    StringBuilder p = new StringBuilder();
    int i = 0;
    for (String proto : protocols) {
      p.append(proto);
      if (i != 0 && i != protocols.length - 1) {
        p.append(",");
      }
      i++;
    }
    return p.toString();
  }

  @Override
  public int getReadyState() {
    return readyState;
  }

  @Override
  public String getUrl() {
    return wsClient.rawUri;
  }

  @Override
  public synchronized void close(int code, String reason) {
    if (readyState == WebSocket.CLOSED) return;
    if (readyState != WebSocket.CLOSING) {
      readyState = WebSocket.CLOSING;
      WsPacket close = new WsPacket();
      close.setWsOpcode(Opcode.CLOSE);
      if (StrUtil.isBlank(reason)) reason = "";
      try {
        byte[] reasonBytes = reason.getBytes("UTF-8");
        short c = (short) code;
        ByteBuffer body = ByteBuffer.allocate(2 + reasonBytes.length);
        body.putShort(c);
        body.put(reasonBytes);
        close.setBody(body.array());
        close.setWsBodyLength(close.getBody().length);
      } catch (UnsupportedEncodingException e) {
      }
      Tio.send(ctx, close);
      String finalReason = reason;
      Observable.timer(1, TimeUnit.SECONDS)
          .subscribe(
              i -> {
                clear(code, finalReason);
              });
    } else {
      clear(code, reason);
    }
  }

  synchronized void clear(int code, String reason) {
    if (readyState == WebSocket.CLOSED) return;
    readyState = WebSocket.CLOSED;
    publisher.onComplete();
    onClose(code, reason);
    try {
      wsClient.tioClient.stop();
    } catch (Exception e) {
    }
  }

  @Override
  public void send(String data) {
    send(WsRequest.fromText(data, wsClient.config.getCharset()));
  }

  @Override
  public void send(WsPacket packet) {
    sendWsPacketStream.onNext(packet);
    if (readyState == WebSocket.OPEN) sendNotifier.onNext(true);
  }

  private synchronized void sendImmediately(WsPacket packet) {
    byte[] wsBody = packet.getBody();
    byte[][] wsBodies = packet.getBodys();
    int wsBodyLength = 0;
    if (wsBody != null) {
      wsBodyLength += wsBody.length;
    } else if (wsBodies != null) {
      for (byte[] bs : wsBodies) {
        wsBodyLength += bs.length;
      }
    }
    ByteBuffer bodyBuf = null;
    if (wsBody != null && wsBody.length > 0) {
      bodyBuf = ByteBuffer.wrap(wsBody);
    } else if (wsBodies != null) {
      bodyBuf = ByteBuffer.allocate(wsBodyLength);
      for (byte[] bs : wsBodies) {
        bodyBuf.put(bs);
      }
    }
    if (bodyBuf == null || wsBodyLength == 0) {
      Tio.send(ctx, packet);
    } else {
      if (wsBodyLength <= maxBodyBytesLength) {
        packet.setBody(bodyBuf.array());
        packet.setBodys(null);
        Tio.send(ctx, packet);
      } else {
        byte[][] parts = ByteKit.split(bodyBuf.array(), maxBodyBytesLength);
        for (int i = 0; i < parts.length; i++) {
          byte[] body = parts[i];
          WsPacket sentPacket = cloneWsPacket(packet);
          sentPacket.setBodys(null);
          sentPacket.setBody(body);
          sentPacket.setWsBodyLength(body.length);
          if (i == 0) {
            sentPacket.setWsEof(false);
          } else if (i < parts.length - 1) {
            sentPacket.setWsEof(false);
            sentPacket.setWsOpcode(Opcode.NOT_FIN);
          } else {
            sentPacket.setWsEof(true);
            sentPacket.setWsOpcode(Opcode.NOT_FIN);
          }
          TioKit.bSend(ctx, sentPacket, 60, TimeUnit.SECONDS);
        }
      }
    }
  }

  @Override
  public Observable<WsPacket> getMessageStream() {
    return getWsPacketStream()
        .filter(p -> p.getWsOpcode().equals(Opcode.BINARY) || p.getWsOpcode().equals(Opcode.TEXT));
  }

  private Observable<WsPacket> getWsPacketStream() {
    return publisher.filter(p -> p instanceof WsPacket).map(p -> (WsPacket) p);
  }

  private void handshake() {
    readyState = WebSocket.CONNECTING;

    ClientChannelContext ctx = wsClient.getClientChannelContext();
    WsSessionContext session = (WsSessionContext) ctx.get();

    session.setHandshaked(false);

    String path = wsClient.uri.getPath();
    if (StrUtil.isBlank(path)) {
      path = "/";
    }
    ClientHttpRequest httpRequest =
        new ClientHttpRequest(Method.GET, path, wsClient.uri.getRawQuery());
    Map<String, String> headers = new HashMap<>();
    if (additionalHttpHeaders != null) headers.putAll(additionalHttpHeaders);
    headers.put("Host", wsClient.uri.getHost() + ":" + wsClient.uri.getPort());
    headers.put("Upgrade", "websocket");
    headers.put("Connection", "Upgrade");
    headers.put("Sec-WebSocket-Key", getSecWebsocketKey());
    headers.put("Sec-WebSocket-Version", "13");
    httpRequest.setHeaders(headers);

    session.setHandshakeRequest(httpRequest);

    ObjKit.Box<Disposable> disposableBox = ObjKit.box(null);

    disposableBox.value =
        publisher
            .filter(packet -> !session.isHandshaked())
            .subscribe(
                packet -> {
                  if (packet instanceof HttpResponse) {
                    HttpResponse resp = (HttpResponse) packet;

                    if (resp.getStatus() == HttpResponseStatus.C101) {
                      HeaderValue upgrade = resp.getHeader(HeaderName.Upgrade);
                      if (upgrade == null || !upgrade.value.toLowerCase().equals("websocket")) {
                        close(1002, "no upgrade or upgrade invalid");
                        return;
                      }
                      HeaderValue connection = resp.getHeader(HeaderName.Connection);
                      if (connection == null || !connection.value.toLowerCase().equals("upgrade")) {
                        close(1002, "no connection or connection invalid");
                        return;
                      }
                      HeaderValue secWebsocketAccept =
                          resp.getHeader(HeaderName.Sec_WebSocket_Accept);
                      if (secWebsocketAccept == null
                          || !verifySecWebsocketAccept(secWebsocketAccept.value)) {
                        close(1002, "no Sec_WebSocket_Accept or Sec_WebSocket_Accept invalid");
                        return;
                      }
                      // TODO: Sec-WebSocket-Extensions, Sec-WebSocket-Protocol

                      readyState = WebSocket.OPEN;
                      session.setHandshaked(true);
                      onOpen();
                    } else {
                      // TODO: support other http code
                      close(1002, "not support http code: " + resp.getStatus().status);
                      return;
                    }

                    disposableBox.value.dispose();
                  }
                });

    Tio.send(ctx, httpRequest);
  }

  private String getSecWebsocketKey() {
    if (secWebsocketKey == null) {
      byte[] bytes = new byte[16];
      for (int i = 0; i < 16; i++) {
        bytes[i] = (byte) (Math.random() * 256);
      }
      secWebsocketKey = BASE64Util.byteArrayToBase64(bytes);
    }
    return secWebsocketKey;
  }

  private boolean verifySecWebsocketAccept(String secWebsocketAccept) {
    return BASE64Util.byteArrayToBase64(
            SHA1Util.SHA1(secWebsocketKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"))
        .equals(secWebsocketAccept);
  }

  private void bindInitStreamObserver() {
    sendWsPacketStream
        .buffer(sendNotifier) // Is it need back pressure control?
        .subscribe(
            packets -> packets.forEach(this::sendImmediately),
            this::onThrows,
            sendNotifier::onComplete);
    getMessageStream()
        .subscribe(
            p -> {
              Consumer<MessageEvent> onMessage = wsClient.config.getOnMessage();
              if (onMessage != null) {
                onMessage.accept(new MessageEvent(p));
              }
            },
            this::onThrows);
    getWsPacketStream()
        .filter(p -> p.getWsOpcode().equals(Opcode.CLOSE))
        .subscribe(
            packet -> {
              if (readyState == WebSocket.CLOSED) return;
              byte[] body = packet.getBody();
              short code = 1000;
              String reason = "";
              if (body != null && body.length >= 2) {
                ByteBuffer bodyBuf = ByteBuffer.wrap(body);
                code = bodyBuf.getShort();
                byte[] reasonBytes = new byte[body.length - 2];
                bodyBuf.get(reasonBytes, 0, reasonBytes.length);
                reason = new String(reasonBytes, "UTF-8");
              }
              if (readyState == WebSocket.CLOSING) {
                clear(code, reason);
              } else {
                readyState = WebSocket.CLOSING;
                packet.setBody(ByteBuffer.allocate(2).putShort(code).array());
                Tio.send(ctx, packet);
                close(code, reason);
              }
            });
    getWsPacketStream()
        .filter(p -> p.getWsOpcode().equals(Opcode.PING))
        .subscribe(
            packet -> {
              WsPacket pong = new WsPacket();
              pong.setWsOpcode(Opcode.PONG);
              pong.setWsEof(true);
              Tio.send(ctx, pong);
            });
  }

  private static WsPacket cloneWsPacket(WsPacket p) {
    WsPacket packet = new WsPacket();
    packet.setHandShake(p.isHandShake());
    packet.setBody(p.getBody());
    packet.setBodys(p.getBodys());
    packet.setWsEof(p.isWsEof());
    packet.setWsOpcode(p.getWsOpcode());
    packet.setWsHasMask(p.isWsHasMask());
    packet.setWsBodyLength(p.getWsBodyLength());
    packet.setWsMask(p.getWsMask());
    packet.setWsBodyText(p.getWsBodyText());
    return packet;
  }
}
