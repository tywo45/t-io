package org.tio.websocket.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.ClientChannelContext;
import org.tio.client.ClientGroupContext;
import org.tio.client.TioClient;
import org.tio.client.intf.ClientAioHandler;
import org.tio.client.intf.ClientAioListener;
import org.tio.websocket.client.config.WsClientConfig;
import org.tio.websocket.client.kit.ReflectKit;
import org.tio.websocket.client.kit.UriKit;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;

public class WsClient {
  @SuppressWarnings("unused")
  private static Logger log = LoggerFactory.getLogger(WsClient.class);

  static ClientAioHandler tioClientHandler = new WsClientAioHander();
  static ClientAioListener aioListener = new WsClientAioListener();
  static ClientGroupContext clientGroupContext =
      new ClientGroupContext(tioClientHandler, aioListener, null);
  static ClientGroupContext clientGroupContextUseSsl =
      new ClientGroupContext(tioClientHandler, aioListener, null);

  static {
    clientGroupContext.setHeartbeatTimeout(0);
    clientGroupContextUseSsl.setHeartbeatTimeout(0);
    try {
      clientGroupContextUseSsl.useSsl();
    } catch (Exception e) {
    }
  }

  /**
   * To create a WsClient.
   *
   * @param uri The uri to connect
   * @return
   * @throws IOException
   */
  public static WsClient create(String uri) throws Exception {
    return create(uri, (Map<String, String>) null);
  }

  /**
   * To create a WsClient.
   *
   * @param uri The uri to connect
   * @param additionalHttpHeaders Additional headers added to the http package sent to the server
   *     during the handshake
   * @return
   * @throws IOException
   */
  public static WsClient create(String uri, Map<String, String> additionalHttpHeaders)
      throws Exception {
    return new WsClient(uri, additionalHttpHeaders);
  }

  /**
   * To create a WsClient.
   *
   * @param uri The uri to connect
   * @param config The config of client. If you change the value later, you need to bear the
   *     possible consequences.
   * @return
   * @throws IOException
   */
  public static WsClient create(String uri, WsClientConfig config) throws Exception {
    return create(uri, null, config);
  }

  /**
   * To create a WsClient.
   *
   * @param uri The uri to connect
   * @param additionalHttpHeaders Additional headers added to the http package sent to the server
   *     during the handshake
   * @param config The config of client. If you change the value later, you need to bear the
   *     possible consequences.
   * @return
   * @throws IOException
   */
  public static WsClient create(
      String uri, Map<String, String> additionalHttpHeaders, WsClientConfig config)
      throws Exception {
    WsClient client = new WsClient(uri, additionalHttpHeaders);
    client.config = config;
    return client;
  }

  URI uri;
  String rawUri;
  TioClient tioClient;
  WsClientConfig config = new WsClientConfig();
  ClientChannelContext clientChannelContext;
  Map<String, String> additionalHttpHeaders = null;
  WebSocketImpl ws = null;
  boolean connected = false;

  WsClient(String rawUri) throws Exception {
    this(rawUri, null);
  }

  WsClient(String rawUri, Map<String, String> additionalHttpHeaders) throws Exception {
    rawUri = rawUri.trim();
    if (!rawUri.matches(
      "wss?\\://((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])|(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9]))(\\:[0-9]+)?(/.*)?(\\?.*)?")) {
      throw new Exception("Invalid scheme of " + rawUri);
    }

    this.rawUri = rawUri;
    this.additionalHttpHeaders = additionalHttpHeaders;
    uri = UriKit.parseURI(rawUri);
    int port = uri.getPort();
    if (port == -1) {
      if (uri.getScheme().equals("ws")) {
        port = 80;
        log.info("No port specified, use the default: {}", port);
      } else {
        port = 443;
      }
      try {
        ReflectKit.setField(uri, "port", port);
      } catch (Exception ex) {
      }
    }
    if (uri.getScheme().equals("ws")) tioClient = new TioClient(clientGroupContext);
    else tioClient = new TioClient(clientGroupContextUseSsl);
    ws = new WebSocketImpl(this, additionalHttpHeaders);
  }

  /**
   * connect to server
   *
   * @return WebSocket
   * @throws Exception
   */
  public synchronized WebSocket connect() throws Exception {
    if (connected) return ws;
    ws.connect();
    connected = true;
    return ws;
  }

  public void close() {
    if (ws != null) {
      ws.close();
    }
  }

  public WebSocket getWs() {
    return ws;
  }

  public WsClientConfig getConfig() {
    return config;
  }

  public TioClient getTioClient() {
    return tioClient;
  }

  public ClientChannelContext getClientChannelContext() {
    return clientChannelContext;
  }

  public URI getUri() {
    return uri;
  }

  public String getRawUri() {
    return rawUri;
  }

  public static int clientCount() {
    return clientGroupContext.connecteds.size() + clientGroupContextUseSsl.connecteds.size();
  }
}
