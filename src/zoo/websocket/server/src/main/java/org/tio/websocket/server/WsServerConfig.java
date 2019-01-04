package org.tio.websocket.server;

import org.tio.http.common.HttpConfig;

/**
 * @author tanyaowu
 * 2017年6月28日 下午2:42:59
 */
public class WsServerConfig extends HttpConfig {

	public WsServerConfig(Integer bindPort, boolean useSession) {
		super(bindPort, useSession);
	}

	/**
	 *
	 * @author tanyaowu
	 */
	public WsServerConfig(Integer bindPort) {
		super(bindPort, true);
	}

}
