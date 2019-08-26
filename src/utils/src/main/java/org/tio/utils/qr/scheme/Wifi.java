package org.tio.utils.qr.scheme;

import static org.tio.utils.qr.scheme.SchemeUtil.getParameters;

import java.util.Map;

/**
 * Encodes a Wifi connection, format is:
 * <code>WIFI:T:AUTHENTICATION;S:SSID;P:PSK;H:HIDDEN;</code>
 */
public class Wifi extends Schema {

	public static final String	WIFI_PROTOCOL_HEADER	= "WIFI:";
	public static final String	AUTHENTICATION			= "T";
	public static final String	SSID					= "S";
	public static final String	PSK						= "P";
	public static final String	HIDDEN					= "H";
	private String				authentication;
	private String				ssid;
	private String				psk;
	private boolean				hidden					= false;

	public Wifi() {
		super();
	}

	/**
	 * @return the authentication
	 */
	public String getAuthentication() {
		return authentication;
	}

	/**
	 * @param authentication
	 *            the authentication to set
	 */
	public void setAuthentication(Authentication authentication) {
		setAuthentication(authentication.toString());
	}

	/**
	 * @param authentication
	 *            the authentication to set
	 */
	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}

	/**
	 * @param authentication
	 *            the authentication to set
	 */
	public Wifi withAuthentication(Authentication authentication) {
		setAuthentication(authentication);
		return this;
	}

	/**
	 * @return the ssid
	 */
	public String getSsid() {
		return ssid;
	}

	/**
	 * @param ssid
	 *            the ssid to set
	 */
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	/**
	 * @param ssid
	 *            the ssid to set
	 */
	public Wifi withSsid(String ssid) {
		setSsid(ssid);
		return this;
	}

	/**
	 * @return the psk
	 */
	public String getPsk() {
		return psk;
	}

	/**
	 * @param psk
	 *            the psk to set
	 */
	public void setPsk(String psk) {
		this.psk = psk;
	}

	/**
	 * @param psk
	 *            the psk to set
	 */
	public Wifi withPsk(String psk) {
		setPsk(psk);
		return this;
	}

	/**
	 * @return the hidden
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * @param value
	 *            the hidden to set
	 */
	public void setHidden(final String value) {
		setHidden(Boolean.valueOf(value));
	}

	/**
	 * @param hidden
	 *            the hidden to set
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	/**
	 * @param hidden
	 *            the hidden to set
	 */
	public Wifi withHidden(boolean hidden) {
		setHidden(hidden);
		return this;
	}

	public enum Authentication {
		WEP, WPA, nopass;
	}

	@Override
	public Schema parseSchema(String code) {
		if (code == null || !code.startsWith(WIFI_PROTOCOL_HEADER)) {
			throw new IllegalArgumentException("this is not a valid WIFI code: " + code);
		}
		Map<String, String> parameters = getParameters(code.substring(WIFI_PROTOCOL_HEADER.length()), "(?<!\\\\);");
		if (parameters.containsKey(SSID)) {
			setSsid(unescape(parameters.get(SSID)));
		}
		if (parameters.containsKey(AUTHENTICATION)) {
			setAuthentication(parameters.get(AUTHENTICATION));
		}
		if (parameters.containsKey(PSK)) {
			setPsk(unescape(parameters.get(PSK)));
		}
		if (parameters.containsKey(HIDDEN)) {
			setHidden(parameters.get(HIDDEN));
		}
		return this;
	}

	@Override
	public String generateString() {
		StringBuilder bob = new StringBuilder(WIFI_PROTOCOL_HEADER);
		if (getSsid() != null) {
			bob.append(SSID).append(":").append(escape(getSsid())).append(";");
		}
		if (getAuthentication() != null) {
			bob.append(AUTHENTICATION).append(":").append(getAuthentication()).append(";");
		}
		if (getPsk() != null) {
			bob.append(PSK).append(":").append(escape(getPsk())).append(";");
		}
		bob.append(HIDDEN).append(":").append(isHidden()).append(";");
		return bob.toString();
	}

	@Override
	public String toString() {
		return generateString();
	}

	public static Wifi parse(final String wifiCode) {
		Wifi wifi = new Wifi();
		wifi.parseSchema(wifiCode);
		return wifi;
	}

	public static String escape(final String text) {
		return text.replace("\\", "\\\\").replace(",", "\\,").replace(";", "\\;").replace(".", "\\.").replace("\"", "\\\"").replace("'", "\\'");
	}

	public static String unescape(final String text) {
		return text.replace("\\\\", "\\").replace("\\,", ",").replace("\\;", ";").replace("\\.", ".").replace("\\\"", "\"").replace("\\'", "'");
	}
}
