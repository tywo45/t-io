package org.tio.core.stat;

import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.intf.Packet;

/**
 * @author tanyaowu 
 * 2017年9月25日 下午4:40:27
 */
public interface IpStatListener {
	/**
	 * 统计时间段到期后，用户可以在这个方法中实现把相关数据入库或是打日志等
	 * @param tioConfig
	 * @param ipStat
	 */
	public void onExpired(TioConfig tioConfig, IpStat ipStat);

	/**
	 * 建链后触发本方法，注：建链不一定成功，需要关注参数isConnected
	 * @param channelContext
	 * @param isConnected 是否连接成功,true:表示连接成功，false:表示连接失败
	 * @param isReconnect 是否是重连, true: 表示这是重新连接，false: 表示这是第一次连接
	 * @param ipStat
	 * @throws Exception
	 * @author: tanyaowu
	 */
	public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect, IpStat ipStat) throws Exception;

	/**
	 * 解码异常时
	 * @param channelContext
	 * @param ipStat
	 */
	public void onDecodeError(ChannelContext channelContext, IpStat ipStat);

	/**
	 * 发送后（注：不一定会发送成功）
	 * @param channelContext
	 * @param packet
	 * @param isSentSuccess
	 * @param ipStat
	 * @throws Exception
	 */
	public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess, IpStat ipStat) throws Exception;

	/**
	 * 解码成功后
	 * @param channelContext
	 * @param packet
	 * @param packetSize
	 * @param ipStat
	 * @throws Exception
	 */
	public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize, IpStat ipStat) throws Exception;

	/**
	 * 接收到一些字节数据后
	 * @param channelContext
	 * @param receivedBytes
	 * @param ipStat
	 * @throws Exception
	 */
	public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes, IpStat ipStat) throws Exception;

	/**
	 * 处理一个消息包后
	 * @param channelContext
	 * @param packet
	 * @param ipStat
	 * @param cost 耗时，单位：毫秒
	 * @throws Exception
	 */
	public void onAfterHandled(ChannelContext channelContext, Packet packet, IpStat ipStat, long cost) throws Exception;

}
