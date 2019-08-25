package org.tio.websocket.common;

import java.nio.ByteBuffer;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.utils.ByteBufferUtils;

public class WsClientEncoder {
  @SuppressWarnings("unused")
private static Logger log = LoggerFactory.getLogger(WsClientEncoder.class);

  private static final Random reuseableRandom = new Random();

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
      WsPacket packet, TioConfig tioConfig, ChannelContext channelContext) {
    byte[] wsBody = packet.getBody(); // 就是ws的body，不包括ws的头
    byte[][] wsBodies = packet.getBodys();
    int wsBodyLength = 0;
    if (wsBody != null) {
      wsBodyLength += wsBody.length;
    } else if (wsBodies != null) {
      for (byte[] bs : wsBodies) {
        wsBodyLength += bs.length;
      }
    }

    byte opcode = packet.getWsOpcode().getCode();
    byte b0 = (byte) (packet.isWsEof() ? -128 : 0);
    b0 |= opcode;

    byte maskedByte = (byte) -128;

    ByteBuffer buf;
    if (wsBodyLength < 126) {
      buf = ByteBuffer.allocate(2 + wsBodyLength + 4);
      buf.put(b0);
      buf.put((byte) (wsBodyLength | maskedByte));
    } else if (wsBodyLength < (1 << 16) - 1) {
      buf = ByteBuffer.allocate(4 + wsBodyLength + 4);
      buf.put(b0);
      buf.put((byte) (126 | maskedByte));
      ByteBufferUtils.writeUB2WithBigEdian(buf, wsBodyLength);
    } else {
      buf = ByteBuffer.allocate(10 + wsBodyLength + 4);
      buf.put(b0);
      buf.put((byte) (127 | maskedByte));

      buf.position(buf.position() + 4);

      ByteBufferUtils.writeUB4WithBigEdian(buf, wsBodyLength);
    }

    ByteBuffer maskkey = ByteBuffer.allocate(4);
    maskkey.putInt(reuseableRandom.nextInt());
    buf.put(maskkey.array());

    if (wsBody != null)
      for (int i = 0; i < wsBody.length; i++) {
        wsBody[i] = ((byte) (wsBody[i] ^ maskkey.get(i % 4)));
      }

    if (wsBody != null && wsBody.length > 0) {
      buf.put(wsBody);
    } else if (wsBodies != null) {
      for (byte[] bs : wsBodies) {
        buf.put(bs);
      }
    }

    return buf;
  }
}
