package org.tio.examples.im.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.intf.Packet;
import org.tio.examples.im.common.packets.Command;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 
 * 1：版本号
 * 2：消息类型(command)
 * 3：是否压缩，1压缩，0未压缩
 * 4-7：同步序号
 * 8-9：消息体长度
 * 
 * @author tanyaowu 
 *
 */
public class ImPacket extends Packet
{

	private static Logger log = LoggerFactory.getLogger(ImPacket.class);

	/**
	 * 心跳字节
	 */
	public static final byte HEARTBEAT_BYTE = -128;
	
	/**
	 * 握手字节
	 */
	public static final byte HANDSHAKE_BYTE = -127;

	/**
	 * 协议版本号
	 */
	public final static byte VERSION = 1;

	/**
	 * 消息体最多为多少
	 */
	public static final int MAX_LENGTH_OF_BODY = (int) (1024 * 1024 * 2.1); //只支持多少M数据

	/**
	 * 消息头最少为多少个字节
	 */
	public static final int LEAST_HEADER_LENGHT = 9;//1+1+2 + (2+4)

	

	

	private byte[] body;

	public ImPacket(byte[] body, Command command)
	{
		super();
		this.body = body;
		this.setCommand(command);
	}

	public ImPacket(Command command)
	{
		super();
		this.setCommand(command);
	}

	public ImPacket()
	{

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		
	}

	@JSONField(serialize = false)
//	private int bodyLen;

	private Command command;

//	public int getBodyLen()
//	{
//		return bodyLen;
//	}

	public Command getCommand()
	{
		return command;
	}

	//	public final static AtomicInteger seq = new AtomicInteger();
	//
	//	private Integer seqNo = null;
	//
	//	@Override
	//	public String getSeqNo()
	//	{
	//		if (this.seqNo == null)
	//		{
	//			return null;
	//		}
	//		return String.valueOf(this.seqNo);
	//	}
	//
	//	@Override
	//	public void setSeqNo(String seqNo)
	//	{
	//		this.seqNo = seqNo;
	//	}

//	public void setBodyLen(int bodyLen)
//	{
//		this.bodyLen = bodyLen;
//	}

	public void setCommand(Command type)
	{
		this.command = type;
		//		if (com.talent.im.common.command.Command.COMMAND_PRIORITY_HANDLER_COMMANDS.contains(type))
		//		{
		//			this.setPriority(PRIORITY_MAX);
		//		}
	}

	/**
	 * @return the body
	 */
	public byte[] getBody()
	{
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(byte[] body)
	{
		this.body = body;
	}

	/** 
	 * @see org.tio.core.intf.Packet#logstr()
	 * 
	 * @return
	 * @author: tanyaowu
	 * 2017年2月22日 下午3:15:18
	 * 
	 */
	@Override
	public String logstr()
	{
		return this.command.name();
	}

//	/**
//	 * @return the isCompress
//	 */
//	public boolean isCompress()
//	{
//		return isCompress;
//	}
//
//	/**
//	 * @param isCompress the isCompress to set
//	 */
//	public void setCompress(boolean isCompress)
//	{
//		this.isCompress = isCompress;
//	}

//	/**
//	 * @return the hasSynSeq
//	 */
//	public boolean isHasSynSeq()
//	{
//		return hasSynSeq;
//	}
//
//	/**
//	 * @param hasSynSeq the hasSynSeq to set
//	 */
//	public void setHasSynSeq(boolean hasSynSeq)
//	{
//		this.hasSynSeq = hasSynSeq;
//	}

//	/**
//	 * @return the is4byteLength
//	 */
//	public boolean isIs4ByteLength()
//	{
//		return is4ByteLength;
//	}
//
//	/**
//	 * @param is4ByteLength the is4byteLength to set
//	 */
//	public void setIs4byteLength(boolean is4ByteLength)
//	{
//		this.is4ByteLength = is4ByteLength;
//	}

}
