package org.tio.core.intf;

import org.tio.core.ChannelContext;

/**
 * @author tanyaowu
 * 2017年5月13日 下午10:35:05
 */
public interface GroupListener {
	/**
	 * 绑定群组后回调该方法
	 * @param channelContext
	 * @param group
	 * @throws Exception
	 * @author tanyaowu
	 */
	void onAfterBind(ChannelContext channelContext, String group) throws Exception;

	/**
	 * 解绑群组后回调该方法
	 * @param channelContext
	 * @param group
	 * @throws Exception
	 * @author tanyaowu
	 */
	void onAfterUnbind(ChannelContext channelContext, String group) throws Exception;
}
