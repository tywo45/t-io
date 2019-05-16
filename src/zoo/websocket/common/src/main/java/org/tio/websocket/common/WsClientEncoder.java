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
      WsPacket packet, GroupContext groupContext, ChannelContext channelContext) {
    byte[] wsBody = packet.getBody(); // 就是ws的body，不包括ws的头
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

    byte opcode = packet.getWsOpcode().getCode();
    byte header0 = (byte) (packet.isWsEof() ? -128 : 0);
    header0 |= opcode;

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
  }
}
