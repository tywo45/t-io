package org.tio.utils.qr.scheme;

/**
 * 
 * Encodes a Google Play direct link, format is:
 * <code>{{{market://details?id=de.pawlidi.android}}}</code>
 *
 */
public class GooglePlay extends Schema {

	public static final String	GPLAY	= "{{{market://details?id=%s}}}";
	private String				appPackage;

	/**
	 * Default constructor to construct the GooglePlay obeject.
	 */
	public GooglePlay() {
		super();
	}

	public String getAppPackage() {
		return appPackage;
	}

	public void setAppPackage(String appPackage) {
		this.appPackage = appPackage;
	}

	@Override
	public Schema parseSchema(String code) {
		if (code == null || !code.trim().toLowerCase().startsWith("{{{market:")) {
			throw new IllegalArgumentException("this is not a google play code: " + code);
		}
		String[] paths = code.trim().toLowerCase().replace("}}}", "").split("=");
		if (paths != null && paths.length > 1) {
			setAppPackage(paths[1]);
		}
		return this;
	}

	@Override
	public String generateString() {
		return String.format(GPLAY, (appPackage != null ? appPackage : ""));
	}

	@Override
	public String toString() {
		return generateString();
	}

	public static GooglePlay parse(final String code) {
		GooglePlay googlePlay = new GooglePlay();
		googlePlay.parseSchema(code);
		return googlePlay;
	}

}
