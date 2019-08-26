package org.tio.websocket.client.event;

import org.tio.websocket.common.WsPacket;

public class MessageEvent implements WsEvent {
  public final WsPacket data;

  public MessageEvent(WsPacket data) {
    this.data = data;
  }
}
