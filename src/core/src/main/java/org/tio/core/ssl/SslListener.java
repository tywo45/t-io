/**
 * 
 */
package org.tio.core.ssl;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.core.ssl.facade.ISSLListener;

/**
 * @author tanyaowu
 *
 */
public class SslListener implements ISSLListener {
	private static Logger log = LoggerFactory.getLogger(SslListener.class);

	private ChannelContext channelContext = null;

	/**
	 * 
	 */
	public SslListener(ChannelContext channelContext) {
		this.channelContext = channelContext;
	}

	@Override
	public void onWrappedData(SslVo sslVo) {
		//Send these bytes via your host application's transport
		log.info("{}, 收到SSL加密后的数据，准备发送出去，{}", channelContext, sslVo);
		//		Packet packet = new Packet();
		//		packet.setPreEncodedByteBuffer(wrappedBytes);
		//		packet.setSslEncrypted(true);
		//		Tio.send(channelContext, packet);

		Object obj = sslVo.getObj();
		if (obj == null) { //如果是null，则是握手尚未完成时的数据
			Packet p = new Packet();

			p.setPreEncodedByteBuffer(sslVo.getByteBuffer());
			p.setSslEncrypted(true);
			//			p.setByteCount(sslVo.getByteBuffer().limit());

			//			log.info("clone packet:{}", Json.toJson(obj));

			boolean isAdded = channelContext.sendRunnable.addMsg(p);
			if (isAdded) {
				channelContext.groupContext.tioExecutor.execute(channelContext.sendRunnable);
			}
		} else {
			//应用数据的发送

			//			if (obj instanceof PacketWithMeta) {
			//				PacketWithMeta initPacketWithMeta = (PacketWithMeta)obj;
			//				PacketWithMeta clonePacketWithMeta = initPacketWithMeta.clone();
			//
			//				p = clonePacketWithMeta.getPacket();
			//				newObj = clonePacketWithMeta;
			//			} else {
			//				Packet initPacket = (Packet)obj;
			//				p = initPacket.clone();
			//				newObj = p;
			//			}
		}

	}

	@Override
	public void onPlainData(ByteBuffer plainBuffer) {
		//This is the deciphered payload for your app to consume
		//		ByteBuffer plainBytes = sslVo.getByteBuffer();
		SslFacadeContext sslFacadeContext = channelContext.sslFacadeContext;
		//plainBytes:java.nio.HeapByteBuffer[pos=0 lim=507 cap=507]

		if (sslFacadeContext.isHandshakeCompleted()) {
			log.info("{}, 收到SSL解密后的数据，SSL握手已经完成，准备解码，{}, isSSLHandshakeCompleted {}", channelContext, plainBuffer, sslFacadeContext.isHandshakeCompleted());
			//			plainBytes.flip();
			channelContext.decodeRunnable.setNewByteBuffer(plainBuffer);
			channelContext.decodeRunnable.run();
		} else {
			log.info("{}, 收到SSL解密后的数据，但SSL握手还没完成，{}, isSSLHandshakeCompleted {}", channelContext, plainBuffer, sslFacadeContext.isHandshakeCompleted());
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
