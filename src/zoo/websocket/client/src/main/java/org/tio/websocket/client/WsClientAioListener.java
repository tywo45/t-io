package org.tio.websocket.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.TioClient;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.websocket.client.kit.ReflectKit;
import org.tio.websocket.common.WsSessionContext;

import javax.net.ssl.SSLHandshakeException;

public class WsClientAioListener implements ClientAioListener {
  private static final Logger log = LoggerFactory.getLogger(WsClientAioListener.class);

  @Override
  public void onAfterConnected(
      ChannelContext channelContext, boolean isConnected, boolean isReconnect) throws Exception {}

  @Override
  public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize)
      throws Exception {}

  @Override
  public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes)
      throws Exception {}

  @Override
  public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess)
      throws Exception {}

  @Override
  public void onAfterHandled(ChannelContext channelContext, Packet packet, long cost)
      throws Exception {}

  @Override
  public void onBeforeClose(
      ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove)
      throws Exception {
    WsClient client = (WsClient) channelContext.getAttribute(WebSocketImpl.clientIntoCtxAttribute);
    if (throwable instanceof SSLHandshakeException && client.uri.getScheme().equals("wss")) {
      log.warn("wss但没有正确的CA证书，更为ws重试");
      ReflectKit.setField(client.uri, "scheme", "ws");
      if (client.uri.getPort() == 443) ReflectKit.setField(client.uri, "port", 80);
      client.construct();
      client.connect();
      return;
    }
    client.ws.clear(1011, remark);
    channelContext.setAttribute(WebSocketImpl.clientIntoCtxAttribute, null);
  }
}
