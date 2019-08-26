package org.tio.http.server;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.Tio;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.http.common.HttpConfig;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpRequestDecoder;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.HttpResponseEncoder;
import org.tio.http.common.handler.HttpRequestHandler;
import org.tio.server.intf.ServerAioHandler;

/**
 *
 * @author tanyaowu
 *
 */
public class HttpServerAioHandler implements ServerAioHandler {
	private static Logger		log			= LoggerFactory.getLogger(HttpServerAioHandler.class);
	public static final String	REQUEST_KEY	= "tio_request_key";
	protected HttpConfig		httpConfig;
	private HttpRequestHandler	requestHandler;

	/**
	 * @author tanyaowu
	 * 2016年11月18日 上午9:13:15
	 *
	 */
	public HttpServerAioHandler(HttpConfig httpConfig, HttpRequestHandler requestHandler) {
		this.httpConfig = httpConfig;
		this.requestHandler = requestHandler;
	}

	@Override
	public HttpRequest decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws AioDecodeException {
		HttpRequest request = HttpRequestDecoder.decode(buffer, limit, position, readableLength, channelContext, httpConfig);
		if (request != null) {
			channelContext.setAttribute(REQUEST_KEY, request);
		}
		return request;
	}

	@Override
	public ByteBuffer encode(Packet packet, TioConfig tioConfig, ChannelContext channelContext) {
		HttpResponse httpResponse = (HttpResponse) packet;
		ByteBuffer byteBuffer;
		try {
			byteBuffer = HttpResponseEncoder.encode(httpResponse, tioConfig, channelContext);
			return byteBuffer;
		} catch (UnsupportedEncodingException e) {
			log.error(e.toString(), e);
			return null;
		}
	}

	/**
	 * @return the httpConfig
	 */
	public HttpConfig getHttpConfig() {
		return httpConfig;
	}

	@Override
	public void handler(Packet packet, ChannelContext channelContext) throws Exception {
		HttpRequest request = (HttpRequest) packet;
		//		request.setHttpConfig(requestHandler.getHttpConfig(request));

		String ip = request.getClientIp();

		if (channelContext.tioConfig.ipBlacklist.isInBlacklist(ip)) {
			HttpResponse httpResponse = request.httpConfig.getRespForBlackIp();
			if (httpResponse != null) {
				Tio.send(channelContext, httpResponse);
				return;
			} else {
				Tio.remove(channelContext, ip + "在黑名单中");
				return;
			}
		}

		HttpResponse httpResponse = requestHandler.handler(request);
		if (httpResponse != null) {
			Tio.send(channelContext, httpResponse);
		} else {
			if (log.isInfoEnabled()) {
				log.info("{}, {}, handler return null, request line: {}", channelContext.tioConfig.getName(), channelContext.toString(), request.getRequestLine().toString());
			}
			//			Tio.remove(channelContext, "handler return null");
			request.close("handler return null");
		}
	}

	/**
	 * @param httpConfig the httpConfig to set
	 */
	public void setHttpConfig(HttpConfig httpConfig) {
		this.httpConfig = httpConfig;
	}

}
