package org.tio.core.maintain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.cache.IpStatRemovalListener;
import org.tio.core.stat.IpStat;
import org.tio.utils.cache.caffeine.CaffeineCache;

/**
 * 使用方法（注意顺序）：<br>
 *	1、serverTioConfig.setIpStatListener(ShowcaseIpStatListener.me);
	2、serverTioConfig.ipStats.addDuration(Time.MINUTE_1 * 5);
 * @author tanyaowu
 * 2017年4月15日 下午12:13:19
 */
public class IpStats {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(IpStats.class);

	private final static String CACHE_NAME = "TIO_IP_STAT";

	private String			tioConfigId;
	private TioConfig	tioConfig;

	/**
	 * key: 时长，单位：秒
	 */
	public final Map<Long, CaffeineCache> cacheMap = new HashMap<>();

	public List<Long> durationList = null;//new ArrayList<>();

	public IpStats(TioConfig tioConfig, Long[] durations) {
		this.tioConfig = tioConfig;
		this.tioConfigId = tioConfig.getId();
		if (durations != null) {
			addDurations(durations);
		}
	}

	/**
	 * 添加监控时段，不要添加过多的时间段，因为每个时间段都要消耗一份内存，一般加一个时间段就可以了
	 * @param duration 单位：秒
	 * @author: tanyaowu
	 */
	public void addDuration(Long duration) {
		synchronized (this) {
			if (durationList == null) {
				durationList = new ArrayList<>();
			}
			@SuppressWarnings("unchecked")
			CaffeineCache caffeineCache = CaffeineCache.register(getCacheName(duration), duration, null, new IpStatRemovalListener(tioConfig, tioConfig.getIpStatListener()));
			cacheMap.put(duration, caffeineCache);
			durationList.add(duration);
		}
	}

	/**
	 * 添加监控时段，不要添加过多的时间段，因为每个时间段都要消耗一份内存，一般加一个时间段就可以了
	 * @param durations 单位：秒
	 * @author: tanyaowu
	 */
	public void addDurations(Long[] durations) {
		if (durations != null) {
			for (Long duration : durations) {
				addDuration(duration);
			}
		}
	}

	/**
	 * 删除监控时间段
	 * @param duration
	 * @author: tanyaowu
	 */
	public void removeDuration(Long duration) {
		clear(duration);
		cacheMap.remove(duration);

		if (durationList != null) {
			durationList.remove(duration);
		}
	}

	/**
	 * 
	 * @param duration
	 * @return
	 * @author: tanyaowu
	 */
	public String getCacheName(Long duration) {
		String cacheName = CACHE_NAME + "_" + this.tioConfigId + "_";
		return cacheName + duration;
	}

	/**
	 * 清空监控数据
	 * @author: tanyaowu
	 */
	public void clear(Long duration) {
		CaffeineCache caffeineCache = cacheMap.get(duration);
		if (caffeineCache == null) {
			return;
		}
		caffeineCache.clear();
	}

	/**
	 * 根据ip获取IpStat，如果缓存中不存在，则创建
	 * @param duration
	 * @param channelContext
	 * @return
	 * @author: tanyaowu
	 */
	public IpStat get(Long duration, ChannelContext channelContext) {
		return get(duration, channelContext, true);
	}

	/**
	 * 根据ip获取IpStat，如果缓存中不存在，则根据forceCreate的值决定是否创建
	 * @param duration
	 * @param channelContext
	 * @param forceCreate
	 * @return
	 * @author: tanyaowu
	 */
	public IpStat get(Long duration, ChannelContext channelContext, boolean forceCreate) {
		return _get(duration, channelContext, forceCreate, true);
	}
	
	/**
	 * 
	 * @param duration
	 * @param channelContext
	 * @param forceCreate
	 * @param useProxyClient
	 * @return
	 * @author tanyaowu
	 */
	public IpStat _get(Long duration, ChannelContext channelContext, boolean forceCreate, boolean useProxyClient) {
		if (channelContext == null) {
			return null;
		}
		CaffeineCache caffeineCache = cacheMap.get(duration);
		if (caffeineCache == null) {
			return null;
		}

		String ip = null;
		if (useProxyClient && channelContext.getProxyClientNode() != null) {
			ip = channelContext.getProxyClientNode().getIp();
		} else {
			ip = channelContext.getClientNode().getIp();
		}
		IpStat ipStat = (IpStat) caffeineCache.get(ip);
		if (ipStat == null && forceCreate) {
			synchronized (this) {
				ipStat = (IpStat) caffeineCache.get(ip);
				if (ipStat == null) {
					ipStat = new IpStat(ip, duration);
					caffeineCache.put(ip, ipStat);
				}
			}
		}
		return ipStat;
	}

	/**
	 *
	 * @return
	 * @author: tanyaowu
	 */
	public ConcurrentMap<String, Serializable> map(Long duration) {
		CaffeineCache caffeineCache = cacheMap.get(duration);
		if (caffeineCache == null) {
			return null;
		}
		ConcurrentMap<String, Serializable> map = caffeineCache.asMap();
		return map;
	}

	/**
	 *
	 * @return
	 * @author: tanyaowu
	 */
	public Long size(Long duration) {
		CaffeineCache caffeineCache = cacheMap.get(duration);
		if (caffeineCache == null) {
			return null;
		}
		return caffeineCache.size();
	}

	/**
	 *
	 * @return
	 * @author: tanyaowu
	 */
	public Collection<Serializable> values(Long duration) {
		CaffeineCache caffeineCache = cacheMap.get(duration);
		if (caffeineCache == null) {
			return null;
		}
		Collection<Serializable> set = caffeineCache.asMap().values();
		return set;
	}
}
