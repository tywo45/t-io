package org.tio.utils.qr.scheme;

import static org.tio.utils.qr.scheme.SchemeUtil.getParameters;

import java.util.Map;

/**
 * Encodes a YouTube video, format is: <code>youtube://[video ID]</code>
 * 
 */
public class YouTube extends Schema {

	public static final String	YOUTUBE	= "youtube";
	private String				videoId;

	/**
	 * Default constructor to construct new YouTube object.
	 */
	public YouTube() {
		super();
	}

	public YouTube(String videoId) {
		super();
		this.videoId = videoId;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	@Override
	public Schema parseSchema(String code) {
		if (code == null || !code.toLowerCase().startsWith(YOUTUBE)) {
			throw new IllegalArgumentException("this is not a valid you tube code: " + code);
		}
		Map<String, String> parameters = getParameters(code);
		if (parameters.containsKey(YOUTUBE)) {
			setVideoId(parameters.get(YOUTUBE));
		}
		return this;
	}

	@Override
	public String generateString() {
		return YOUTUBE + ":" + videoId;
	}

	@Override
	public String toString() {
		return generateString();
	}

	public static YouTube parse(final String code) {
		YouTube youTube = new YouTube();
		youTube.parseSchema(code);
		return youTube;
	}
}
