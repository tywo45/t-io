/**
 * 
 */
package org.tio.core.ssl;

import org.tio.core.GroupContext;
import org.tio.core.intf.Packet;

/**
 * @author tanyaowu
 *
 */
public class SslUtils {

	/**
	 * 
	 */
	private SslUtils() {

	}

	/**
	 * 是否需要对这个packet进行SSL加密 
	 * @param packet
	 * @param groupContext
	 * @return
	 */
	public static boolean needSslEncrypt(Packet packet, GroupContext groupContext) {
		if (!packet.isSslEncrypted() && groupContext.sslConfig != null) {
			return true;
		}
		return false;
	}

	/**
	 * 是否是SSL连接
	 * @param groupContext
	 * @return
	 */
	public static boolean isSsl(GroupContext groupContext) {
		return groupContext.isSsl();
	}

}
