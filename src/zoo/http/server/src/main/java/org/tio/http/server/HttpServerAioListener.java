package org.tio.http.server;

import org.apache.commons.lang3.StringUtils;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.intf.Packet;
import org.tio.core.ssl.SslFacadeContext;
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
		SslFacadeContext sslFacadeContext = channelContext.sslFacadeContext;
		if ((sslFacadeContext == null || sslFacadeContext.isHandshakeCompleted())/** && packet instanceof HttpResponse*/
		) {
			HttpResponse httpResponse = (HttpResponse) packet;

			String Connection = httpResponse.getHeader(HttpConst.ResponseHeaderKey.Connection);
			// 现在基本都是1.1了，所以用close来判断
			if (StringUtils.equalsIgnoreCase(Connection, HttpConst.ResponseHeaderValue.Connection.close)) {
				HttpRequest request = httpResponse.getHttpRequest();
				String line = request.getRequestLine().getLine();
				Tio.remove(channelContext, "onAfterSent, " + line);
			}
		}
	}

	@Override
	public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {
		HttpRequest request = (HttpRequest) channelContext.getAttribute(HttpServerAioHandler.REQUEST_KEY);
		if (request != null) {
			request.setClosed(true);
		}
	}

	@Override
	public void onAfterHandled(ChannelContext channelContext, Packet packet, long cost) throws Exception {

	}

	@Override
	public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes) throws Exception {

	}
}
