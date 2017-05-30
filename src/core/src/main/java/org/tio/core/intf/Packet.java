package org.tio.core.intf;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @author tanyaowu 
 * 2017年4月1日 上午9:34:59
 */
public class Packet {
	private static final AtomicLong ID_ATOMICLONG = new AtomicLong();

	private Long id = ID_ATOMICLONG.incrementAndGet();

	private int byteCount = 0;

	private Long respId = null;

	private PacketListener packetListener;

	private boolean isBlockSend = false;

	/**
	 * 同步发送时，需要的同步序列号
	 */
	private Integer synSeq = 0;

	/**
	 * 预编码过的bytebuffer，如果此值不为null，框架则会忽略原来的encode()而直接用此值
	 */
	private ByteBuffer preEncodedByteBuffer = null;

	/**
	 * @return the synSeq
	 */
	public Integer getSynSeq() {
		return synSeq;
	}

	/**
	 * @param synSeq the synSeq to set
	 */
	public void setSynSeq(Integer synSeq) {
		this.synSeq = synSeq;
	}

	/**
	 * @return the preEncodedByteBuffer
	 */
	public ByteBuffer getPreEncodedByteBuffer() {
		return preEncodedByteBuffer;
	}

	/**
	 * @param preEncodedByteBuffer the preEncodedByteBuffer to set
	 */
	public void setPreEncodedByteBuffer(ByteBuffer preEncodedByteBuffer) {
		this.preEncodedByteBuffer = preEncodedByteBuffer;
	}

	public String logstr() {
		return "";
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the respId
	 */
	public Long getRespId() {
		return respId;
	}

	/**
	 * @param respId the respId to set
	 */
	public void setRespId(Long respId) {
		this.respId = respId;
	}

	/**
	 * @return the packetListener
	 */
	public PacketListener getPacketListener() {
		return packetListener;
	}

	/**
	 * @param packetListener the packetListener to set
	 */
	public void setPacketListener(PacketListener packetListener) {
		this.packetListener = packetListener;
	}

	/**
	 * @return the isBlockSend
	 */
	public boolean isBlockSend() {
		return isBlockSend;
	}

	/**
	 * @param isBlockSend the isBlockSend to set
	 */
	public void setBlockSend(boolean isBlockSend) {
		this.isBlockSend = isBlockSend;
	}

	/**
	 * @return the byteCount
	 */
	public int getByteCount() {
		return byteCount;
	}

	/**
	 * @param byteCount the byteCount to set
	 */
	public void setByteCount(int byteCount) {
		this.byteCount = byteCount;
	}

}
