package org.tio.utils.qr.scheme;

import java.util.Map;

/**
 * Abstact class for sub schema.
 */
public abstract class SubSchema {

	SubSchema() {
		super();
	}

	/**
	 * Parse qr code sub schema for given code string and parent parameters.
	 * 
	 * @param code
	 * @return schema
	 */
	public abstract SubSchema parseSchema(Map<String, String> parameters, String code);

	/**
	 * Generates code string.
	 * 
	 * @return code
	 */
	public abstract String generateString();

}
