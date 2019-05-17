package org.tio.websocket.client;

import io.reactivex.subjects.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.intf.ClientAioHandler;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.HttpResponseEncoder;
import org.tio.websocket.client.httpclient.ClientHttpRequest;
import org.tio.websocket.client.httpclient.HttpRequestEncoder;
import org.tio.websocket.client.httpclient.HttpResponseDecoder;
import org.tio.websocket.common.*;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class WsClientAioHander implements ClientAioHandler {
  private static final Logger log = LoggerFactory.getLogger(WsClientAioHander.class);

  private static final String NOT_FINAL_WEBSOCKET_PACKET_PARTS = "TIO_N_F_W_P_P";

  @Override
  public Packet heartbeatPacket(ChannelContext ctx) {
    return null;
  }

  @Override
  public Packet decode(
      ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext ctx)
      throws AioDecodeException {
    WsSessionContext session = (WsSessionContext) ctx.getAttribute();
    if (!session.isHandshaked()) {
      HttpResponse response =
          HttpResponseDecoder.decode(buffer, limit, position, readableLength, ctx);
      session.setHandshakeResponse(response);
      return response;
    }
    WsResponse packet = WsClientDecoder.decode(buffer, ctx);
    if (packet != null) {
      if (!packet.isWsEof()) { // 数据包尚未完成
        List<WsResponse> parts =
            (List<WsResponse>) ctx.getAttribute(NOT_FINAL_WEBSOCKET_PACKET_PARTS);
        if (parts == null) {
          parts = new ArrayList<>();
          ctx.setAttribute(NOT_FINAL_WEBSOCKET_PACKET_PARTS, parts);
        }
        parts.add(packet);
      } else {
        List<WsResponse> parts =
            (List<WsResponse>) ctx.getAttribute(NOT_FINAL_WEBSOCKET_PACKET_PARTS);
        if (parts != null) {
          ctx.setAttribute(NOT_FINAL_WEBSOCKET_PACKET_PARTS, null);

          parts.add(packet);
          WsResponse first = parts.get(0);
          packet.setWsOpcode(first.getWsOpcode());

          int allBodyLength = 0;
          for (WsResponse wsRequest : parts) {
            allBodyLength += wsRequest.getBody().length;
          }

          byte[] allBody = new byte[allBodyLength];
          Integer index = 0;
          for (WsResponse wsRequest : parts) {
            System.arraycopy(wsRequest.getBody(), 0, allBody, index, wsRequest.getBody().length);
            index += wsRequest.getBody().length;
          }
          packet.setBody(allBody);
        }

        HttpRequest handshakeRequest = session.getHandshakeRequest();
        if (packet.getWsOpcode() != Opcode.BINARY) {
          try {
            String text = new String(packet.getBody(), handshakeRequest.getCharset());
            packet.setWsBodyText(text);
          } catch (UnsupportedEncodingException e) {
            log.error(e.toString(), e);
          }
        }
      }
    }
    return packet;
  }

  @Override
  public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext ctx) {
    WsSessionContext session = (WsSessionContext) ctx.getAttribute();
    if (!session.isHandshaked() && packet instanceof HttpRequest) {
      try {
        return HttpRequestEncoder.encode((HttpRequest) packet, groupContext, ctx);
      } catch (UnsupportedEncodingException e) {
        log.error(e.toString());
        return null;
      }
    }
    try {
      return WsClientEncoder.encode((WsPacket) packet, groupContext, ctx);
    } catch (Exception e) {
      log.error(e.toString());
      return null;
    }
  }

  @Override
  public void handler(Packet packet, ChannelContext ctx) throws Exception {
    if (packet instanceof WsPacket) {
      WsPacket wsPacket = (WsPacket) packet;
      if (!wsPacket.isWsEof()) {
        return;
      }
    }
    Subject<Packet> packetPublisher =
        (Subject<Packet>) ctx.getAttribute(WebSocketImpl.packetPublisherKey);
    packetPublisher.onNext(packet);
  }
}
