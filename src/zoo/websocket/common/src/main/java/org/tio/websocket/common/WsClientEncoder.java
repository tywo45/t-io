package org.tio.websocket.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.utils.ByteBufferUtils;

import java.nio.ByteBuffer;
import java.util.BitSet;

public class WsClientEncoder {
  private static Logger log = LoggerFactory.getLogger(WsClientEncoder.class);

  /*
      0                   1                   2                   3
      0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     +-+-+-+-+-------+-+-------------+-------------------------------+
     |F|R|R|R| opcode|M| Payload len |    Extended payload length    |
     |I|S|S|S|  (4)  |A|     (7)     |             (16/64)           |
     |N|V|V|V|       |S|             |   (if payload len==126/127)   |
     | |1|2|3|       |K|             |                               |
     +-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
     |     Extended payload length continued, if payload len == 127  |
     + - - - - - - - - - - - - - - - +-------------------------------+
     |                               |Masking-key, if MASK set to 1  |
     +-------------------------------+-------------------------------+
     | Masking-key (continued)       |          Payload Data         |
     +-------------------------------- - - - - - - - - - - - - - - - +
     :                     Payload Data continued ...                :
     + - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
     |                     Payload Data continued ...                |
     +---------------------------------------------------------------+
  */
  public static ByteBuffer encode(
      WsRequest packet, GroupContext groupContext, ChannelContext channelContext) {
    byte[] wsBody = packet.getBody();//就是ws的body，不包括ws的头
    byte[][] wsBodies = packet.getBodys();
    int wsBodyLength = 0;
    if (wsBody != null) {
      wsBodyLength += wsBody.length;
    } else if (wsBodies != null) {
      for (int i = 0; i < wsBodies.length; i++) {
        byte[] bs = wsBodies[i];
        wsBodyLength += bs.length;
      }
    }

    byte header0 = (byte) (0x8f & (packet.getWsOpcode().getCode() | 0xf0));
    if(!packet.isWsEof()){
      header0 = (byte)(header0 & ~(1));
    }

    ByteBuffer buf = null;
    if (wsBodyLength < 126) {
      buf = ByteBuffer.allocate(2 + wsBodyLength);
      buf.put(header0);
      buf.put((byte) wsBodyLength);
    } else if (wsBodyLength < (1 << 16) - 1) {
      buf = ByteBuffer.allocate(4 + wsBodyLength);
      buf.put(header0);
      buf.put((byte) 126);
      ByteBufferUtils.writeUB2WithBigEdian(buf, wsBodyLength);
    } else {
      buf = ByteBuffer.allocate(10 + wsBodyLength);
      buf.put(header0);
      buf.put((byte) 127);

      //			buf.put(new byte[] { 0, 0, 0, 0 });
      buf.position(buf.position() + 4);

      ByteBufferUtils.writeUB4WithBigEdian(buf, wsBodyLength);
    }

    if (wsBody != null && wsBody.length > 0) {
      buf.put(wsBody);
    } else if (wsBodies != null) {
      for (int i = 0; i < wsBodies.length; i++) {
        byte[] bs = wsBodies[i];
        buf.put(bs);
      }
    }

    return buf;
//    byte byte0 = 0;
//    if (packet.isWsEof()) {
//      byte0 = (byte) (byte0 | 1);
//      switch (packet.getWsOpcode()) {
//        case TEXT:
//          byte0 = (byte) (byte0 | 0x10);
//          break;
//        case BINARY:
//          byte0 = (byte) (byte0 | 0x20);
//          break;
//        case CLOSE:
//          byte0 = (byte) (byte0 | 0x80);
//          break;
//        case PING:
//          byte0 = (byte) (byte0 | 0x90);
//          break;
//        case PONG:
//          byte0 = (byte) (byte0 | 0xA0);
//          break;
//      }
//    }
//
//    byte byte1 = 0;
//    if (packet.isWsHasMask()) {
//      byte1 = (byte) (byte1 | 1);
//    }
//
//    byte[] wsBody = packet.getBody();//就是ws的body，不包括ws的头
//    byte[][] wsBodies = packet.getBodys();
//    int wsBodyLength = 0;
//    if (wsBody != null) {
//      wsBodyLength += wsBody.length;
//    } else if (wsBodies != null) {
//      for (int i = 0; i < wsBodies.length; i++) {
//        byte[] bs = wsBodies[i];
//        wsBodyLength += bs.length;
//      }
//    }
//
//    byte header0 = (byte) (0x8f & (packet.getWsOpcode().getCode() | 0xf0));
//    ByteBuffer buf = null;
//    if (wsBodyLength < 126) {
//      buf = ByteBuffer.allocate(2 + wsBodyLength);
//      buf.put(header0);
//      buf.put((byte) wsBodyLength);
//    } else if (wsBodyLength < (1 << 16) - 1) {
//      buf = ByteBuffer.allocate(4 + wsBodyLength);
//      buf.put(header0);
//      buf.put((byte) 126);
//      ByteBufferUtils.writeUB2WithBigEdian(buf, wsBodyLength);
//    } else {
//      buf = ByteBuffer.allocate(10 + wsBodyLength);
//      buf.put(header0);
//      buf.put((byte) 127);
//
//      //			buf.put(new byte[] { 0, 0, 0, 0 });
//      buf.position(buf.position() + 4);
//
//      ByteBufferUtils.writeUB4WithBigEdian(buf, wsBodyLength);
//    }
//
//    if (wsBody != null && wsBody.length > 0) {
//      buf.put(wsBody);
//    } else if (wsBodies != null) {
//      for (int i = 0; i < wsBodies.length; i++) {
//        byte[] bs = wsBodies[i];
//        buf.put(bs);
//      }
//    }
//
//    int frameLength = 16;
//    ByteBuffer frame = ByteBuffer.allocate(frameLength);
//    frame.put(byte0);
//
//    return frame;
  }

  public static int set(int num, int i, int bit) {
    if (bit == 0) {
      return num & ~(1 << i);
    } else {
      return num | (1 << i);
    }
  }

  public static int at(byte num, int i) {
    return (num & (1 << i)) == 0 ? 0 : 1;
  }

  public static void main(String[] args) {
    ByteBuffer frame = ByteBuffer.allocate(20 * 4);
    for (byte i = 0; i < 20 * 4; i++) {
      frame.put(i);
    }
    int j = 0;
    for (byte b : frame.array()) {
      System.out.print(j + "\t");
      for (int i = 0; i < 8; i++) {
        System.out.print(at(b, i));
      }
      System.out.println();
      j++;
    }
    System.out.println();

    byte byte0 = 0;

    byte0 = (byte) (byte0 | 1);
    switch (Opcode.TEXT) {
      case TEXT:
        byte0 = (byte) (byte0 | 0x10);
        break;
      case BINARY:
        byte0 = (byte) (byte0 | 0x02);
        break;
      case CLOSE:
        byte0 = (byte) (byte0 | 0x08);
        break;
      case PING:
        byte0 = (byte) (byte0 | 0x09);
        break;
      case PONG:
        byte0 = (byte) (byte0 | 0x0A);
        break;
    }

    for (int i = 0; i < 8; i++) {
      System.out.print(at(byte0, i));
    }

    System.out.println();
    for (int i = 0; i < 8; i++) {
      System.out.print(at((byte) 0x8f, i));
    }
  }
}
