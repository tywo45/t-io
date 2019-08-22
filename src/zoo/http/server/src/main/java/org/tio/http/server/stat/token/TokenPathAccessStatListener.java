package org.tio.http.server.stat.token;

import org.tio.core.TioConfig;
import org.tio.http.common.HttpRequest;

public interface TokenPathAccessStatListener {

	/**
	 * 
	 * @param tioConfig
	 * @param token
	 * @param tokenAccessStat
	 * @author tanyaowu
	 */
	public void onExpired(TioConfig tioConfig, String token, TokenAccessStat tokenAccessStat);

	/**
	 * 
	 * @param httpRequest
	 * @param token
	 * @param path
	 * @param tokenAccessStat
	 * @param tokenPathAccessStat
	 * @author tanyaowu
	 */
	public boolean onChanged(HttpRequest httpRequest, String token, String path, TokenAccessStat tokenAccessStat, TokenPathAccessStat tokenPathAccessStat);

}
