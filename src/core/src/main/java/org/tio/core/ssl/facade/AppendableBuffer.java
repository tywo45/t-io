package org.tio.core.ssl.facade;

import java.nio.ByteBuffer;

class AppendableBuffer {
	private ByteBuffer b;

	public ByteBuffer append(ByteBuffer data) {

		int size = data.limit();
		if (b != null) {
			size += b.capacity();
		}

		ByteBuffer nb = ByteBuffer.allocate(size);
		if (b != null) {
			nb.put(b);
			clear();
		}
		nb.put(data);
		return nb;
	}

	/**
	 * 把
	 * @param byteBuffer
	 */
	public void set(ByteBuffer byteBuffer) {
		if (byteBuffer.hasRemaining()) {
			b = ByteBuffer.allocate(byteBuffer.remaining());
			b.put(byteBuffer);
			b.rewind();
		}
	}

	public void clear() {
		b = null;
	}

	public boolean hasRemaining() {
		if (b != null) {
			return b.hasRemaining();
		}
		return false;
	}

	public ByteBuffer get() {
		return b;
	}
}
