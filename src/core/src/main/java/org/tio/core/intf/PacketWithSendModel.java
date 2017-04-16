package org.tio.core.intf;

import org.tio.core.PacketSendMode;

/**
 * 
 * @author tanyaowu 
 * 2017年4月1日 上午9:34:59
 */
public class PacketWithSendModel
{
	private Packet packet = null;
	private PacketSendMode packetSendMode = null;
	private Boolean isSentSuccess = null;
	/**
	 * @param packet
	 * @param packetSendMode
	 * @author: tanyaowu
	 */
	public PacketWithSendModel(Packet packet, PacketSendMode packetSendMode)
	{
		super();
		this.packet = packet;
		this.packetSendMode = packetSendMode;
	}
	/**
	 * @return the packet
	 */
	public Packet getPacket()
	{
		return packet;
	}

	/**
	 * @return the packetSendMode
	 */
	public PacketSendMode getPacketSendMode()
	{
		return packetSendMode;
	}
	/**
	 * @return the isSentSuccess
	 */
	public Boolean getIsSentSuccess()
	{
		return isSentSuccess;
	}
	/**
	 * @param isSentSuccess the isSentSuccess to set
	 */
	public void setIsSentSuccess(Boolean isSentSuccess)
	{
		this.isSentSuccess = isSentSuccess;
	}

}
