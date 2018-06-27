package org.tio.websocket.common;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.utils.ByteBufferUtils;

/**
 * 参考了baseio: https://gitee.com/generallycloud/baseio
 * com.generallycloud.nio.codec.http11.WebSocketProtocolEncoder
 * @author tanyaowu
 *
 */
public class WsServerEncoder {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(WsServerEncoder.class);

	public static final int MAX_HEADER_LENGTH = 20480;

	private static void checkLength(byte[] bytes, int length, int offset) {
		if (bytes == null) {
			throw new IllegalArgumentException("null");
		}

		if (offset < 0) {
			throw new IllegalArgumentException("invalidate offset " + offset);
		}

		if (bytes.length - offset < length) {
			throw new IllegalArgumentException("invalidate length " + bytes.length);
		}
	}

	public static ByteBuffer encode(WsResponse wsResponse, GroupContext groupContext, ChannelContext channelContext) {
		byte[] imBody = wsResponse.getBody();//就是ws的body，不包括ws的头
		int wsBodyLength = 0;

		if (imBody != null) {
			wsBodyLength += imBody.length;
		}

		byte header0 = (byte) (0x8f & (wsResponse.getWsOpcode().getCode() | 0xf0));
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
			buf.put(new byte[] { 0, 0, 0, 0 });
			ByteBufferUtils.writeUB4WithBigEdian(buf, wsBodyLength);
		}

		if (imBody != null && imBody.length > 0) {
			buf.put(imBody);
		}

		return buf;
	}

	public static void int2Byte(byte[] bytes, int value, int offset) {
		checkLength(bytes, 4, offset);

		bytes[offset + 3] = (byte) (value & 0xff);
		bytes[offset + 2] = (byte) (value >> 8 * 1 & 0xff);
		bytes[offset + 1] = (byte) (value >> 8 * 2 & 0xff);
		bytes[offset + 0] = (byte) (value >> 8 * 3);
	}

	/**
	 *
	 * @author tanyaowu
	 * 2017年2月22日 下午4:06:42
	 *
	 */
	public WsServerEncoder() {

	}
}
