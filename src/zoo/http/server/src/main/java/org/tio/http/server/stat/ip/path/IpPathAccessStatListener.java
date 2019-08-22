package org.tio.http.server.stat.ip.path;

import org.tio.core.TioConfig;
import org.tio.http.common.HttpRequest;

public interface IpPathAccessStatListener {

	/**
	 * 
	 * @param tioConfig
	 * @param ip
	 * @param ipAccessStat
	 * @author tanyaowu
	 */
	public void onExpired(TioConfig tioConfig, String ip, IpAccessStat ipAccessStat);

	/**
	 * 
	 * @param httpRequest
	 * @param ip
	 * @param path
	 * @param ipAccessStat
	 * @param ipPathAccessStat
	 * @author tanyaowu
	 */
	public boolean onChanged(HttpRequest httpRequest, String ip, String path, IpAccessStat ipAccessStat, IpPathAccessStat ipPathAccessStat);

}
