package org.tio.utils.qr.scheme;

import static org.tio.utils.qr.scheme.SchemeUtil.getParameters;

import java.util.Map;

/**
 * Encodes a mms code, format is: <code>mms:+1-212-555-1212:subject</code>
 * 
 */
public class MMS extends Schema {

	private static final String	MMS	= "mms";
	private String				number;
	private String				subject;

	public MMS() {
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
		if (code == null || !code.trim().toLowerCase().startsWith(MMS)) {
			throw new IllegalArgumentException("this is not a valid sms code: " + code);
		}
		Map<String, String> parameters = getParameters(code.trim().toLowerCase());
		if (parameters.containsKey(MMS)) {
			setNumber(parameters.get(MMS));
		}
		if (getNumber() != null && parameters.containsKey(getNumber())) {
			setSubject(parameters.get(getNumber()));
		}
		return this;
	}

	@Override
	public String generateString() {
		return MMS + ":" + number + (subject != null ? ":" + subject : "");
	}

	@Override
	public String toString() {
		return generateString();
	}

	public static MMS parse(final String mmsCode) {
		MMS mms = new MMS();
		mms.parseSchema(mmsCode);
		return mms;
	}
}
