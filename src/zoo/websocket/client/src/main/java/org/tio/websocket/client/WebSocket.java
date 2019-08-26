package org.tio.websocket.client;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import org.tio.websocket.client.event.CloseEvent;
import org.tio.websocket.client.event.ErrorEvent;
import org.tio.websocket.client.event.MessageEvent;
import org.tio.websocket.client.event.OpenEvent;
import org.tio.websocket.common.WsPacket;
import org.tio.websocket.common.WsRequest;

import io.reactivex.Observable;

public interface WebSocket {
  int CONNECTING = 0;
  int OPEN = 1;
  int CLOSING = 2;
  int CLOSED = 3;

  String getExtensions();

  /**
   * To add listener of close event
   * @param listener listener
   * @return Runnable to remove this listener
   */
  Runnable addOnClose(Consumer<CloseEvent> listener);

  /**
   * To add listener of error event
   * @param listener listener
   * @return Runnable to remove this listener 
   */
  Runnable addOnError(Consumer<ErrorEvent> listener);

  /**
   * To add listener of message event
   * @param listener listener
   * @return Runnable to remove this listener
   */
  Runnable addOnMessage(Consumer<MessageEvent> listener);

  /**
   * To add listener of open event
   * @param listener listener
   * @return Runnable to remove this listener
   */
  Runnable addOnOpen(Consumer<OpenEvent> listener);

  /**
   * To add listener of throws event
   * @param listener listener
   * @return Runnable to remove this listener
   */
  Runnable addOnThrows(Consumer<Throwable> listener);

  String getProtocol();

  /**
   * ready state: CONNECTING, OPEN, CLOSING, CLOSED
   * @return
   */
  int getReadyState();

  /**
   * url
   * @return
   */
  String getUrl();

  /**
   * Connect to server
   * @throws Exception
   */
  void connect() throws Exception;

  /**
   * Close this WebSocket
   */
  default void close() {
    close(1000);
  }

  /**
   * Close this WebSocket by code
   */
  default void close(int code) {
    close(code, "");
  }

  /**
   * Close this WebSocket by code and reason
   */
  void close(int code, String reason);

  /**
   * Send message to server
   * @param data
   */
  default void send(byte[] data) {
    send(WsRequest.fromBytes(data));
  }

  /**
   * Send message to server
   * @param data
   */
  default void send(ByteBuffer data) {
    if (data.hasArray()) {
      send(data.array());
    } else {
      int remaining = data.remaining();
      byte[] bytes = new byte[remaining];
      data.get(bytes);
      send(bytes);
    }
  }

  /**
   * Send message to server
   * @param data
   */
  void send(String data);

  /**
   * Send packet to server
   * @param packet
   */
  void send(WsPacket packet);

  /**
   * Get message stream from server
   * @return
   */
  Observable<WsPacket> getMessageStream();

}
