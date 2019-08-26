package org.tio.utils.qr.scheme;

import static org.tio.utils.qr.scheme.SchemeUtil.getParameters;

import java.util.Map;

/**
 * 
 *
 */
public class MeCard extends Schema {

	private static final String	BEGIN_MECARD	= "MECARD";
	private static final String	NAME			= "N";
	private static final String	ADDRESS			= "ADR";
	private static final String	TEL				= "TEL";
	private static final String	EMAIL			= "EMAIL";
	private static final String	LINE_SEPARATOR	= ";";

	private String	name;
	private String	address;
	private String	telephone;
	private String	email;

	public MeCard() {
		super();
	}

	public MeCard(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public Schema parseSchema(String code) {
		if (code == null || !code.startsWith(BEGIN_MECARD)) {
			throw new IllegalArgumentException("this is not a valid MeCard code: " + code);
		}
		Map<String, String> parameters = getParameters(code.replaceFirst(BEGIN_MECARD + ":", ""), LINE_SEPARATOR, ":");
		if (parameters.containsKey(NAME)) {
			setName(parameters.get(NAME));
		}
		if (parameters.containsKey(ADDRESS)) {
			setAddress(parameters.get(ADDRESS));
		}
		if (parameters.containsKey(TEL)) {
			setTelephone(parameters.get(TEL));
		}
		if (parameters.containsKey(EMAIL)) {
			setEmail(parameters.get(EMAIL));
		}
		return this;
	}

	@Override
	public String generateString() {
		StringBuilder sb = new StringBuilder();
		sb.append(BEGIN_MECARD).append(":");
		if (name != null) {
			sb.append(NAME).append(":").append(name).append(LINE_SEPARATOR);
		}
		if (address != null) {
			sb.append(ADDRESS).append(":").append(address).append(LINE_SEPARATOR);
		}
		if (telephone != null) {
			sb.append(TEL).append(":").append(telephone).append(LINE_SEPARATOR);
		}
		if (email != null) {
			sb.append(EMAIL).append(":").append(email).append(LINE_SEPARATOR);
		}
		sb.append(LINE_SEPARATOR);
		return sb.toString();
	}

	/**
	 * Returns the textual representation of this mecard of the form
	 * <p>
	 * MECARD:N:Doe,John;TEL:13035551212;EMAIL:john.doe@example.com;;
	 * </p>
	 */
	@Override
	public String toString() {
		return generateString();
	}

	public static MeCard parse(final String meCardCode) {
		MeCard meCard = new MeCard();
		meCard.parseSchema(meCardCode);
		return meCard;
	}
}
