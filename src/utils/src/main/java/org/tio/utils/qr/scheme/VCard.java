package org.tio.utils.qr.scheme;

import static org.tio.utils.qr.scheme.SchemeUtil.LINE_FEED;
import static org.tio.utils.qr.scheme.SchemeUtil.getParameters;

import java.util.Map;

/**
 * A simple wrapper for vCard data to use with ZXing QR Code generator.
 * <p>
 * See also http://zxing.appspot.com/generator/ and Contact Information
 *
 */
public class VCard extends Schema {

	private static final String	BEGIN_VCARD	= "BEGIN:VCARD";
	private static final String	NAME		= "N";
	private static final String	COMPANY		= "ORG";
	private static final String	TITLE		= "TITLE";
	private static final String	PHONE		= "TEL";
	private static final String	WEB			= "URL";
	private static final String	EMAIL		= "EMAIL";
	private static final String	ADDRESS		= "ADR";
	private static final String	NOTE		= "NOTE";

	private String	name;
	private String	company;
	private String	title;
	private String	phoneNumber;
	private String	email;
	private String	address;
	private String	website;
	private String	note;

	public VCard() {
		super();
	}

	public VCard(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public VCard setName(String name) {
		this.name = name;
		return this;
	}

	public String getCompany() {
		return company;
	}

	public VCard setCompany(String company) {
		this.company = company;
		return this;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public VCard setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public VCard setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public VCard setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getAddress() {
		return address;
	}

	public VCard setAddress(String address) {
		this.address = address;
		return this;
	}

	public String getWebsite() {
		return website;
	}

	public VCard setWebsite(String website) {
		this.website = website;
		return this;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public Schema parseSchema(String code) {
		if (code == null || !code.startsWith(BEGIN_VCARD)) {
			throw new IllegalArgumentException("this is not a valid VCARD code: " + code);
		}
		Map<String, String> parameters = getParameters(code);
		if (parameters.containsKey(NAME)) {
			setName(parameters.get(NAME));
		}
		if (parameters.containsKey(TITLE)) {
			setTitle(parameters.get(TITLE));
		}
		if (parameters.containsKey(COMPANY)) {
			setCompany(parameters.get(COMPANY));
		}
		if (parameters.containsKey(ADDRESS)) {
			setAddress(parameters.get(ADDRESS));
		}
		if (parameters.containsKey(EMAIL)) {
			setEmail(parameters.get(EMAIL));
		}
		if (parameters.containsKey(WEB)) {
			setWebsite(parameters.get(WEB));
		}
		if (parameters.containsKey(PHONE)) {
			setPhoneNumber(parameters.get(PHONE));
		}
		if (parameters.containsKey(NOTE)) {
			setNote(parameters.get(NOTE));
		}
		return this;
	}

	@Override
	public String generateString() {
		StringBuilder sb = new StringBuilder();
		sb.append(BEGIN_VCARD).append(LINE_FEED);
		sb.append("VERSION:3.0").append(LINE_FEED);
		if (name != null) {
			sb.append(NAME).append(":").append(name);
		}
		if (company != null) {
			sb.append(LINE_FEED).append(COMPANY).append(":").append(company);
		}
		if (title != null) {
			sb.append(LINE_FEED).append(TITLE).append(":").append(title);
		}
		if (phoneNumber != null) {
			sb.append(LINE_FEED).append(PHONE).append(":").append(phoneNumber);
		}
		if (website != null) {
			sb.append(LINE_FEED).append(WEB).append(":").append(website);
		}
		if (email != null) {
			sb.append(LINE_FEED).append(EMAIL).append(":").append(email);
		}
		if (address != null) {
			sb.append(LINE_FEED).append(ADDRESS).append(":").append(address);
		}
		if (note != null) {
			sb.append(LINE_FEED).append(NOTE).append(":").append(note);
		}
		sb.append(LINE_FEED).append("END:VCARD");
		return sb.toString();
	}

	/**
	 * Returns the textual representation of this vcard of the form
	 * <p>
	 * BEGIN:VCARD N:John Doe ORG:Company TITLE:Title TEL:1234 URL:www.example.org
	 * EMAIL:john.doe@example.org ADR:Street END:VCARD
	 * </p>
	 */
	public String toString() {
		return generateString();
	}

	public static VCard parse(final String code) {
		VCard vCard = new VCard();
		vCard.parseSchema(code);
		return vCard;
	}
}
