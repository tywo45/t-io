package org.tio.core;

/**
 * 
 * @author tanyaowu 
 * 2017年10月19日 上午9:39:36
 */
public interface ChannelContextFilter {

	/**
	 * 过滤ChannelContext
	 * @param channelContext
	 * @return false: 排除此channelContext, true: 不排除
	 *
	 * @author tanyaowu
	 * 2017年1月13日 下午3:28:54
	 *
	 */
	public boolean filter(ChannelContext channelContext);

}
