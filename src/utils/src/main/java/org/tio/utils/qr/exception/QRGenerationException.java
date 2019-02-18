package org.tio.utils.qr.exception;

public class QRGenerationException extends RuntimeException {
	private static final long serialVersionUID = -905248026571279247L;

	public QRGenerationException(String message, Throwable underlyingException) {
		super(message, underlyingException);
	}
}
