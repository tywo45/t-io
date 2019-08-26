package org.tio.http.server.stat.token;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.tio.utils.SystemTimer;
import org.tio.utils.hutool.BetweenFormater;
import org.tio.utils.hutool.BetweenFormater.Level;
import org.tio.utils.lock.MapWithLock;

/**
 * token访问统计
 * @author tanyaowu 
 * 2017年10月27日 下午1:53:03
 */
public class TokenAccessStat implements Serializable {
	private static final long serialVersionUID = 5314797979230623121L;

	/**
	 * key:   path, 形如："/user/login"
	 * value: TokenPathAccessStat
	 */
	private MapWithLock<String, TokenPathAccessStat> tokenPathAccessStatMap = new MapWithLock<>(new HashMap<>());

	private Long durationType;

	private String ip;

	private String	uid;
	/**
	 * 当前统计了多久，单位：毫秒
	 */
	private long	duration;

	public long getDuration() {
		duration = SystemTimer.currTime - this.firstAccessTime;
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	/**
	 * token
	 */
	private String token;

	/**
	 * 第一次访问时间， 单位：毫秒
	 */
	private long firstAccessTime = SystemTimer.currTime;

	/**
	 * 最近一次访问时间， 单位：毫秒
	 */
	private long lastAccessTime = SystemTimer.currTime;

	/**
	 * 这个token访问的次数
	 */
	public final AtomicInteger count = new AtomicInteger();

	/**
	 * 这个token访问给服务器带来的时间消耗，单位：毫秒
	 */
	public final AtomicLong timeCost = new AtomicLong();

	/**
	 * 根据token获取TokenAccesspathStat，如果缓存中不存在，则创建
	 * @param tokenAccessStat
	 * @param path
	 * @return
	 * @author tanyaowu
	 */
	public TokenPathAccessStat get(String path) {
		return get(path, true);
	}

	/**
	 * 根据tokenAccessStat获取TokenAccesspathStat，如果缓存中不存在，则根据forceCreate的值决定是否创建
	 * @param tokenAccessStat
	 * @param path
	 * @param forceCreate
	 * @return
	 * @author tanyaowu
	 */
	public TokenPathAccessStat get(String path, boolean forceCreate) {
		if (path == null) {
			return null;
		}

		TokenPathAccessStat tokenPathAccessStat = tokenPathAccessStatMap.get(path);
		if (tokenPathAccessStat == null && forceCreate) {
			tokenPathAccessStat = tokenPathAccessStatMap.putIfAbsent(path, new TokenPathAccessStat(durationType, token, path, ip, uid));
		}

		return tokenPathAccessStat;
	}

	/**
	 * 
	 * @param durationType
	 * @param token
	 * @param ip
	 * @param uid
	 */
	public TokenAccessStat(Long durationType, String token, String ip, String uid) {
		this.durationType = durationType;
		this.token = token;
		this.ip = ip;
		this.setUid(uid);
	}

	public MapWithLock<String, TokenPathAccessStat> getTokenPathAccessStatMap() {
		return tokenPathAccessStatMap;
	}

	public void setTokenPathAccessStatMap(MapWithLock<String, TokenPathAccessStat> tokenPathAccessStatMap) {
		this.tokenPathAccessStatMap = tokenPathAccessStatMap;
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
}
