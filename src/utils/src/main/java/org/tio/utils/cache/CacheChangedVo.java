package org.tio.utils.cache;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author tanyaowu
 * 2017年8月12日 下午9:30:31
 */
public class CacheChangedVo implements Serializable {

	private static final long serialVersionUID = 1546804469064012259L;

	public static final String CLIENTID = UUID.randomUUID().toString();

	private String cacheName;

	private String key;

	private String clientId = CLIENTID;

	private CacheChangeType type;

	/**
	 *
	 * @author tanyaowu
	 */
	public CacheChangedVo() {
		super();
	}

	//	private

	/**
	 * @param cacheName
	 * @param type
	 * @author tanyaowu
	 */
	public CacheChangedVo(String cacheName, CacheChangeType type) {
		this();
		this.cacheName = cacheName;
		this.type = type;
	}

	/**
	 * @param cacheName
	 * @param key
	 * @param type
	 * @author tanyaowu
	 */
	public CacheChangedVo(String cacheName, String key, CacheChangeType type) {
		this();
		this.cacheName = cacheName;
		this.key = key;
		this.type = type;
	}

	/**
	 * @return the cacheName
	 */
	public String getCacheName() {
		return cacheName;
	}

	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the type
	 */
	public CacheChangeType getType() {
		return type;
	}

	/**
	 * @param cacheName the cacheName to set
	 */
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	/**
	 * @param clientId the clientId to set
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(CacheChangeType type) {
		this.type = type;
	}
}
