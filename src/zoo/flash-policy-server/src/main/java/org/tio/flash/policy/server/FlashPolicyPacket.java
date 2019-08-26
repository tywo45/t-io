package org.tio.flash.policy.server;

import org.tio.core.intf.Packet;

/**
 *
 * @author tanyaowu
 *
 */
public class FlashPolicyPacket extends Packet {
	private static final long	serialVersionUID	= -172060606924066412L;
	public static final int		MIN_LENGHT			= 22;					//消息最少的长度
	public static final int		MAX_LING_LENGHT		= 256;					//一行最大的长度

	public static final FlashPolicyPacket REQUEST = new FlashPolicyPacket();

	public static final FlashPolicyPacket RESPONSE = new FlashPolicyPacket();

	private FlashPolicyPacket() {
	}

}
