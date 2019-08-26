package org.tio.core.ssl.facade;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.net.ssl.SSLException;

import org.tio.core.ssl.SslVo;

public interface ISSLFacade {
	void setHandshakeCompletedListener(IHandshakeCompletedListener hcl);

	void setSSLListener(ISSLListener l);

	void setCloseListener(ISessionClosedListener l);

	/**
	 * 开始握手
	 * @throws IOException
	 */
	void beginHandshake() throws IOException;

	/**
	 * SSL握手是否已经完成
	 * @return
	 */
	boolean isHandshakeCompleted();

	/**
	 * 加密
	 * @param sslVo
	 * @throws SSLException
	 */
	void encrypt(SslVo sslVo) throws SSLException;

	/**
	 * 解密
	 * @param byteBuffer
	 * @throws SSLException
	 */
	void decrypt(ByteBuffer byteBuffer) throws SSLException;

	void close();

	boolean isCloseCompleted();

	boolean isClientMode();

	void terminate();
}
