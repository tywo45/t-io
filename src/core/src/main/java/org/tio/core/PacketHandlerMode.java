package org.tio.core;

/**
 *  消息处理模式
 * @author tanyaowu 
 *
 */
public enum PacketHandlerMode {
	/**
	 * 处理消息与解码在同一个线程中处理
	 */
	SINGLE_THREAD(1),

	/**
	 * 把packet丢到一个队列中，让线程池去处理
	 */
	QUEUE(2);

	private final int value;

	public static PacketHandlerMode forNumber(int value) {
		switch (value) {
		case 1:
			return SINGLE_THREAD;
		case 2:
			return QUEUE;
		default:
			return null;
		}
	}

	private PacketHandlerMode(int value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
}
