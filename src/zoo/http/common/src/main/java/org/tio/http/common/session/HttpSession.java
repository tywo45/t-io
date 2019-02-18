package org.tio.http.common.session;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.HttpConfig;
import org.tio.utils.SystemTimer;

/**
 *
 * @author tanyaowu
 * 2017年8月5日 上午10:16:26
 */
public class HttpSession implements java.io.Serializable {
	private static Logger log = LoggerFactory.getLogger(HttpSession.class);

	private static final long serialVersionUID = 6077020620501316538L;

	private Map<String, Serializable> data = new ConcurrentHashMap<>();

	private String id = null;

	private long createTime = SystemTimer.currTime;

	/**
	 * 此处空的构造函数必须要有
	 * 
	 * @author: tanyaowu
	 */
	public HttpSession() {
	}

	/**
	 * @author tanyaowu
	 */
	public HttpSession(String id) {
		this.id = id;
	}

	/**
	 * 清空所有属性
	 * @param httpConfig
	 * @author tanyaowu
	 */
	public void clear(HttpConfig httpConfig) {
		data.clear();
		update(httpConfig);
	}

	/**
	 * 获取会话属性
	 * @param key
	 * @return
	 * @author tanyaowu
	 */
	public Object getAttribute(String key) {
		return data.get(key);
	}

	/**
	 * 
	 * @param key
	 * @param clazz
	 * @return
	 * @author: tanyaowu
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key, Class<T> clazz) {
		return (T) data.get(key);
	}

	@SuppressWarnings("unchecked")
	public <T extends Serializable> T getAttribute(String key, Class<T> clazz, T defaultObj, HttpConfig httpConfig) {
		T t = (T) data.get(key);
		if (t == null) {
			log.warn("key【{}】'value in session is null", key);
			if (defaultObj != null) {
				setAttribute(key, defaultObj, httpConfig);
			}
			return defaultObj;
		}
		return t;
	}

	//	public Map<String, Serializable> getData() {
	//		return data;
	//	}

	public String getId() {
		return id;
	}

	/**
	 *
	 * @param key
	 * @param httpConfig
	 * @author tanyaowu
	 */
	public void removeAttribute(String key, HttpConfig httpConfig) {
		data.remove(key);
		update(httpConfig);
	}

	/**
	 * 设置会话属性
	 * @param key
	 * @param value
	 * @param httpConfig
	 * @author tanyaowu
	 */
	public void setAttribute(String key, Serializable value, HttpConfig httpConfig) {
		data.put(key, value);
		update(httpConfig);
	}

	public void update(HttpConfig httpConfig) {
		httpConfig.getSessionStore().put(id, this);
	}

	//	public void setData(Map<String, Serializable> data) {
	//		this.data = data;
	//	}

	public void setId(String id) {
		this.id = id;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
}
