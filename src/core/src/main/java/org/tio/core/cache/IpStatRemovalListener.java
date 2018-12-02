package org.tio.core.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.GroupContext;
import org.tio.core.stat.IpStat;
import org.tio.core.stat.IpStatListener;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;

/**
 * @author tanyaowu 
 * 2017年8月21日 下午1:32:32
 */
@SuppressWarnings("rawtypes")
public class IpStatRemovalListener implements RemovalListener {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(IpStatRemovalListener.class);

	private IpStatListener ipStatListener;

	private GroupContext groupContext = null;

	/**
	 * 
	 * @author: tanyaowu
	 */
	public IpStatRemovalListener(GroupContext groupContext, IpStatListener ipStatListener) {
		this.groupContext = groupContext;
		this.ipStatListener = ipStatListener;
	}

	//	@Override
	//	public void onRemoval(RemovalNotification notification) {
	//		String ip = (String) notification.getKey();
	//		IpStat ipStat = (IpStat) notification.getValue();
	//
	//		if (ipStatListener != null) {
	//			ipStatListener.onExpired(groupContext, ipStat);
	//		}
	//
	//		//		log.info("ip数据统计[{}]\r\n{}", ip, Json.toFormatedJson(ipStat));
	//	}

	@Override
	public void onRemoval(Object key, Object value, RemovalCause cause) {
		//		String ip = (String) key;
		IpStat ipStat = (IpStat) value;

		if (ipStatListener != null) {
			ipStatListener.onExpired(groupContext, ipStat);
		}

	}
}
