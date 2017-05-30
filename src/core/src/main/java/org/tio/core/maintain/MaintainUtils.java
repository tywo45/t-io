package org.tio.core.maintain;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.intf.Packet;

public class MaintainUtils {
	private static Logger log = LoggerFactory.getLogger(MaintainUtils.class);

	/**
	 * 彻底删除，不再维护
	 * @param channelContext
	 *
	 * @author: tanyaowu
	 *
	 */
	public static <SessionContext, P extends Packet, R> void removeFromMaintain(ChannelContext<SessionContext, P, R> channelContext) {
		GroupContext<SessionContext, P, R> groupContext = channelContext.getGroupContext();
		try {
			groupContext.connections.remove(channelContext);
			groupContext.connecteds.remove(channelContext);
			groupContext.closeds.remove(channelContext);
			groupContext.ids.unbind(channelContext);
			if (StringUtils.isNotBlank(channelContext.getUserid())) {
				try {
					Aio.unbindUser(channelContext);
				} catch (Throwable e) {
					log.error(e.toString(), e);
				}
			}
			Aio.unbindGroup(channelContext);
		} catch (Exception e1) {
			log.error(e1.toString(), e1);
		}
	}

}
