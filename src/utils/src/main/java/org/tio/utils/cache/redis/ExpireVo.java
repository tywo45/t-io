package org.tio.utils.cache.redis;

import java.util.Objects;

/**
 * @author tanyaowu
 * 2017年8月14日 下午1:40:14
 */
public class ExpireVo {

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

		//		Set<ExpireVo> set = new HashSet<>();
		//
		//		ExpireVo expireVo1 = new ExpireVo("x", "1", 1000);
		//		ExpireVo expireVo2 = new ExpireVo("x", "1", 1000);
		//		ExpireVo expireVo3 = new ExpireVo("x", "2", 1000);
		//
		//		boolean x = set.add(expireVo1);
		//		boolean y = set.add(expireVo2);
		//		boolean z = set.add(expireVo3);
		//
		//		System.out.println(set.size());

	}

	private String cacheName;

	private String key;

	private long timeToIdleSeconds;

	public ExpireVo(String cacheName, String key, long timeToIdleSeconds) {
		super();
		this.cacheName = cacheName;
		this.key = key;
		this.timeToIdleSeconds = timeToIdleSeconds;
		//		this.expirable = expirable;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ExpireVo other = (ExpireVo) obj;

		return Objects.equals(cacheName, other.cacheName) && Objects.equals(key, other.key);
	}

	public String getCacheName() {
		return cacheName;
	}

	public long getTimeToIdleSeconds() {
		return timeToIdleSeconds;
	}

	public String getKey() {
		return key;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (cacheName == null ? 0 : cacheName.hashCode());
		result = prime * result + (key == null ? 0 : key.hashCode());
		return result;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public void setTimeToIdleSeconds(long expire) {
		this.timeToIdleSeconds = expire;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
