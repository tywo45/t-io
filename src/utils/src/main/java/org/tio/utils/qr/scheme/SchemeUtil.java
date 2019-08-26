package org.tio.utils.qr.scheme;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A bunch of utility methods for parsing the QR codes.
 */
public class SchemeUtil {

	public static final String	LINE_FEED					= "\n";
	public static final String	DEFAULT_PARAM_SEPARATOR		= "\r?\n";
	public static final String	DEFAULT_KEY_VALUE_SEPARATOR	= ":";

	/**
	 * Parses the given string into a key/value map. Uses a colon as key/value
	 * separator.
	 * 
	 * @param qrCode
	 *            the qrCode to split into key/value pairs.
	 * @param paramSeparator
	 *            the string that splits the parameters
	 * @return the parsed key/value map
	 */
	public static Map<String, String> getParameters(final String qrCode, final String paramSeparator) {
		return getParameters(qrCode, paramSeparator, DEFAULT_KEY_VALUE_SEPARATOR);
	}

	/**
	 * Parses the given string into a key/value map. Uses a line feed as
	 * parameter separator, and a colon as key/value separator.
	 * 
	 * @param qrCode
	 *            the qrCode to split into key/value pairs.
	 * @return the parsed key/value map
	 */
	public static Map<String, String> getParameters(final String qrCode) {
		return getParameters(qrCode, DEFAULT_PARAM_SEPARATOR, ":");
	}

	/**
	 * Parses the given string into a key/value map
	 * 
	 * @param qrCode
	 *            the qrCode to split into key/value pairs.
	 * @param paramSeparator
	 *            the string that splits the parameters
	 * @param keyValueSeparator
	 *            the string that splits the key/value pairs
	 * @return the parsed key/value map
	 */
	public static Map<String, String> getParameters(final String qrCode, final String paramSeparator, final String keyValueSeparator) {
		Map<String, String> result = new LinkedHashMap<String, String>();
		String[] parts = qrCode.split(paramSeparator);
		for (int i = 0; i < parts.length; i++) {
			String[] param = parts[i].split(keyValueSeparator);
			if (param.length > 1) {
				result.put(param[0], param[1]);
			}
		}
		return result;
	}

}
