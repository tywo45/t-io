package org.tio.core.maintain;

import java.util.Collection;
import java.util.function.Consumer;

import org.tio.core.GroupContext;
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

	private final static String		CACHE_NAME_PREFIX		= "TIO_IP_BLACK_LIST";
	private final static Long		TIME_TO_LIVE_SECONDS	= Time.DAY_1 * 120;
	private final static Long		TIME_TO_IDLE_SECONDS	= null;
	private String					cacheName				= null;
	private CaffeineCache			cache					= null;
	private ServerGroupContext		serverGroupContext;
	public final static IpBlacklist	GLOBAL					= new IpBlacklist();

	private IpBlacklist() {
		this.id = "__global__";
		this.cacheName = CACHE_NAME_PREFIX + this.id;
		this.cache = CaffeineCache.register(this.cacheName, TIME_TO_LIVE_SECONDS, TIME_TO_IDLE_SECONDS, null);
	}

	public IpBlacklist(String id, ServerGroupContext serverGroupContext) {
		this.id = id;
		this.serverGroupContext = serverGroupContext;
		this.cacheName = CACHE_NAME_PREFIX + this.id;
		this.cache = CaffeineCache.register(this.cacheName, TIME_TO_LIVE_SECONDS, TIME_TO_IDLE_SECONDS, null);
	}

	public boolean add(String ip) {
		//先添加到黑名单列表
		cache.put(ip, SystemTimer.currTime);

		if (serverGroupContext != null) {
			//删除相关连接
			Tio.remove(serverGroupContext, ip, "ip[" + ip + "]被加入了黑名单, " + serverGroupContext.getName());
		} else {
			GroupContext.ALL_SERVER_GROUPCONTEXTS.stream().forEach(new Consumer<ServerGroupContext>() {
				@Override
				public void accept(ServerGroupContext groupContext) {
					Tio.remove(groupContext, ip, "ip[" + ip + "]被加入了黑名单, " + groupContext.getName());

				}
			});
		}

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
