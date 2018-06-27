package org.tio.http.server.stat.ip.path;

import org.tio.core.GroupContext;
import org.tio.http.common.HttpRequest;

public interface IpPathAccessStatListener {

	/**
	 * 
	 * @param groupContext
	 * @param ip
	 * @param ipAccessStat
	 * @author tanyaowu
	 */
	public void onExpired(GroupContext groupContext, String ip, IpAccessStat ipAccessStat);
	
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
