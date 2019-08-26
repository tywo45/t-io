package org.tio.websocket.common;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.utils.ByteBufferUtils;

public class WsClientDecoder {

  private static Logger log = LoggerFactory.getLogger(WsClientDecoder.class);

  public static WsResponse decode(ByteBuffer buf, ChannelContext channelContext)
      throws AioDecodeException {
    // 第一阶段解析
    int initPosition = buf.position();
    int readableLength = buf.limit() - initPosition;

    int headLength = WsPacket.MINIMUM_HEADER_LENGTH;

    if (readableLength < headLength) {
      return null;
    }

    byte first = buf.get();
    //		int b = first & 0xFF; //转换成32位
    boolean fin = (first & 0x80) > 0; // 得到第8位 10000000>0
    @SuppressWarnings("unused")
    int rsv = (first & 0x70) >>> 4; // 得到5、6、7 为01110000 然后右移四位为00000111
    byte opCodeByte = (byte) (first & 0x0F); // 后四位为opCode 00001111
    Opcode opcode = Opcode.valueOf(opCodeByte);
    if (opcode == Opcode.CLOSE) {
      //			Tio.remove(channelContext, "收到opcode:" + opcode);
      //			return null;
    }

    byte second = buf.get(); // 向后读取一个字节
    boolean hasMask =
        (second & 0xFF) >> 7
            == 1; // 用于标识PayloadData是否经过掩码处理。如果是1，Masking-key域的数据即是掩码密钥，用于解码PayloadData。客户端发出的数据帧需要进行掩码处理，所以此位是1。

    // Client data must be masked
    if (!hasMask) { // 第9为为mask,必须为1
      // throw new AioDecodeException("websocket client data must be masked");
    } else {
      headLength += 4;
    }
    int payloadLength = second & 0x7F; // 读取后7位  Payload legth，如果<126则payloadLength

    byte[] mask = null;
    if (payloadLength == 126) { // 为126读2个字节，后两个字节为payloadLength
      headLength += 2;
      if (readableLength < headLength) {
        return null;
      }
      payloadLength = ByteBufferUtils.readUB2WithBigEdian(buf);
      log.info("{} payloadLengthFlag: 126，payloadLength {}", channelContext, payloadLength);

    } else if (payloadLength == 127) { // 127读8个字节,后8个字节为payloadLength
      headLength += 8;
      if (readableLength < headLength) {
        return null;
      }

      payloadLength = (int) buf.getLong();
      log.info("{} payloadLengthFlag: 127，payloadLength {}", channelContext, payloadLength);
    }

    if (payloadLength < 0 ) {
      throw new AioDecodeException("body length(" + payloadLength + ") is not right");
    }

    if (readableLength < headLength + payloadLength) {
      return null;
    }

    if (hasMask) {
      mask = ByteBufferUtils.readBytes(buf, 4);
    }

    // 第二阶段解析
    WsResponse websocketPacket = new WsResponse();
    websocketPacket.setWsEof(fin);
    websocketPacket.setWsHasMask(hasMask);
    websocketPacket.setWsMask(mask);
    websocketPacket.setWsOpcode(opcode);
    websocketPacket.setWsBodyLength(payloadLength);

    if (payloadLength == 0) {
      return websocketPacket;
    }

    byte[] array = ByteBufferUtils.readBytes(buf, payloadLength);
    if (hasMask) {
      for (int i = 0; i < array.length; i++) {
        array[i] = (byte) (array[i] ^ mask[i % 4]);
      }
    }

    websocketPacket.setBody(array);
    return websocketPacket;
  }

  /** @author tanyaowu 2017年2月22日 下午4:06:42 */
  public WsClientDecoder() {}
}
