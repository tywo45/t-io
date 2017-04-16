package org.tio.examples.im.common.http.websocket;

import java.util.HashMap;
import java.util.Map;

import org.tio.examples.im.common.ImPacket;

/**
 * 参考了baseio: https://git.oschina.net/generallycloud/baseio
 * com.generallycloud.nio.codec.http11.future.WebSocketReadFutureImpl
 * @author tanyaowu 
 *
 */
public class WebsocketPacket extends ImPacket
{
	public static final int MINIMUM_HEADER_LENGTH = 2;

	public static final int MAX_BODY_LENGTH = 1024 * 1024 * 1024;

	public static final String CHARSET_NAME = "utf-8";

	public static enum Opcode
	{
		TEXT((byte) 1), BINARY((byte) 2), CLOSE((byte) 8), PING((byte) 9), PONG((byte) 10);

		private final byte code;

		public byte getCode()
		{
			return code;
		}

		private Opcode(byte code)
		{
			this.code = code;
		}

		private static final Map<Byte, Opcode> map = new HashMap<>();
		static
		{
			for (Opcode command : values())
			{
				map.put(command.getCode(), command);
			}
		}

		public static Opcode valueOf(byte code)
		{
			return map.get(code);
		}
	}

	private boolean wsEof;
	protected Opcode wsOpcode;
	private boolean wsHasMask;
	private int wsBodyLength;
	private byte[] wsMask;
	private byte[] wsBody;
	private String wsBodyText;  //当为文本时才有此字段
	
	/**
	 * @return the wsEof
	 */
	public boolean isWsEof()
	{
		return wsEof;
	}

	/**
	 * @param wsEof the wsEof to set
	 */
	public void setWsEof(boolean wsEof)
	{
		this.wsEof = wsEof;
	}

	/**
	 * @return the wsOpcode
	 */
	public Opcode getWsOpcode()
	{
		return wsOpcode;
	}

	/**
	 * @param wsOpcode the wsOpcode to set
	 */
	public void setWsOpcode(Opcode wsOpcode)
	{
		this.wsOpcode = wsOpcode;
	}

	/**
	 * @return the wsHasMask
	 */
	public boolean isWsHasMask()
	{
		return wsHasMask;
	}

	/**
	 * @param wsHasMask the wsHasMask to set
	 */
	public void setWsHasMask(boolean wsHasMask)
	{
		this.wsHasMask = wsHasMask;
	}

	/**
	 * @return the wsBodyLength
	 */
	public int getWsBodyLength()
	{
		return wsBodyLength;
	}

	/**
	 * @param wsBodyLength the wsBodyLength to set
	 */
	public void setWsBodyLength(int wsBodyLength)
	{
		this.wsBodyLength = wsBodyLength;
	}

	/**
	 * @return the wsMask
	 */
	public byte[] getWsMask()
	{
		return wsMask;
	}

	/**
	 * @param wsMask the wsMask to set
	 */
	public void setWsMask(byte[] wsMask)
	{
		this.wsMask = wsMask;
	}

	/**
	 * @return the wsBody
	 */
	public byte[] getWsBody()
	{
		return wsBody;
	}

	/**
	 * @param wsBody the wsBody to set
	 */
	public void setWsBody(byte[] wsBody)
	{
		this.wsBody = wsBody;
	}

	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2017年2月22日 下午4:14:40
	 * 
	 */
	public WebsocketPacket()
	{
	}

	/**
	 * @return the wsBodyText
	 */
	public String getWsBodyText()
	{
		return wsBodyText;
	}

	/**
	 * @param wsBodyText the wsBodyText to set
	 */
	public void setWsBodyText(String wsBodyText)
	{
		this.wsBodyText = wsBodyText;
	}

}
