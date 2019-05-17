package org.tio.websocket.client.event;

// ref: https://developer.mozilla.org/en-US/docs/Web/API/CloseEvent
public class CloseEvent implements WsEvent {
  public final int code;
  public final String reason;
  public final boolean wasClean;

  public CloseEvent(int code, String reason, boolean wasClean) {
    this.code = code;
    this.reason = reason;
    this.wasClean = wasClean;
  }
}
