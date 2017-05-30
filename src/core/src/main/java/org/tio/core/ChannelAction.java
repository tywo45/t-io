package org.tio.core;

/**
 * client action
 * @author tanyaowu 
 *
 */
public enum ChannelAction {
	/**
	 * 
	 */
	CONNECT(1),

	/**
	 * 
	 */
	RECEIVED(2),
	/**
	 * 
	 */
	BEFORE_SEND(3),

	/**
	 * 
	 */
	AFTER_SEND(4),

	/**
	 * 
	 */
	UNCONNECT(5),

	/**
	 * 重连
	 */
	RE_CONNECT(6),

	/**
	 * 
	 */
	BEFORE_HANDLER(7),
	/**
	 * 
	 */
	AFTER_HANDLER(8),

	/**
	 * 
	 */
	RECEIVED_BUF(9);

	private final int value;

	public static ChannelAction forNumber(int value) {
		switch (value) {
		case 1:
			return CONNECT;
		case 2:
			return RECEIVED;
		case 3:
			return BEFORE_SEND;
		case 4:
			return AFTER_SEND;
		case 5:
			return UNCONNECT;
		case 6:
			return RE_CONNECT;
		case 7:
			return BEFORE_HANDLER;
		case 8:
			return AFTER_HANDLER;
		case 9:
			return RECEIVED_BUF;
		default:
			return null;
		}
	}

	private ChannelAction(int value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
}
