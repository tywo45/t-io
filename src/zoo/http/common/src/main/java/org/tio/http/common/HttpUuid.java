package org.tio.http.common;

import java.util.concurrent.atomic.AtomicLong;

import org.tio.core.intf.TioUuid;

/**
 * @author tanyaowu
 * 2017年6月5日 上午10:44:26
 */
public class HttpUuid implements TioUuid {
	//	private static Logger log = LoggerFactory.getLogger(HttpUuid.class);

	private static java.util.concurrent.atomic.AtomicLong seq = new AtomicLong();

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	/**
	 *
	 * @author tanyaowu
	 */
	public HttpUuid() {
	}

	/**
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public String uuid() {
		return seq.incrementAndGet() + "";
	}
}
