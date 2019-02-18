package org.tio.utils.qr.scheme;

import static org.tio.utils.qr.scheme.SchemeUtil.getParameters;

import java.util.Map;

/**
 * Encodes a e-mail address, format is: <code>mailto:mail@address.com</code>
 *
 */
public class EMail extends Schema {

	private static final String	MAILTO	= "mailto";
	private String				email;

	/**
	 * Default constructor to construct new e-mail object.
	 */
	public EMail() {
		super();
	}

	public EMail(String email) {
		super();
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public Schema parseSchema(String code) {
		if (code == null || !code.toLowerCase().startsWith(MAILTO)) {
			throw new IllegalArgumentException("this is not a valid email code: " + code);
		}
		Map<String, String> parameters = getParameters(code.toLowerCase());
		if (parameters.containsKey(MAILTO)) {
			setEmail(parameters.get(MAILTO));
		}
		return this;
	}

	@Override
	public String generateString() {
		return MAILTO + ":" + email;
	}

	public static EMail parse(final String emailCode) {
		EMail mail = new EMail();
		mail.parseSchema(emailCode);
		return mail;
	}

	@Override
	public String toString() {
		return generateString();
	}
}
