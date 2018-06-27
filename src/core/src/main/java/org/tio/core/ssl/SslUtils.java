/**
 * 
 */
package org.tio.core.ssl;

import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;

/**
 * @author tanyaowu
 *
 */
public class SslUtils {

	/**
	 * 
	 */
	public SslUtils() {
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	
	/**
	 * 是否需要进行SSL加密 
	 * @param packet
	 * @param channelContext
	 * @return
	 */
	public static boolean needSslEncrypt(Packet packet, ChannelContext channelContext) {
		if (!packet.isSslEncrypted() && channelContext.sslFacadeContext != null) {
			return true;
		}
		return false;
	}
	
//	/**
//	 * 是否需要进行SSL加密 
//	 * @param obj PacketWithMeta or Packet
//	 * @param channelContext
//	 * @return
//	 */
//	public static boolean needSslEncrypt(Packet packet, ChannelContext channelContext) {
//		if (obj instanceof Packet) {
//			return needSslEncrypt((Packet)obj, channelContext);
//		} else {
//			PacketWithMeta packetWithMeta = (PacketWithMeta)obj;
//			return needSslEncrypt(packetWithMeta.getPacket(), channelContext);
//		}
//	}
	
	/**
	 * 是否 是SSL连接
	 * @param channelContext
	 * @return
	 */
	public static boolean isSsl(ChannelContext channelContext) {
		return channelContext.sslFacadeContext != null;
	}

}
