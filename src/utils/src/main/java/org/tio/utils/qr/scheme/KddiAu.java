package org.tio.utils.qr.scheme;

import static org.tio.utils.qr.scheme.SchemeUtil.LINE_FEED;
import static org.tio.utils.qr.scheme.SchemeUtil.getParameters;

import java.util.Map;

/**
 * 
 *
 */
public class KddiAu extends Schema {

	private static final String	BEGIN	= "MEMORY";
	private static final String	NAME1	= "NAME1";
	private static final String	NAME2	= "NAME2";
	private static final String	MAIL1	= "MAIL1";
	private static final String	MAIL2	= "MAIL2";
	private static final String	MAIL3	= "MAIL3";
	private static final String	TEL1	= "TEL1";
	private static final String	TEL2	= "TEL2";
	private static final String	TEL3	= "TEL3";
	private static final String	ADD		= "ADD";

	private String	name1;
	private String	name2;
	private String	address;
	private String	telephone1;
	private String	telephone2;
	private String	telephone3;
	private String	email1;
	private String	email2;
	private String	email3;

	public KddiAu() {
		super();
	}

	public KddiAu(String name1) {
		this.name1 = name1;
	}

	public String getName1() {
		return name1;
	}

	public void setName1(String name1) {
		this.name1 = name1;
	}

	public String getName2() {
		return name2;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelephone1() {
		return telephone1;
	}

	public void setTelephone1(String telephone1) {
		this.telephone1 = telephone1;
	}

	public String getTelephone2() {
		return telephone2;
	}

	public void setTelephone2(String telephone2) {
		this.telephone2 = telephone2;
	}

	public String getTelephone3() {
		return telephone3;
	}

	public void setTelephone3(String telephone3) {
		this.telephone3 = telephone3;
	}

	public String getEmail1() {
		return email1;
	}

	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	public String getEmail2() {
		return email2;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	public String getEmail3() {
		return email3;
	}

	public void setEmail3(String email3) {
		this.email3 = email3;
	}

	@Override
	public Schema parseSchema(String code) {
		if (code == null || !code.startsWith(BEGIN)) {
			throw new IllegalArgumentException("this is not a valid KDDI AU code: " + code);
		}
		Map<String, String> parameters = getParameters(code);
		if (parameters.containsKey(NAME1)) {
			setName1(parameters.get(NAME1));
		}
		if (parameters.containsKey(NAME2)) {
			setName2(parameters.get(NAME2));
		}
		if (parameters.containsKey(ADD)) {
			setAddress(parameters.get(ADD));
		}
		if (parameters.containsKey(TEL1)) {
			setTelephone1(parameters.get(TEL1));
		}
		if (parameters.containsKey(TEL2)) {
			setTelephone1(parameters.get(TEL2));
		}
		if (parameters.containsKey(TEL3)) {
			setTelephone1(parameters.get(TEL3));
		}
		if (parameters.containsKey(MAIL1)) {
			setEmail1(parameters.get(MAIL1));
		}
		if (parameters.containsKey(MAIL2)) {
			setEmail2(parameters.get(MAIL2));
		}
		if (parameters.containsKey(MAIL3)) {
			setEmail3(parameters.get(MAIL3));
		}
		return this;
	}

	@Override
	public String generateString() {
		StringBuilder sb = new StringBuilder();
		sb.append(BEGIN).append(LINE_FEED);
		if (name1 != null) {
			sb.append(NAME1).append(":").append(name1);
		}
		if (name2 != null) {
			sb.append(NAME2).append(":").append(name2);
		}
		if (address != null) {
			sb.append(ADD).append(":").append(address);
		}
		if (telephone1 != null) {
			sb.append(TEL1).append(":").append(telephone1);
		}
		if (telephone2 != null) {
			sb.append(TEL2).append(":").append(telephone2);
		}
		if (telephone3 != null) {
			sb.append(TEL3).append(":").append(telephone3);
		}
		if (email1 != null) {
			sb.append(MAIL1).append(":").append(email1);
		}
		if (email2 != null) {
			sb.append(MAIL2).append(":").append(email2);
		}
		if (email3 != null) {
			sb.append(MAIL3).append(":").append(email3);
		}
		sb.append(LINE_FEED);
		return sb.toString();
	}

	@Override
	public String toString() {
		return generateString();
	}

	public static KddiAu parse(final String kddiAuCode) {
		KddiAu kddiAu = new KddiAu();
		kddiAu.parseSchema(kddiAuCode);
		return kddiAu;
	}
}
