package org.tio.core.intf;

/**
 * 
 * @author tanyaowu 
 * 2017年4月1日 上午9:34:42
 */
public class EncodedPacket extends Packet {

	private byte[] bytes;

	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 
	 */
	public EncodedPacket(byte[] bytes) {
		this.bytes = bytes;
	}

	/**
	 * @return the bytes
	 */
	public byte[] getBytes() {
		return bytes;
	}

	/**
	 * @param bytes the bytes to set
	 */
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

}
