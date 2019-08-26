package org.tio.http.common.session.limiter;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu 
 * 2018年12月5日 下午10:36:28
 */
public class SessionRateVo implements Serializable {
	private static final long	serialVersionUID	= 5585145117550534333L;
	@SuppressWarnings("unused")
	private static Logger		log					= LoggerFactory.getLogger(SessionRateVo.class);

	public static SessionRateVo create(String path) {
		return new SessionRateVo(path);
	}

	private String path = null;

	/**
	 * 上一次访问时间
	 */
	private long lastAccessTime = 0;

	/**
	 * 已经访问了多少次（一分钟）
	 */
	private AtomicInteger accessCount = new AtomicInteger();

	/**
	 * 
	 * @author tanyaowu
	 */
	public SessionRateVo(String path) {
		this.path = path;
	}

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	/**
	 * @return the lastAccessTime
	 */
	public long getLastAccessTime() {
		return lastAccessTime;
	}

	/**
	 * @param lastAccessTime the lastAccessTime to set
	 */
	public void setLastAccessTime(long lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	/**
	 * @return the accessCount
	 */
	public AtomicInteger getAccessCount() {
		return accessCount;
	}

	/**
	 * @param accessCount the accessCount to set
	 */
	public void setAccessCount(AtomicInteger accessCount) {
		this.accessCount = accessCount;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
