package org.tio.core.maintain;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.utils.hutool.StrUtil;
import org.tio.utils.lock.MapWithLock;

/**
 * 一对一  (bsid <--> ChannelContext)<br>
 * Bs: business，业务id和ChannelContext绑定<br>
 * 需求见：https://gitee.com/tywo45/t-io/issues/IK30Q
 * @author tanyaowu 
 */
public class BsIds {
	private static Logger log = LoggerFactory.getLogger(BsIds.class);

	/**
	 * key: 业务id
	 * value: ChannelContext
	 */
	private MapWithLock<String, ChannelContext> map = new MapWithLock<>(new HashMap<String, ChannelContext>());

	/**
	 * 
	 * @param channelContext
	 * @param bsId
	 * @author tanyaowu
	 */
	public void bind(ChannelContext channelContext, String bsId) {
		try {
			GroupContext groupContext = channelContext.groupContext;
			if (groupContext.isShortConnection) {
				return;
			}

			//先解绑，否则如果业务层绑定两个不同的bsid，就会导致资源释放不掉
			unbind(channelContext);

			if (StrUtil.isBlank(bsId)) {
				return;
			}
			channelContext.setBsId(bsId);
			map.put(bsId, channelContext);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}

	/**
	 * 
	 * @param groupContext
	 * @param bsId
	 * @return
	 * @author tanyaowu
	 */
	public ChannelContext find(GroupContext groupContext, String bsId) {
		if (groupContext.isShortConnection) {
			return null;
		}

		if (StrUtil.isBlank(bsId)) {
			return null;
		}

		return map.get(bsId);
	}

	/**
	 * 
	 * @return
	 * @author tanyaowu
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
			String bsId = channelContext.getBsId();
			if (StrUtil.isBlank(bsId)) {
				return;
			}
			map.remove(bsId);
			channelContext.setBsId(null);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}
}
