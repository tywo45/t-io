package org.tio.core.exception;

/**
 * 
 * @author tanyaowu 
 * 2017年4月1日 上午9:33:24
 */
public class LengthOverflowException extends java.lang.Throwable {

	private static final long serialVersionUID = 5231789012657669073L;

	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 
	 */
	public LengthOverflowException() {
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 *
	 * @author: tanyaowu
	 * 
	 */
	public LengthOverflowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

	/**
	 * @param message
	 * @param cause
	 *
	 * @author: tanyaowu
	 * 
	 */
	public LengthOverflowException(String message, Throwable cause) {
		super(message, cause);

	}

	/**
	 * @param message
	 *
	 * @author: tanyaowu
	 * 
	 */
	public LengthOverflowException(String message) {
		super(message);

	}

	/**
	 * @param cause
	 *
	 * @author: tanyaowu
	 * 
	 */
	public LengthOverflowException(Throwable cause) {
		super(cause);

	}

}
