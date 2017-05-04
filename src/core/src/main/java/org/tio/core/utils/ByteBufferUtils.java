package org.tio.core.utils;

import java.nio.ByteBuffer;

public class ByteBufferUtils {
	/**
	 * 
	 * @param byteBuffer1
	 * @param byteBuffer2
	 * @return
	 * @author: tanyaowu
	 */
	public static ByteBuffer composite(ByteBuffer byteBuffer1, ByteBuffer byteBuffer2) {
		int capacity = (byteBuffer1.limit() - byteBuffer1.position()) + (byteBuffer2.limit() - byteBuffer2.position());
		ByteBuffer ret = ByteBuffer.allocate(capacity);

		ret.put(byteBuffer1);
		ret.put(byteBuffer2);

		ret.position(0);
		ret.limit(ret.capacity());
		return ret;
	}

	/**
	 * 
	 * @param src
	 * @param startindex 从0开始
	 * @param endindex 
	 * @return
	 *
	 * @author: tanyaowu
	 *
	 */
	public static ByteBuffer copy(ByteBuffer src, int startindex, int endindex) {
		int size = endindex - startindex;
		byte[] dest = new byte[size];
		System.arraycopy(src.array(), startindex, dest, 0, dest.length);
		ByteBuffer newByteBuffer = ByteBuffer.wrap(dest);
		return newByteBuffer;
	}

	public static void copy(ByteBuffer src, int srcStartindex, ByteBuffer dest, int destStartIndex, int length) {
		System.arraycopy(src.array(), srcStartindex, dest.array(), destStartIndex, length);
	}
	//	public static byte read(ByteBuffer buffer)
	//	{
	//		return buffer.get();
	//	}

	public static int readUB2(ByteBuffer buffer) {
		int ret = buffer.get() & 0xff;
		ret |= (buffer.get() & 0xff) << 8;
		return ret;
	}

	//	public static int readUB3(ByteBuffer buffer)
	//	{
	//		int ret = buffer.get() & 0xff;
	//		ret |= (buffer.get() & 0xff) << 8;
	//		ret |= (buffer.get() & 0xff) << 16;
	//		return ret;
	//	}

	public static long readUB4(ByteBuffer buffer) {
		long ret = buffer.get() & 0xff;
		ret |= (long) (buffer.get() & 0xff) << 8;
		ret |= (long) (buffer.get() & 0xff) << 16;
		ret |= (long) (buffer.get() & 0xff) << 24;
		return ret;
	}

	public static byte[] readBytes(ByteBuffer buffer, int length) {
		byte[] ab = new byte[length];
		buffer.get(ab);
		return ab;
	}

	public static final void writeUB2(ByteBuffer buffer, int i) {
		buffer.put((byte) (i & 0xff));
		buffer.put((byte) (i >>> 8));
	}

	//	public static final void writeUB3(ByteBuffer buffer, int i)
	//	{
	//		buffer.put((byte) (i & 0xff));
	//		buffer.put((byte) (i >>> 8));
	//		buffer.put((byte) (i >>> 16));
	//	}

	public static final void writeUB4(ByteBuffer buffer, long l) {
		buffer.put((byte) (l & 0xff));
		buffer.put((byte) (l >>> 8));
		buffer.put((byte) (l >>> 16));
		buffer.put((byte) (l >>> 24));
	}
}
