package org.tio.core.maintain;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.tio.client.ClientGroupContext;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;

/**
 * 
 * @author tanyaowu 
 * 2017年10月19日 上午9:40:34
 */
public class MaintainUtils {

	/**
	 * 彻底删除，不再维护
	 * @param channelContext
	 *
	 * @author tanyaowu
	 *
	 */
	public static void remove(ChannelContext channelContext) {
		GroupContext groupContext = channelContext.groupContext;
		if (!groupContext.isServer()) {
			ClientGroupContext clientGroupContext = (ClientGroupContext)groupContext;
			clientGroupContext.closeds.remove(channelContext);
			clientGroupContext.connecteds.remove(channelContext);
		}

		groupContext.connections.remove(channelContext);
		groupContext.ips.unbind(channelContext);
		groupContext.ids.unbind(channelContext);
		

		close(channelContext);
	}

	/**
	 * 
	 * @param channelContext
	 * @author tanyaowu
	 */
	public static void close(ChannelContext channelContext) {
		GroupContext groupContext = channelContext.groupContext;
		groupContext.users.unbind(channelContext);
		groupContext.tokens.unbind(channelContext);
		groupContext.groups.unbind(channelContext);
		
		groupContext.bsIds.unbind(channelContext);
	}

	/**
	 * 
	 * @param groupContext
	 * @return
	 */
	public static Set<ChannelContext> createSet(Comparator<ChannelContext> comparator) {
		if (comparator == null) {
			return new HashSet<ChannelContext>();
		} else {
			return new TreeSet<ChannelContext>(comparator);
		}

	}

}
