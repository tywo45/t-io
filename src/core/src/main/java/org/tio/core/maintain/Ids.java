package org.tio.core.maintain;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.utils.lock.MapWithLock;

/**
 *
 * @author tanyaowu
 * 2017年4月15日 下午12:13:19
 */
public class Ids {
	private static Logger log = LoggerFactory.getLogger(Ids.class);

	/**
	 * key: ChannelContext对象的id字段
	 * value: ChannelContext
	 */
	private MapWithLock<String, ChannelContext> map = new MapWithLock<>(new HashMap<String, ChannelContext>());

	/**
	 *
	 * @param channelContext
	 * @author tanyaowu
	 */
	public void bind(ChannelContext channelContext) {
		try {
			GroupContext groupContext = channelContext.groupContext;
			if (groupContext.isShortConnection) {
				return;
			}

			String key = channelContext.getId();
			if (StringUtils.isBlank(key)) {
				return;
			}
			map.put(key, channelContext);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}

	/**
	 * Find.
	 *
	 * @param id the id
	 * @return the channel context
	 */
	public ChannelContext find(GroupContext groupContext, String id) {
		if (groupContext.isShortConnection) {
			return null;
		}

		if (StringUtils.isBlank(id)) {
			return null;
		}

		return map.get(id);
	}

	/**
	 * @return the cacheMap
	 */
	public MapWithLock<String, ChannelContext> getMap() {
		return map;
	}

	/**
	 *
	 * @param channelContext
	 * @author tanyaowu
	 */
	public void unbind(ChannelContext channelContext) {
		try {
			GroupContext groupContext = channelContext.groupContext;
			if (groupContext.isShortConnection) {
				return;
			}

			String key = channelContext.getId();
			if (StringUtils.isBlank(key)) {
				return;
			}
			map.remove(key);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}
}
