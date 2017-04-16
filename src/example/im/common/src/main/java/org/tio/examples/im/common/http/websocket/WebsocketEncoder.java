package org.tio.examples.im.common.http.websocket;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.http.websocket.WebsocketPacket.Opcode;
import org.tio.examples.im.common.packets.Command;

/**
 * 参考了baseio: https://git.oschina.net/generallycloud/baseio
 * com.generallycloud.nio.codec.http11.WebSocketProtocolEncoder
 * @author tanyaowu 
 *
 */
public class WebsocketEncoder
{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(WebsocketEncoder.class);

	/**
	 * 
	 * @author: tanyaowu
	 * 2017年2月22日 下午4:06:42
	 * 
	 */
	public WebsocketEncoder()
	{

	}

	public static final int MAX_HEADER_LENGTH = 20480;

	public static ByteBuffer encode(ImPacket imPacket, GroupContext<ImSessionContext, ImPacket, Object> groupContext,
			ChannelContext<ImSessionContext, ImPacket, Object> channelContext)
	{
		byte[] websocketHeader;
		byte[] imBody = imPacket.getBody();
		int wsBodyLength = 1; //固定有一个命令码，占一位
		if (imBody != null)
		{
			wsBodyLength += imBody.length;
		}

		byte header0 = (byte) (0x8f & (Opcode.BINARY.getCode() | 0xf0));

		if (wsBodyLength < 126)
		{
			websocketHeader = new byte[2];
			websocketHeader[0] = header0;
			websocketHeader[1] = (byte) wsBodyLength;
		} else if (wsBodyLength < ((1 << 16) - 1))
		{
			websocketHeader = new byte[4];
			websocketHeader[0] = header0;
			websocketHeader[1] = 126;
			websocketHeader[3] = (byte) (wsBodyLength & 0xff);
			websocketHeader[2] = (byte) ((wsBodyLength >> 8) & 0x80);
		} else
		{
			websocketHeader = new byte[6];
			websocketHeader[0] = header0;
			websocketHeader[1] = 127;
			int2Byte(websocketHeader, wsBodyLength, 2);
		}
		ByteBuffer buf = ByteBuffer.allocate(websocketHeader.length + wsBodyLength);
		buf.put(websocketHeader);

		Command command = imPacket.getCommand();
		buf.put((byte) command.getNumber());

		if (imBody != null)
		{
			buf.put(imBody);
		}

		return buf;
	}

	public static void int2Byte(byte[] bytes, int value, int offset)
	{
		checkLength(bytes, 4, offset);

		bytes[offset + 3] = (byte) ((value & 0xff));
		bytes[offset + 2] = (byte) ((value >> 8 * 1) & 0xff);
		bytes[offset + 1] = (byte) ((value >> 8 * 2) & 0xff);
		bytes[offset + 0] = (byte) ((value >> 8 * 3));
	}

	private static void checkLength(byte[] bytes, int length, int offset)
	{
		if (bytes == null)
		{
			throw new IllegalArgumentException("null");
		}

		if (offset < 0)
		{
			throw new IllegalArgumentException("invalidate offset " + offset);
		}

		if (bytes.length - offset < length)
		{
			throw new IllegalArgumentException("invalidate length " + bytes.length);
		}
	}

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2017年2月22日 下午4:06:42
	 * 
	 */
	public static void main(String[] args)
	{

	}

}
