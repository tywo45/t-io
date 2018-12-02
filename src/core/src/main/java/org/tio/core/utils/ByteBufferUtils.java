package org.tio.core.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.exception.LengthOverflowException;
import org.tio.utils.hutool.StrUtil;

/**
 * 
 * @author tanyaowu 
 * 2017年10月19日 上午9:41:00
 */
public class ByteBufferUtils {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ByteBufferUtils.class);

	/**
	 * 组合两个bytebuffer，把可读部分的组合成一个新的bytebuffer
	 * @param byteBuffer1
	 * @param byteBuffer2
	 * @return
	 * @author: tanyaowu
	 */
	public static ByteBuffer composite(ByteBuffer byteBuffer1, ByteBuffer byteBuffer2) {
		int capacity = byteBuffer1.remaining() + byteBuffer2.remaining();
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
	 * @param srcStartindex
	 * @param dest
	 * @param destStartIndex
	 * @param length
	 */
	public static void copy(ByteBuffer src, int srcStartindex, ByteBuffer dest, int destStartIndex, int length) {
		System.arraycopy(src.array(), srcStartindex, dest.array(), destStartIndex, length);
	}

	/**
	 *
	 * @param src 本方法不会改变position等指针变量
	 * @param startindex 从0开始
	 * @param endindex
	 * @return
	 *
	 * @author: tanyaowu
	 *
	 */
	public static ByteBuffer copy(ByteBuffer src, int startindex, int endindex) {
		int size = endindex - startindex;
		int initPosition = src.position();
		int initLimit = src.limit();

		src.position(startindex);
		src.limit(endindex);
		ByteBuffer ret = ByteBuffer.allocate(size);
		ret.put(src);
		ret.flip();

		src.position(initPosition);
		src.limit(initLimit);
		return ret;
	}

	/**
	 * 
	 * @param src 本方法不会改变position等指针变量
	 * @return
	 * @author tanyaowu
	 */
	public static ByteBuffer copy(ByteBuffer src) {
		int startindex = src.position();
		int endindex = src.limit();
		return copy(src, startindex, endindex);
	}

	/**
	 * 
	 * @param src
	 * @param unitSize 每个单元的大小
	 * @return 如果不需要拆分，则返回null
	 */
	public static ByteBuffer[] split(ByteBuffer src, int unitSize) {
		int limit = src.limit();
		if (unitSize >= limit) {
			return null;//new ByteBuffer[] { src };
		}

		//		return null;

		int size = (int) (Math.ceil((double) src.limit() / (double) unitSize));
		ByteBuffer[] ret = new ByteBuffer[size];
		int srcIndex = 0;
		for (int i = 0; i < size; i++) {
			int bufferSize = unitSize;
			if (i == size - 1) {
				bufferSize = src.limit() % unitSize;
			}

			byte[] dest = new byte[bufferSize];
			System.arraycopy(src.array(), srcIndex, dest, 0, dest.length);
			srcIndex = srcIndex + bufferSize;

			ret[i] = ByteBuffer.wrap(dest);
			ret[i].position(0);
			ret[i].limit(ret[i].capacity());
		}

		return ret;
	}

	//	public static Packet[] split(Packet packet, int unitSize) {
	//		
	//	}

	/**
	 * 
	 * @param buffer
	 * @return
	 * @throws LengthOverflowException
	 * @author tanyaowu
	 */
	public static int lineEnd(ByteBuffer buffer) throws LengthOverflowException {
		return lineEnd(buffer, Integer.MAX_VALUE);
	}

	/**
	 * 
	 * @param buffer
	 * @param maxlength
	 * @return
	 * @throws LengthOverflowException
	 * @author tanyaowu
	 */
	public static int lineEnd(ByteBuffer buffer, int maxlength) throws LengthOverflowException {
		int initPosition = buffer.position();
		int endPosition = indexOf(buffer, '\n', maxlength);
		if ((endPosition - initPosition > 0) && (buffer.get(endPosition - 1) == '\r')) {
			return endPosition - 1;
		}
		return endPosition;
	}

	/**
	 * 
	 * @param buffer position会被移动
	 * @param theChar 结束
	 * @param maxlength
	 * @return
	 * @throws LengthOverflowException
	 * @author tanyaowu
	 */
	public static int indexOf(ByteBuffer buffer, char theChar, int maxlength) throws LengthOverflowException {
		int count = 0;
		boolean needJudgeLengthOverflow = buffer.remaining() > maxlength;
		while (buffer.hasRemaining()) {
			if (buffer.get() == theChar) {
				return buffer.position() - 1;
			}
			if (needJudgeLengthOverflow) {
				count++;
				if (count > maxlength) {
					throw new LengthOverflowException("maxlength is " + maxlength);
				}
			}
		}
		return -1;
	}

	public static byte[] readBytes(ByteBuffer buffer, int length) {
		byte[] ab = new byte[length];
		buffer.get(ab);
		return ab;
	}

	/**
	 * 
	 * @param buffer
	 * @param length
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String readString(ByteBuffer buffer, int length, String charset) throws UnsupportedEncodingException {
		byte[] bs = readBytes(buffer, length);
		if (StrUtil.isNotBlank(charset)) {
			return new String(bs, charset);
		}
		return new String(bs);
	}

	/**
	 *
	 * @param buffer
	 * @param charset
	 * @return
	 * @author: tanyaowu
	 */
	public static String readLine(ByteBuffer buffer, String charset) throws LengthOverflowException {
		return readLine(buffer, charset, Integer.MAX_VALUE);
	}

	/**
	 *
	 * @param buffer
	 * @param charset
	 * @param maxlength
	 * @return
	 * @author: tanyaowu
	 */
	public static String readLine(ByteBuffer buffer, String charset, Integer maxlength) throws LengthOverflowException {
		//		boolean canEnd = false;
		int startPosition = buffer.position();
		int endPosition = lineEnd(buffer, maxlength);
		if (endPosition == -1) {
			return null;
		}

		int nowPosition = buffer.position();

		if (endPosition > startPosition) {
			byte[] bs = new byte[endPosition - startPosition];
			buffer.position(startPosition);
			buffer.get(bs);
			buffer.position(nowPosition);
			if (StrUtil.isNotBlank(charset)) {
				try {
					return new String(bs, charset);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			} else {
				return new String(bs);
			}
		} else if (endPosition == startPosition) {
			return "";
		}
		return null;
	}

	/**
	 * 
	 * @param buffer
	 * @param charset
	 * @param endChar
	 * @param maxlength
	 * @return
	 * @throws LengthOverflowException
	 * @author tanyaowu
	 */
	public static String readString(ByteBuffer buffer, String charset, char endChar, Integer maxlength) throws LengthOverflowException {
		//		boolean canEnd = false;
		int startPosition = buffer.position();
		int endPosition = indexOf(buffer, endChar, maxlength);
		if (endPosition == -1) {
			return null;
		}

		int nowPosition = buffer.position();
		if (endPosition > startPosition) {
			byte[] bs = new byte[endPosition - startPosition];
			buffer.position(startPosition);
			buffer.get(bs);
			buffer.position(nowPosition);
			if (StrUtil.isNotBlank(charset)) {
				try {
					return new String(bs, charset);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			} else {
				return new String(bs);
			}
		} else if (endPosition == startPosition) {
			return "";
		}
		return null;
	}

	public static int readUB1(ByteBuffer buffer) {
		int ret = buffer.get() & 0xff;
		return ret;
	}

	public static int readUB2(ByteBuffer buffer) {
		int ret = buffer.get() & 0xff;
		ret |= (buffer.get() & 0xff) << 8;
		return ret;
	}

	public static int readUB2WithBigEdian(ByteBuffer buffer) {
		int ret = (buffer.get() & 0xff) << 8;
		ret |= buffer.get() & 0xff;
		return ret;
	}

	public static long readUB4(ByteBuffer buffer) {
		long ret = buffer.get() & 0xff;
		ret |= (long) (buffer.get() & 0xff) << 8;
		ret |= (long) (buffer.get() & 0xff) << 16;
		ret |= (long) (buffer.get() & 0xff) << 24;
		return ret;
	}

	public static long readUB4WithBigEdian(ByteBuffer buffer) {
		long ret = (long) (buffer.get() & 0xff) << 24;
		ret |= (long) (buffer.get() & 0xff) << 16;
		ret |= (long) (buffer.get() & 0xff) << 8;
		ret |= buffer.get() & 0xff;

		return ret;
	}

	public static final void writeUB2(ByteBuffer buffer, int i) {
		buffer.put((byte) (i & 0xff));
		buffer.put((byte) (i >>> 8));
	}

	public static final void writeUB2WithBigEdian(ByteBuffer buffer, int i) {
		buffer.put((byte) (i >>> 8));
		buffer.put((byte) (i & 0xff));
	}

	public static final void writeUB4(ByteBuffer buffer, long l) {
		buffer.put((byte) (l & 0xff));
		buffer.put((byte) (l >>> 8));
		buffer.put((byte) (l >>> 16));
		buffer.put((byte) (l >>> 24));
	}

	public static final void writeUB4WithBigEdian(ByteBuffer buffer, long l) {
		buffer.put((byte) (l >>> 24));
		buffer.put((byte) (l >>> 16));
		buffer.put((byte) (l >>> 8));
		buffer.put((byte) (l & 0xff));
	}
}
