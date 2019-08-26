/**
 * 
 */
package org.tio.core.ssl;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * @author tanyaowu
 *
 */
public class SslVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2582637215518609443L;

	private ByteBuffer	byteBuffer	= null;
	/**
	 * List<Packet> or Packet
	 */
	private Object		obj			= null;

	public ByteBuffer getByteBuffer() {
		return byteBuffer;
	}

	public void setByteBuffer(ByteBuffer byteBuffer) {
		this.byteBuffer = byteBuffer;
	}

	public Object getObj() {
		return obj;
	}

	/**
	 * 
	 * @param byteBuffer
	 * @param obj List<Packet> or Packet
	 */
	public SslVo(ByteBuffer byteBuffer, Object obj) {
		this.byteBuffer = byteBuffer;
		this.obj = obj;
	}

	public SslVo() {
	}

	@Override
	public String toString() {
		return "SslVo [byteBuffer=" + byteBuffer + ", obj=" + obj + "]";
	}

}
