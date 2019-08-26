package org.tio.utils.qr.scheme;

import static org.tio.utils.qr.scheme.SchemeUtil.getParameters;

import java.util.Map;

/**
 * Bookmark encoding
 */
public class Bookmark extends Schema {

	private static final String	BEGIN_BOOKMARK	= "MEBKM";
	private static final String	URL				= "URL";
	private static final String	TITLE			= "TITLE";
	private static final String	LINE_SEPARATOR	= ";";
	private String				url;
	private String				titel;

	public Bookmark() {
		super();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitel() {
		return titel;
	}

	public void setTitel(String titel) {
		this.titel = titel;
	}

	@Override
	public Schema parseSchema(String code) {
		if (code == null || !code.startsWith(BEGIN_BOOKMARK)) {
			throw new IllegalArgumentException("this is not a valid Bookmark code: " + code);
		}
		Map<String, String> parameters = getParameters(code.replaceFirst(BEGIN_BOOKMARK + ":", ""), LINE_SEPARATOR, ":");
		if (parameters.containsKey(URL)) {
			setUrl(parameters.get(URL));
		}
		if (parameters.containsKey(TITLE)) {
			setTitel(parameters.get(TITLE));
		}
		return this;
	}

	@Override
	public String generateString() {
		StringBuilder sb = new StringBuilder();
		sb.append(BEGIN_BOOKMARK).append(":");
		if (url != null) {
			sb.append(URL).append(":").append(url).append(LINE_SEPARATOR);
		}
		if (titel != null) {
			sb.append(TITLE).append(":").append(titel).append(LINE_SEPARATOR);
		}
		sb.append(LINE_SEPARATOR);
		return sb.toString();
	}

	/**
	 * Returns the textual representation of this bookmark of the form
	 * <p>
	 * MEBKM:URL:google.com;TITLE:Google;
	 * </p>
	 */
	@Override
	public String toString() {
		return generateString();
	}

	public static Bookmark parse(final String code) {
		Bookmark bookmark = new Bookmark();
		bookmark.parseSchema(code);
		return bookmark;
	}

}
