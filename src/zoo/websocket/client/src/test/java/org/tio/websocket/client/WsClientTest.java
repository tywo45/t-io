package org.tio.websocket.client;

import org.junit.Test;

import static org.junit.Assert.*;

public class WsClientTest {

  @Test
  public void uriRegexp() {
    String re =
        "wss?\\://((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])|(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9]))(\\:[0-9]+)?(/.*)?(\\?.*)?";
    assertEquals(true, "ws://localhost:8988/djui?uihd=132se3s&nsufdn=7udhgewbye".matches(re));
    assertEquals(true, "wss://localhost:8988/djui?uihd=132se3s&nsufdn=7udhgewbye".matches(re));
    assertEquals(false, "http://localhost:8988/djui?uihd=132se3s&nsufdn=7udhgewbye".matches(re));
    assertEquals(true, "wss://www.baidu.com/djui?uihd=132se3s&nsufdn=7udhgewbye".matches(re));
    assertEquals(false, "ws://localho,st:8988/djui?uihd=132se3s&nsufdn=7udhgewbye".matches(re));
    assertEquals(true, "wss://127.0.0.1/djui?uihd=132se3s&nsufdn=7udhgewbye".matches(re));
    assertEquals(true, "ws://192.211.213.100".matches(re));
  }
}
