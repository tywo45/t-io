package org.tio.http.server;

import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.intf.Packet;
import org.tio.http.common.HttpConst;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.server.intf.ServerAioListener;

/**
 * HTTP ServerAioListener
 * @author tanyaowu
 */
public class HttpServerAioListener implements ServerAioListener {

	public HttpServerAioListener() {
	}

	@Override
	public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) {
		return;
	}

	@Override
	public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize) {

	}

	@Override
	public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) {
		//		if ((channelContext.sslFacadeContext == null || channelContext.sslFacadeContext.isHandshakeCompleted())/** && packet instanceof HttpResponse*/
		//		) {}

		HttpResponse httpResponse = (HttpResponse) packet;
		HttpRequest request = httpResponse.getHttpRequest();
		//		String connection = request.getConnection();

		if (request != null) {
			if (request.httpConfig.compatible1_0) {
				switch (request.requestLine.version) {
				case HttpConst.HttpVersion.V1_0:
					if (!HttpConst.RequestHeaderValue.Connection.keep_alive.equals(request.getConnection())) {
						Tio.remove(channelContext, "http 请求头Connection!=keep-alive：" + request.getRequestLine());
					}
					break;

				default:
					if (HttpConst.RequestHeaderValue.Connection.close.equals(request.getConnection())) {
						Tio.remove(channelContext, "http 请求头Connection=close：" + request.getRequestLine());
					}
					break;
				}
			} else {
				if (HttpConst.RequestHeaderValue.Connection.close.equals(request.getConnection())) {
					Tio.remove(channelContext, "http 请求头Connection=close：" + request.getRequestLine());
				}
			}
		}
	}

	@Override
	public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {
		//		HttpRequest request = (HttpRequest) channelContext.getAttribute(HttpServerAioHandler.REQUEST_KEY);
		//		if (request != null) {
		//			request.setClosed(true);
		//		}
	}

	@Override
	public void onAfterHandled(ChannelContext channelContext, Packet packet, long cost) throws Exception {

	}

	@Override
	public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes) throws Exception {

	}

	@Override
	public boolean onHeartbeatTimeout(ChannelContext channelContext, Long interval, int heartbeatTimeoutCount) {
		return false;
	}
}
