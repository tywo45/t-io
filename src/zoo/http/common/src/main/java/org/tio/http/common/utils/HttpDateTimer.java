package org.tio.http.common.utils;

import org.tio.http.common.HeaderValue;
import org.tio.utils.SystemTimer;
import org.tio.utils.SystemTimer.TimerListener;
import org.tio.utils.hutool.DateUtil;

/**
 * 
 * @author tanyaowu 
 * 2018年6月17日 下午10:37:16
 */
public class HttpDateTimer {

	static {
		SystemTimer.addTimerListener(new TimerListener() {
			@Override
			public void onChange(long currTime) {
				httpDateString = DateUtil.httpDate(currTime);
				httpDateValue = HeaderValue.from(httpDateString);
			}
		});
	}

	private static volatile String httpDateString = DateUtil.httpDate();

	public static volatile HeaderValue httpDateValue = HeaderValue.from(httpDateString);

	public static String currDateString() {
		return httpDateString;
	}

	public static HeaderValue httpDateValue() {
		return httpDateValue;
	}
}
