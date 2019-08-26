package org.tio.websocket.client.event;

public class ErrorEvent implements WsEvent {
  public final String msg;

  public ErrorEvent(String msg) {
    this.msg = msg;
  }
}
