package org.tio.http.server.stat.ip.path;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.tio.utils.SystemTimer;
import org.tio.utils.hutool.BetweenFormater;
import org.tio.utils.hutool.BetweenFormater.Level;
import org.tio.utils.lock.MapWithLock;
import org.tio.utils.lock.SetWithLock;

/**
 * ip访问统计
 * @author tanyaowu 
 * 2017年10月27日 下午1:53:03
 */
public class IpAccessStat implements Serializable {
	private static final long serialVersionUID = 5314797979230623121L;

	/**
	 * key:   path, 形如："/user/login"
	 * value: IpPathAccessStat
	 */
	private MapWithLock<String, IpPathAccessStat> ipPathAccessStatMap = new MapWithLock<>(new HashMap<>());

	private Long durationType;

	/**
	 * 这个ip下的所有sessionid
	 */
	public final SetWithLock<String> sessionIds = new SetWithLock<>(new HashSet<>());

	/**
	 * 当前统计了多久，单位：毫秒
	 */
	private long duration;

	public long getDuration() {
		duration = SystemTimer.currTime - this.firstAccessTime;
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	/**
	 * ip
	 */
	private String ip;

	/**
	 * 第一次访问时间， 单位：毫秒
	 */
	private long firstAccessTime = SystemTimer.currTime;

	/**
	 * 最近一次访问时间， 单位：毫秒
	 */
	private long lastAccessTime = SystemTimer.currTime;

	/**
	 * 这个ip访问的次数
	 */
	public final AtomicInteger count = new AtomicInteger();

	/**
	 * 这个ip访问给服务器带来的时间消耗，单位：毫秒
	 */
	public final AtomicLong timeCost = new AtomicLong();

	/**
	 * 不带session的访问次数
	 */
	public final AtomicInteger noSessionCount = new AtomicInteger();

	/**
	 * 根据ip获取IpAccesspathStat，如果缓存中不存在，则创建
	 * @param ipAccessStat
	 * @param path
	 * @return
	 * @author tanyaowu
	 */
	public IpPathAccessStat get(String path) {
		return get(path, true);
	}

	/**
	 * 根据ipAccessStat获取IpAccesspathStat，如果缓存中不存在，则根据forceCreate的值决定是否创建
	 * @param ipAccessStat
	 * @param path
	 * @param forceCreate
	 * @return
	 * @author tanyaowu
	 */
	public IpPathAccessStat get(String path, boolean forceCreate) {
		if (path == null) {
			return null;
		}

		IpPathAccessStat ipPathAccessStat = ipPathAccessStatMap.get(path);
		if (ipPathAccessStat == null && forceCreate) {
			ipPathAccessStat = ipPathAccessStatMap.putIfAbsent(path, new IpPathAccessStat(durationType, ip, path));
		}

		return ipPathAccessStat;
	}

	/**
	 * 
	 * @param durationType
	 * @param ip
	 * @author tanyaowu
	 */
	public IpAccessStat(Long durationType, String ip) {
		this.durationType = durationType;
		this.ip = ip;
	}

	public MapWithLock<String, IpPathAccessStat> getIpPathAccessStatMap() {
		return ipPathAccessStatMap;
	}

	public void setIpPathAccessStatMap(MapWithLock<String, IpPathAccessStat> ipPathAccessStatMap) {
		this.ipPathAccessStatMap = ipPathAccessStatMap;
	}

	/**
	 * @return the duration
	 */
	public String getFormatedDuration() {
		duration = SystemTimer.currTime - this.firstAccessTime;
		BetweenFormater betweenFormater = new BetweenFormater(duration, Level.MILLSECOND);
		return betweenFormater.format();
	}

	public double getPerSecond() {
		int count = this.count.get();
		long duration = getDuration();
		double perSecond = (double) ((double) count / (double) duration) * (double) 1000;
		return perSecond;
	}

	public Long getDurationType() {
		return durationType;
	}

	public void setDurationType(Long durationType) {
		this.durationType = durationType;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public long getFirstAccessTime() {
		return firstAccessTime;
	}

	public void setFirstAccessTime(long firstAccessTime) {
		this.firstAccessTime = firstAccessTime;
	}

	public long getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(long lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}
}
