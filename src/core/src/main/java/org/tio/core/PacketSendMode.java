package org.tio.core;

/**
 *  消息发送模式
 * @author tanyaowu 
 *
 */
public enum PacketSendMode {
	/**
	 * 把packet丢到一个队列中，让线程池去发送
	 */
	QUEUE(1),
	/**
	 * 单条消息阻塞发送，直接发送成功，才折回
	 */
	SINGLE_BLOCK(2),

	/**
	 * 一群消息阻塞发送，直接发送成功，才折回
	 */
	GROUP_BLOCK(3);

	private final int value;

	public static PacketSendMode forNumber(int value) {
		switch (value) {
		case 1:
			return QUEUE;
		case 2:
			return SINGLE_BLOCK;
		case 3:
			return GROUP_BLOCK;
		default:
			return null;
		}
	}

	private PacketSendMode(int value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
}
