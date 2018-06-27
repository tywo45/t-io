package org.tio.core;

/**
 * 同步消息 action
 * @author tanyaowu
 *
 */
public enum SynPacketAction {
	/**
	 *
	 */
	BEFORE_WAIT(1),

	/**
	 *
	 */
	AFTER__WAIT(2),

	/**
	 *
	 */
	BEFORE_DOWN(3);

	public static SynPacketAction forNumber(int value) {
		switch (value) {
		case 1:
			return BEFORE_WAIT;
		case 2:
			return AFTER__WAIT;
		case 3:
			return BEFORE_DOWN;
		default:
			return null;
		}
	}

	private final int value;

	private SynPacketAction(int value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
}
