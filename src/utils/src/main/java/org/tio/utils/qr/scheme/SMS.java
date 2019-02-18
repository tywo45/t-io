package org.tio.utils.qr.scheme;

import static org.tio.utils.qr.scheme.SchemeUtil.getParameters;

import java.util.Map;

/**
 * Encodes a sms code, format is: <code>sms:+1-212-555-1212:subject</code>
 * 
 */
public class SMS extends Schema {

	private static final String	SMS	= "sms";
	private String				number;
	private String				subject;

	public SMS() {
		super();
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Override
	public Schema parseSchema(String code) {
		if (code == null || !code.trim().toLowerCase().startsWith(SMS)) {
			throw new IllegalArgumentException("this is not a valid sms code: " + code);
		}
		Map<String, String> parameters = getParameters(code.trim().toLowerCase());
		if (parameters.containsKey(SMS)) {
			setNumber(parameters.get(SMS));
		}
		if (getNumber() != null && parameters.containsKey(getNumber())) {
			setSubject(parameters.get(getNumber()));
		}
		return this;
	}

	@Override
	public String generateString() {
		return SMS + ":" + number + (subject != null ? ":" + subject : "");
	}

	@Override
	public String toString() {
		return generateString();
	}

	public static SMS parse(final String code) {
		SMS sms = new SMS();
		sms.parseSchema(code);
		return sms;
	}
}
