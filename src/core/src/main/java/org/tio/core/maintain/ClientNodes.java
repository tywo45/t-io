package org.tio.core.maintain;

import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Node;
import org.tio.utils.lock.MapWithLock;

/**
 *一对一  (ip:port <--> ChannelContext)<br>
 * @author tanyaowu
 * 2017年4月1日 上午9:35:20
 */
public class ClientNodes {
	private static Logger log = LoggerFactory.getLogger(ClientNodes.class);

	/**
	 *
	 * @param channelContext
	 * @return
	 * @author tanyaowu
	 */
	public static String getKey(ChannelContext channelContext) {
		Node clientNode = channelContext.getClientNode();
		if (clientNode == null) {
			throw new RuntimeException("client node is null");
		}
		String key = getKey(clientNode.getIp(), clientNode.getPort());
		return key;
	}

	/**
	 *
	 * @param ip
	 * @param port
	 * @return
	 * @author tanyaowu
	 */
	public static String getKey(String ip, int port) {
		String key = ip + ":" + port;
		return key;
	}

	/** remoteAndChannelContext key: "ip:port" value: ChannelContext. */
	private MapWithLock<String, ChannelContext> mapWithLock = new MapWithLock<>();

	/**
	 *
	 * @param key
	 * @return
	 * @author tanyaowu
	 */
	public ChannelContext find(String key) {
		Lock lock = mapWithLock.readLock();
		lock.lock();
		try {
			Map<String, ChannelContext> m = mapWithLock.getObj();
			return m.get(key);
		} catch (Throwable e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 *
	 * @param ip
	 * @param port
	 * @return
	 * @author tanyaowu
	 */
	public ChannelContext find(String ip, int port) {
		String key = getKey(ip, port);
		return find(key);
	}

	/**
	 *
	 * @return
	 * @author tanyaowu
	 */
	public MapWithLock<String, ChannelContext> getObjWithLock() {
		return mapWithLock;
	}

	/**
	 * 添加映射
	 * @param channelContext
	 * @author tanyaowu
	 */
	public void put(ChannelContext channelContext) {
		if (channelContext.tioConfig.isShortConnection) {
			return;
		}
		try {
			String key = getKey(channelContext);
			mapWithLock.put(key, channelContext);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}

	/**
	 * Removes映射
	 * @param channelContext
	 * @author tanyaowu
	 */
	public void remove(ChannelContext channelContext) {
		if (channelContext.tioConfig.isShortConnection) {
			return;
		}
		try {
			String key = getKey(channelContext);
			mapWithLock.remove(key);
		} catch (Throwable e) {
			log.error(e.toString(), e);
		}
	}
}
