package org.tio.utils.qr.scheme;

import static org.tio.utils.qr.scheme.SchemeUtil.LINE_FEED;
import static org.tio.utils.qr.scheme.SchemeUtil.getParameters;

import java.util.Map;

/**
 * 
 * A simple wrapper for iCal data to use with ZXing QR Code generator.
 *
 */
public class ICal extends Schema {

	private static final String	BEGIN_VCALENDAR	= "BEGIN:VCALENDAR";
	private SubSchema			subSchema;

	/**
	 * Invisible default constructor.
	 */
	private ICal() {
		super();
	}

	public ICal(IEvent event) {
		this();
		this.subSchema = event;
	}

	public ICal(IToDo toDo) {
		this();
		this.subSchema = toDo;
	}

	public ICal(IJournal journal) {
		this();
		this.subSchema = journal;
	}

	public ICal(IFreeBusyTime freeBusyTime) {
		this();
		this.subSchema = freeBusyTime;
	}

	public SubSchema getSubSchema() {
		return subSchema;
	}

	@Override
	public Schema parseSchema(String code) {
		if (code == null || !code.startsWith(BEGIN_VCALENDAR)) {
			throw new IllegalArgumentException("this is not a valid ICal code: " + code);
		}
		Map<String, String> parameters = getParameters(code);
		if (parameters.containsKey(IEvent.NAME)) {
			subSchema = IEvent.parse(parameters, code);
		}
		if (parameters.containsKey(IToDo.NAME)) {
			subSchema = IToDo.parse(parameters, code);
		}
		if (parameters.containsKey(IJournal.NAME)) {
			subSchema = IJournal.parse(parameters, code);
		}
		if (parameters.containsKey(IFreeBusyTime.NAME)) {
			subSchema = IFreeBusyTime.parse(parameters, code);
		}
		return this;
	}

	@Override
	public String generateString() {
		StringBuilder sb = new StringBuilder();
		sb.append(BEGIN_VCALENDAR).append(LINE_FEED);
		sb.append("VERSION:2.0").append(LINE_FEED);
		sb.append("PRODID:-//hacksw/handcal//NONSGML v1.0//EN").append(LINE_FEED);
		if (subSchema != null) {
			sb.append(subSchema.generateString());
		}
		sb.append(LINE_FEED).append("END:VCALENDAR");
		return sb.toString();
	}

	@Override
	public String toString() {
		return generateString();
	}

	public static ICal parse(final String code) {
		ICal iCal = new ICal();
		iCal.parseSchema(code);
		return iCal;
	}
}
