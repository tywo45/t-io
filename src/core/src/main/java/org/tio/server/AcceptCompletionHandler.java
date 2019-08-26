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
			ServerTioConfig serverTioConfig = tioServer.getServerTioConfig();
			InetSocketAddress inetSocketAddress = (InetSocketAddress) asynchronousSocketChannel.getRemoteAddress();
			String clientIp = inetSocketAddress.getHostString();
			//			serverTioConfig.ips.get(clientIp).getRequestCount().incrementAndGet();

			//			CaffeineCache[] caches = serverTioConfig.ips.getCaches();
			//			for (CaffeineCache guavaCache : caches) {
			//				IpStat ipStat = (IpStat) guavaCache.get(clientIp);
			//				ipStat.getRequestCount().incrementAndGet();
			//			}

			if (org.tio.core.Tio.IpBlacklist.isInBlacklist(serverTioConfig, clientIp)) {
				log.info("{}在黑名单中, {}", clientIp, serverTioConfig.getName());
				asynchronousSocketChannel.close();
				return;
			}

			if (serverTioConfig.statOn) {
				((ServerGroupStat) serverTioConfig.groupStat).accepted.incrementAndGet();
			}

			//			channelContext.getIpStat().getActivatedCount().incrementAndGet();
			//			for (CaffeineCache guavaCache : caches) {
			//				IpStat ipStat = (IpStat) guavaCache.get(clientIp);
			//				ipStat.getActivatedCount().incrementAndGet();
			//			}
			//			for (Long v : durationList) {
			//				IpStat ipStat = (IpStat) serverTioConfig.ips.get(v, clientIp);
			//				IpStat.getActivatedCount().incrementAndGet();
			//			}
			//			IpStat.getActivatedCount(clientIp, true).incrementAndGet();

			asynchronousSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			asynchronousSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 64 * 1024);
			asynchronousSocketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 64 * 1024);
			asynchronousSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

			ServerChannelContext channelContext = new ServerChannelContext(serverTioConfig, asynchronousSocketChannel);
			channelContext.setClosed(false);
			channelContext.stat.setTimeFirstConnected(SystemTimer.currTime);
			channelContext.setServerNode(tioServer.getServerNode());

			//			channelContext.traceClient(ChannelAction.CONNECT, null, null);

			//			serverTioConfig.connecteds.add(channelContext);
			serverTioConfig.ips.bind(channelContext);

			boolean isConnected = true;
			boolean isReconnect = false;
			if (serverTioConfig.getServerAioListener() != null) {
				if (!SslUtils.isSsl(channelContext.tioConfig)) {
					try {
						serverTioConfig.getServerAioListener().onAfterConnected(channelContext, isConnected, isReconnect);
					} catch (Throwable e) {
						log.error(e.toString(), e);
					}
				}
			}

			if (serverTioConfig.ipStats.durationList != null && serverTioConfig.ipStats.durationList.size() > 0) {
				try {
					for (Long v : serverTioConfig.ipStats.durationList) {
						IpStat ipStat = (IpStat) serverTioConfig.ipStats.get(v, channelContext);
						ipStat.getRequestCount().incrementAndGet();
						serverTioConfig.getIpStatListener().onAfterConnected(channelContext, isConnected, isReconnect, ipStat);
					}
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
			}

			if (!tioServer.isWaitingStop()) {
				ReadCompletionHandler readCompletionHandler = channelContext.getReadCompletionHandler();
				ByteBuffer readByteBuffer = readCompletionHandler.getReadByteBuffer();//ByteBuffer.allocateDirect(channelContext.tioConfig.getReadBufferSize());
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
