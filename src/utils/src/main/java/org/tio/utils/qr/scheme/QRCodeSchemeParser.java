package org.tio.utils.qr.scheme;

import java.io.UnsupportedEncodingException;
import java.util.Set;

/**
 * Common interface for classes that can interpret certain QR code text into
 * content types like e.g. {@link Wifi}. TODO: does this parser stuff make sense
 * at all?
 */
public interface QRCodeSchemeParser {

	/**
	 * Tries to parse the given QR code text.
	 * 
	 * @param qrCodeText
	 *            the qrcode text to interpret
	 * @return the interpreted type
	 * @throws UnsupportedEncodingException
	 *             if the code is not supported by this interpreter.
	 */
	Object parse(final String qrCodeText) throws UnsupportedEncodingException;

	Set<Class<? extends Schema>> getSupportedSchemes();
}
