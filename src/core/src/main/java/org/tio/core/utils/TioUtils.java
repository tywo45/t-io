package org.tio.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.ChannelContext.CloseCode;
import org.tio.utils.thread.ThreadUtils;

/**
 * 
 * @author tanyaowu 
 * 2017年10月19日 上午9:40:54
 */
public class TioUtils {
	private static Logger log = LoggerFactory.getLogger(TioUtils.class);

	public static boolean checkBeforeIO(ChannelContext channelContext) {
		if (channelContext.isWaitingClose) {
			return false;
		}

		Boolean isopen = null;
		if (channelContext.asynchronousSocketChannel != null) {
			isopen = channelContext.asynchronousSocketChannel.isOpen();

			if (channelContext.isClosed || channelContext.isRemoved) {
				if (isopen) {
					try {
						Tio.close(channelContext,
						        "asynchronousSocketChannel is open, but channelContext isClosed: " + channelContext.isClosed + ", isRemoved: " + channelContext.isRemoved, CloseCode.CHANNEL_NOT_OPEN);
					} catch (Throwable e) {
						log.error(e.toString(), e);
					}
				}
				log.info("{}, isopen:{}, isClosed:{}, isRemoved:{}", channelContext, isopen, channelContext.isClosed, channelContext.isRemoved);
				return false;
			}
		} else {
			log.error("{}, 请检查此异常, asynchronousSocketChannel is null, isClosed:{}, isRemoved:{}, {} ", channelContext, channelContext.isClosed, channelContext.isRemoved,
			        ThreadUtils.stackTrace());
			return false;
		}

		if (!isopen) {
			log.info("{}, 可能对方关闭了连接, isopen:{}, isClosed:{}, isRemoved:{}", channelContext, isopen, channelContext.isClosed, channelContext.isRemoved);
			Tio.close(channelContext, "asynchronousSocketChannel is not open, 可能对方关闭了连接", CloseCode.CHANNEL_NOT_OPEN);
			return false;
		}
		return true;
	}

}
