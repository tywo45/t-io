package org.tio.core;

import org.tio.core.intf.TioUuid;

/**
 * @author tanyaowu
 * 2017年6月5日 上午10:31:40
 */
public class DefaultTioUuid implements TioUuid {

	/**
	 *
	 * @author tanyaowu
	 */
	public DefaultTioUuid() {
	}

	/**
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public String uuid() {
		return java.util.UUID.randomUUID().toString();
	}
}
