package org.tio.core.maintain;

import java.io.File;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.ClientGroupContext;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;

/**
 * 
 * @author tanyaowu 
 * 2017年10月19日 上午9:40:34
 */
public class MaintainUtils {
	
	private static final Logger log = LoggerFactory.getLogger(MaintainUtils.class);

	public static final String TEMP_DIR = "tiotemp/";

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
			ClientGroupContext clientGroupContext = (ClientGroupContext) groupContext;
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
		deleteTempDir(channelContext);
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

	public static void deleteTempDir(ChannelContext channelContext) {
		try {
			String dir = channelContext.getId();
			File dirFile = new File(TEMP_DIR + dir);
			dirFile.deleteOnExit();
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}

	/**
	 * 
	 * @param channelContext
	 * @return
	 * @author tanyaowu
	 */
	public static File createTempDir(ChannelContext channelContext) {
		String dir = channelContext.getId();
		File dirFile = new File(TEMP_DIR + dir);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		return dirFile;
	}

	public static File tempReceivedFile(ChannelContext channelContext) {
		File tempDir = createTempDir(channelContext);
		File tempReceivedFile = new File(tempDir, "received");
		return tempReceivedFile;
	}

	public static File tempWriteFile(ChannelContext channelContext) {
		File tempDir = createTempDir(channelContext);
		File tempReceivedFile = new File(tempDir, "write");
		return tempReceivedFile;
	}

}
