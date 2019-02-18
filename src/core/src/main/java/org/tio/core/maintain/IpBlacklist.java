package org.tio.core.maintain;

import java.util.Collection;

import org.tio.core.Tio;
import org.tio.server.ServerGroupContext;
import org.tio.utils.SystemTimer;
import org.tio.utils.cache.caffeine.CaffeineCache;
import org.tio.utils.time.Time;

/**
 *
 * @author tanyaowu
 * 2017年5月22日 下午2:53:47
 */
public class IpBlacklist {
	private String id;

	private final static String	CACHE_NAME				= "TIO_IP_BLACK_LIST";
	private final static Long	TIME_TO_LIVE_SECONDS	= Time.MINUTE_1 * 120;
	private final static Long	TIME_TO_IDLE_SECONDS	= null;

	private String			cacheName	= null;
	private CaffeineCache	cache		= null;

	private ServerGroupContext serverGroupContext;

	public IpBlacklist(String id, ServerGroupContext serverGroupContext) {
		this.id = id;
		this.serverGroupContext = serverGroupContext;
		this.cacheName = CACHE_NAME + this.id;
		this.cache = CaffeineCache.register(this.cacheName, TIME_TO_LIVE_SECONDS, TIME_TO_IDLE_SECONDS, null);
	}

	public boolean add(String ip) {
		//先添加到黑名单列表
		cache.put(ip, SystemTimer.currTime);

		//再删除相关连接
		Tio.remove(serverGroupContext, ip, "ip[" + ip + "]被加入了黑名单");
		return true;
	}

	public void clear() {
		cache.clear();
	}

	public Collection<String> getAll() {
		return cache.keys();
	}

	/**
	 * 是否在黑名单中
	 * @param ip
	 * @return
	 * @author tanyaowu
	 */
	public boolean isInBlacklist(String ip) {
		return cache.get(ip) != null;
	}

	/**
	 * 从黑名单中删除
	 * @param ip
	 * @return
	 * @author: tanyaowu
	 */
	public void remove(String ip) {
		cache.remove(ip);
	}
}
