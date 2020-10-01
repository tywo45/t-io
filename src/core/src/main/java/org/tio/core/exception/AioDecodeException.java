/*
 * 使用本软件请从杭州钛特云有限公司获取授权，其它途径获取本软件的行为皆为侵权行为
 */
package org.tio.core.exception;

/**
 * @deprecated 用TioDecodeException
 * @author tanyaowu
 * 2017年4月1日 上午9:33:24
 */
public class AioDecodeException extends TioDecodeException {
	private static final long serialVersionUID = -8207465969738755041L;

	/**
	 *
	 *
	 * @author tanyaowu
	 *
	 */
	public AioDecodeException() {
	}

	/**
	 * @param message
	 *
	 * @author tanyaowu
	 *
	 */
	public AioDecodeException(String message) {
		super(message);

	}

	/**
	 * @param message
	 * @param cause
	 *
	 * @author tanyaowu
	 *
	 */
	public AioDecodeException(String message, Throwable cause) {
		super(message, cause);

	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 *
	 * @author tanyaowu
	 *
	 */
	public AioDecodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

	/**
	 * @param cause
	 *
	 * @author tanyaowu
	 *
	 */
	public AioDecodeException(Throwable cause) {
		super(cause);

	}

}
