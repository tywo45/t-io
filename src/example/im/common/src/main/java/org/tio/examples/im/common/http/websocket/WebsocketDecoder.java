package org.tio.examples.im.common.http.websocket;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.utils.ByteBufferUtils;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.http.websocket.WebsocketPacket.Opcode;

/**
 * 参考了baseio: https://git.oschina.net/generallycloud/baseio
 * com.generallycloud.nio.codec.http11.future.WebSocketReadFutureImpl
 * @author tanyaowu 
 *
 */
public class WebsocketDecoder {
	private static Logger log = LoggerFactory.getLogger(WebsocketDecoder.class);

	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2017年2月22日 下午4:06:42
	 * 
	 */
	public WebsocketDecoder() {

	}

	//	public static final int MAX_HEADER_LENGTH = 20480;

	public static WebsocketPacket decode(ByteBuffer buf, ChannelContext<ImSessionContext, ImPacket, Object> channelContext) throws AioDecodeException {
		//第一阶段解析
		int initPosition = buf.position();
		int readableLength = buf.limit() - initPosition;

		int headLength = WebsocketPacket.MINIMUM_HEADER_LENGTH;

		if (readableLength < headLength) {
			return null;
		}

		byte b = buf.get();
		boolean eof = ((b & 0xFF) >> 7) == 1;
		if (!eof) {
			log.error("不是最后一帖{}", channelContext);
		}
		Opcode opcode = Opcode.valueOf((byte) (b & 0xF));
		b = buf.get();
		boolean hasMask = ((b & 0xFF) >> 7) == 1;
		if (hasMask) {
			headLength += 4;
		}

		int bodyLength = (b & 0x7f);
		if (bodyLength == 126) {
			headLength += 2;
		} else if (bodyLength > 126) {
			headLength += 4;
		}

		//第二阶段解析
		if (readableLength < headLength) {
			return null;
		}

		if (bodyLength == 126) {
			bodyLength = ByteBufferUtils.readUB2(buf);//buf.getUnsignedShort();
		} else if (bodyLength > 126) {
			bodyLength = (int) ByteBufferUtils.readUB4(buf);//buf.getUnsignedInt();
		}

		if (bodyLength < 0 || bodyLength > WebsocketPacket.MAX_BODY_LENGTH) {
			throw new AioDecodeException("body length(" + bodyLength + ") is not right");
		}

		byte[] mask = null;
		if (hasMask) {
			mask = ByteBufferUtils.readBytes(buf, 4);
		}

		//第三阶段解析
		if (readableLength < (headLength + bodyLength)) {
			return null;
		}
		WebsocketPacket websocketPacket = new WebsocketPacket();
		websocketPacket.setWsEof(eof);
		websocketPacket.setWsHasMask(hasMask);
		websocketPacket.setWsMask(mask);
		websocketPacket.setWsOpcode(opcode);
		websocketPacket.setWsBodyLength(bodyLength);

		if (bodyLength == 0) {
			return websocketPacket;
		}

		byte[] array = ByteBufferUtils.readBytes(buf, bodyLength);
		if (hasMask) {
			for (int i = 0; i < array.length; i++) {
				array[i] = (byte) (array[i] ^ mask[i % 4]);
			}
		}

		websocketPacket.setWsBody(array);
		String text = null;
		if (opcode == Opcode.BINARY) {
			//throw new AioDecodeException("暂时不支持binary");
		} else {
			try {
				text = new String(array, WebsocketPacket.CHARSET_NAME);
				websocketPacket.setWsBodyText(text);
			} catch (UnsupportedEncodingException e) {
				log.error(e.toString(), e);
			}
		}

		return websocketPacket;

	}

	public static enum Step {
		header, remain_header, data,
	}

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2017年2月22日 下午4:06:42
	 * 
	 */
	public static void main(String[] args) {

	}

}
