package org.tio.core.ssl.facade;

import java.nio.ByteBuffer;

import org.tio.core.ssl.SslVo;

public interface ISSLListener {
	/**
	 * 业务层通过这个方法把SSL加密后的数据发出去
	 * @param sslVo
	 */
	public void onWrappedData(SslVo sslVo);

	/**
	 * 业务层通过这个方法把SSL解密后的数据进行业务解包
	 * @param plainBuffer
	 */
	public void onPlainData(ByteBuffer plainBuffer);
}
