package org.tio.server;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ReadCompletionHandler;
import org.tio.core.ssl.SslUtils;
import org.tio.core.stat.IpStat;
import org.tio.utils.SystemTimer;

/**
 *
 * @author tanyaowu
 * 2017年4月4日 上午9:27:45
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, TioServer> {

	private static Logger log = LoggerFactory.getLogger(AcceptCompletionHandler.class);

	public AcceptCompletionHandler() {
	}

	/**
	 *
	 * @param asynchronousSocketChannel
	 * @param tioServer
	 * @author tanyaowu
	 */
	@Override
	public void completed(AsynchronousSocketChannel asynchronousSocketChannel, TioServer tioServer) {
		try {
			ServerGroupContext serverGroupContext = tioServer.getServerGroupContext();
			InetSocketAddress inetSocketAddress = (InetSocketAddress) asynchronousSocketChannel.getRemoteAddress();
			String clientIp = inetSocketAddress.getHostString();
			//			serverGroupContext.ips.get(clientIp).getRequestCount().incrementAndGet();

			//			CaffeineCache[] caches = serverGroupContext.ips.getCaches();
			//			for (CaffeineCache guavaCache : caches) {
			//				IpStat ipStat = (IpStat) guavaCache.get(clientIp);
			//				ipStat.getRequestCount().incrementAndGet();
			//			}

			if (org.tio.core.Tio.IpBlacklist.isInBlacklist(serverGroupContext, clientIp)) {
				log.warn("[{}]在黑名单中", clientIp);
				asynchronousSocketChannel.close();
				return;
			}

			if (serverGroupContext.statOn) {
				((ServerGroupStat) serverGroupContext.groupStat).accepted.incrementAndGet();
			}

			//			channelContext.getIpStat().getActivatedCount().incrementAndGet();
			//			for (CaffeineCache guavaCache : caches) {
			//				IpStat ipStat = (IpStat) guavaCache.get(clientIp);
			//				ipStat.getActivatedCount().incrementAndGet();
			//			}
			//			for (Long v : durationList) {
			//				IpStat ipStat = (IpStat) serverGroupContext.ips.get(v, clientIp);
			//				IpStat.getActivatedCount().incrementAndGet();
			//			}
			//			IpStat.getActivatedCount(clientIp, true).incrementAndGet();

			asynchronousSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			asynchronousSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 64 * 1024);
			asynchronousSocketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 64 * 1024);
			asynchronousSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

			ServerChannelContext channelContext = new ServerChannelContext(serverGroupContext, asynchronousSocketChannel);
			channelContext.setClosed(false);
			channelContext.stat.setTimeFirstConnected(SystemTimer.currTime);
			channelContext.setServerNode(tioServer.getServerNode());

			//			channelContext.traceClient(ChannelAction.CONNECT, null, null);

			//			serverGroupContext.connecteds.add(channelContext);
			serverGroupContext.ips.bind(channelContext);

			boolean isConnected = true;
			boolean isReconnect = false;
			if (serverGroupContext.getServerAioListener() != null) {
				if (!SslUtils.isSsl(channelContext.groupContext)) {
					try {
						serverGroupContext.getServerAioListener().onAfterConnected(channelContext, isConnected, isReconnect);
					} catch (Throwable e) {
						log.error(e.toString(), e);
					}
				}
			}

			if (serverGroupContext.ipStats.durationList != null && serverGroupContext.ipStats.durationList.size() > 0) {
				try {
					for (Long v : serverGroupContext.ipStats.durationList) {
						IpStat ipStat = (IpStat) serverGroupContext.ipStats.get(v, clientIp);
						ipStat.getRequestCount().incrementAndGet();
						serverGroupContext.getIpStatListener().onAfterConnected(channelContext, isConnected, isReconnect, ipStat);
					}
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
			}

			if (!tioServer.isWaitingStop()) {
				ReadCompletionHandler readCompletionHandler = channelContext.getReadCompletionHandler();
				ByteBuffer readByteBuffer = readCompletionHandler.getReadByteBuffer();//ByteBuffer.allocateDirect(channelContext.groupContext.getReadBufferSize());
				readByteBuffer.position(0);
				readByteBuffer.limit(readByteBuffer.capacity());
				asynchronousSocketChannel.read(readByteBuffer, readByteBuffer, readCompletionHandler);
			}
		} catch (Throwable e) {
			log.error("", e);
		} finally {
			if (tioServer.isWaitingStop()) {
				log.info("{}即将关闭服务器，不再接受新请求", tioServer.getServerNode());
			} else {
				AsynchronousServerSocketChannel serverSocketChannel = tioServer.getServerSocketChannel();
				serverSocketChannel.accept(tioServer, this);
			}
		}
	}

	/**
	 *
	 * @param exc
	 * @param tioServer
	 * @author tanyaowu
	 */
	@Override
	public void failed(Throwable exc, TioServer tioServer) {
		AsynchronousServerSocketChannel serverSocketChannel = tioServer.getServerSocketChannel();
		serverSocketChannel.accept(tioServer, this);

		log.error("[" + tioServer.getServerNode() + "]监听出现异常", exc);

	}

}
