package org.tio.utils.qr.scheme;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Encodes a url connection, format is: <code>HTTP://URL</code>
 * 
 */
public class Url extends Schema {

	private URL url;

	public Url() {
		super();
	}

	public String getUrl() {
		if (url != null) {
			return url.toString().toUpperCase();
		}
		return null;
	}

	public void setUrl(String url) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			// ignore
			this.url = null;
		}
	}

	@Override
	public Schema parseSchema(String code) {
		if (code == null || (!code.trim().toLowerCase().startsWith("http") && !code.trim().toLowerCase().startsWith("https"))) {
			throw new IllegalArgumentException("this is not a valid url code: " + code);
		}
		setUrl(code.trim());
		return this;
	}

	@Override
	public String generateString() {
		return getUrl();
	}

	@Override
	public String toString() {
		return generateString();
	}

	public static Url parse(final String code) {
		Url u = new Url();
		u.parseSchema(code);
		return u;
	}

}
