package org.tio.websocket.server;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.Tio;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.http.common.HeaderName;
import org.tio.http.common.HeaderValue;
import org.tio.http.common.HttpConst;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpRequestDecoder;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.HttpResponseEncoder;
import org.tio.http.common.HttpResponseStatus;
import org.tio.server.intf.ServerAioHandler;
import org.tio.utils.hutool.StrUtil;
import org.tio.websocket.common.Opcode;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.common.WsResponse;
import org.tio.websocket.common.WsServerDecoder;
import org.tio.websocket.common.WsServerEncoder;
import org.tio.websocket.common.WsSessionContext;
import org.tio.websocket.common.util.BASE64Util;
import org.tio.websocket.common.util.SHA1Util;
import org.tio.websocket.server.handler.IWsMsgHandler;

/** @author tanyaowu */
public class WsServerAioHandler implements ServerAioHandler {
	private static Logger		log									= LoggerFactory.getLogger(WsServerAioHandler.class);
	/**
	 * value: List<WsRequest>
	 */
	private static final String	NOT_FINAL_WEBSOCKET_PACKET_PARTS	= "TIO_N_F_W_P_P";
	
	/**
	 * SEC_WEBSOCKET_KEY后缀
	 */
	private static final String SEC_WEBSOCKET_KEY_SUFFIX = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
	
	private static final byte[] SEC_WEBSOCKET_KEY_SUFFIX_BYTES = SEC_WEBSOCKET_KEY_SUFFIX.getBytes();

	private WsServerConfig wsServerConfig;

	private IWsMsgHandler wsMsgHandler;

	/**
	 * @param wsServerConfig
	 * @param wsMsgHandler
	 */
	public WsServerAioHandler(WsServerConfig wsServerConfig, IWsMsgHandler wsMsgHandler) {
		this.wsServerConfig = wsServerConfig;
		this.wsMsgHandler = wsMsgHandler;
	}

	@SuppressWarnings("unchecked")
	@Override
	public WsRequest decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws AioDecodeException {
		WsSessionContext wsSessionContext = (WsSessionContext) channelContext.get();
		//		int initPosition = buffer.position();

		if (!wsSessionContext.isHandshaked()) {//尚未握手
			HttpRequest request = HttpRequestDecoder.decode(buffer, limit, position, readableLength, channelContext, wsServerConfig);
			if (request == null) {
				return null;
			}

			HttpResponse httpResponse = updateWebSocketProtocol(request, channelContext);
			if (httpResponse == null) {
				throw new AioDecodeException("http协议升级到websocket协议失败");
			}

			wsSessionContext.setHandshakeRequest(request);
			wsSessionContext.setHandshakeResponse(httpResponse);

			WsRequest wsRequestPacket = new WsRequest();
			//			wsRequestPacket.setHeaders(httpResponse.getHeaders());
			//			wsRequestPacket.setBody(httpResponse.getBody());
			wsRequestPacket.setHandShake(true);

			return wsRequestPacket;
		}

		WsRequest websocketPacket = WsServerDecoder.decode(buffer, channelContext);

		if (websocketPacket != null) {
			if (!websocketPacket.isWsEof()) {  //数据包尚未完成
				List<WsRequest> parts = (List<WsRequest>) channelContext.getAttribute(NOT_FINAL_WEBSOCKET_PACKET_PARTS);
				if (parts == null) {
					parts = new ArrayList<>();
					channelContext.setAttribute(NOT_FINAL_WEBSOCKET_PACKET_PARTS, parts);
				}
				parts.add(websocketPacket);
			} else {
				List<WsRequest> parts = (List<WsRequest>) channelContext.getAttribute(NOT_FINAL_WEBSOCKET_PACKET_PARTS);
				if (parts != null) {
					channelContext.setAttribute(NOT_FINAL_WEBSOCKET_PACKET_PARTS, null);
					
					parts.add(websocketPacket);
					WsRequest first = parts.get(0);
					websocketPacket.setWsOpcode(first.getWsOpcode());
					
					int allBodyLength = 0;
					for (WsRequest wsRequest : parts) {
						allBodyLength += wsRequest.getBody().length;
					}
					
					byte[] allBody = new byte[allBodyLength];
					Integer index = 0;
					for (WsRequest wsRequest : parts) {
						System.arraycopy(wsRequest.getBody(), 0, allBody, index, wsRequest.getBody().length);
						index += wsRequest.getBody().length;
					}
					websocketPacket.setBody(allBody);
				}
				
				HttpRequest handshakeRequest = wsSessionContext.getHandshakeRequest();
				if (websocketPacket.getWsOpcode() != Opcode.BINARY) {
					byte[] bodyBs = websocketPacket.getBody();
					if (bodyBs != null) {
						try {
							String text = new String(bodyBs, handshakeRequest.getCharset());
							websocketPacket.setWsBodyText(text);
						} catch (UnsupportedEncodingException e) {
							log.error(e.toString(), e);
						}
					}
				}
			}
		}

		return websocketPacket;
	}

	@Override
	public ByteBuffer encode(Packet packet, TioConfig tioConfig, ChannelContext channelContext) {
		WsResponse wsResponse = (WsResponse) packet;

		// 握手包
		if (wsResponse.isHandShake()) {
			WsSessionContext imSessionContext = (WsSessionContext) channelContext.get();
			HttpResponse handshakeResponse = imSessionContext.getHandshakeResponse();
			try {
				return HttpResponseEncoder.encode(handshakeResponse, tioConfig, channelContext);
			} catch (UnsupportedEncodingException e) {
				log.error(e.toString(), e);
				return null;
			}
		}

		ByteBuffer byteBuffer = WsServerEncoder.encode(wsResponse, tioConfig, channelContext);
		return byteBuffer;
	}

	/** @return the httpConfig */
	public WsServerConfig getHttpConfig() {
		return wsServerConfig;
	}

	private WsResponse h(WsRequest websocketPacket, byte[] bytes, Opcode opcode, ChannelContext channelContext) throws Exception {
		WsResponse wsResponse = null;
		if (opcode == Opcode.TEXT) {
			if (bytes == null || bytes.length == 0) {
				Tio.remove(channelContext, "错误的websocket包，body为空");
				return null;
			}
			String text = new String(bytes, wsServerConfig.getCharset());
			Object retObj = wsMsgHandler.onText(websocketPacket, text, channelContext);
			String methodName = "onText";
			wsResponse = processRetObj(retObj, methodName, channelContext);
			return wsResponse;
		} else if (opcode == Opcode.BINARY) {
			if (bytes == null || bytes.length == 0) {
				Tio.remove(channelContext, "错误的websocket包，body为空");
				return null;
			}
			Object retObj = wsMsgHandler.onBytes(websocketPacket, bytes, channelContext);
			String methodName = "onBytes";
			wsResponse = processRetObj(retObj, methodName, channelContext);
			return wsResponse;
		} else if (opcode == Opcode.PING || opcode == Opcode.PONG) {
			log.debug("收到" + opcode);
			return null;
		} else if (opcode == Opcode.CLOSE) {
			Object retObj = wsMsgHandler.onClose(websocketPacket, bytes, channelContext);
			String methodName = "onClose";
			wsResponse = processRetObj(retObj, methodName, channelContext);
			return wsResponse;
		} else {
			Tio.remove(channelContext, "错误的websocket包，错误的Opcode");
			return null;
		}
	}

	@Override
	public void handler(Packet packet, ChannelContext channelContext) throws Exception {

		WsRequest wsRequest = (WsRequest) packet;

		if (wsRequest.isHandShake()) {//是握手包
			WsSessionContext wsSessionContext = (WsSessionContext) channelContext.get();
			HttpRequest request = wsSessionContext.getHandshakeRequest();
			HttpResponse httpResponse = wsSessionContext.getHandshakeResponse();
			HttpResponse r = wsMsgHandler.handshake(request, httpResponse, channelContext);
			if (r == null) {
				Tio.remove(channelContext, "业务层不同意握手");
				return;
			}
			wsSessionContext.setHandshakeResponse(r);

			WsResponse wsResponse = new WsResponse();
			wsResponse.setHandShake(true);
			Tio.send(channelContext, wsResponse);
			wsSessionContext.setHandshaked(true);

			wsMsgHandler.onAfterHandshaked(request, httpResponse, channelContext);
			return;
		}

		if (!wsRequest.isWsEof()) {
			return;
		}

		WsResponse wsResponse = h(wsRequest, wsRequest.getBody(), wsRequest.getWsOpcode(), channelContext);

		if (wsResponse != null) {
			Tio.send(channelContext, wsResponse);
		}

		return;
	}

	private WsResponse processRetObj(Object obj, String methodName, ChannelContext channelContext) throws Exception {
		WsResponse wsResponse = null;
		if (obj == null) {
			return null;
		} else {
			if (obj instanceof String) {
				String str = (String) obj;
				wsResponse = WsResponse.fromText(str, wsServerConfig.getCharset());
				return wsResponse;
			} else if (obj instanceof byte[]) {
				wsResponse = WsResponse.fromBytes((byte[]) obj);
				return wsResponse;
			} else if (obj instanceof WsResponse) {
				return (WsResponse) obj;
			} else if (obj instanceof ByteBuffer) {
				byte[] bs = ((ByteBuffer) obj).array();
				wsResponse = WsResponse.fromBytes(bs);
				return wsResponse;
			} else {
				log.error("{} {}.{}()方法，只允许返回byte[]、ByteBuffer、WsResponse或null，但是程序返回了{}", channelContext, this.getClass().getName(), methodName, obj.getClass().getName());
				return null;
			}
		}
	}

	/** @param httpConfig the httpConfig to set */
	public void setHttpConfig(WsServerConfig httpConfig) {
		this.wsServerConfig = httpConfig;
	}

	/**
	 * 本方法改编自baseio: https://gitee.com/generallycloud/baseio<br>
	 * 感谢开源作者的付出
	 *
	 * @param request
	 * @param channelContext
	 * @return
	 * @author tanyaowu
	 */
	public static HttpResponse updateWebSocketProtocol(HttpRequest request, ChannelContext channelContext) {
		Map<String, String> headers = request.getHeaders();

		String Sec_WebSocket_Key = headers.get(HttpConst.RequestHeaderKey.Sec_WebSocket_Key);

		if (StrUtil.isNotBlank(Sec_WebSocket_Key)) {
			byte[] Sec_WebSocket_Key_Bytes = null;
			try {
				Sec_WebSocket_Key_Bytes = Sec_WebSocket_Key.getBytes(request.getCharset());
			} catch (UnsupportedEncodingException e) {
//				log.error(e.toString(), e);
				throw new RuntimeException(e);
			}
			byte[] allBs = new byte[Sec_WebSocket_Key_Bytes.length + SEC_WEBSOCKET_KEY_SUFFIX_BYTES.length];
			System.arraycopy(Sec_WebSocket_Key_Bytes, 0, allBs, 0, Sec_WebSocket_Key_Bytes.length);
			System.arraycopy(SEC_WEBSOCKET_KEY_SUFFIX_BYTES, 0, allBs, Sec_WebSocket_Key_Bytes.length, SEC_WEBSOCKET_KEY_SUFFIX_BYTES.length);
			
//			String Sec_WebSocket_Key_Magic = Sec_WebSocket_Key + SEC_WEBSOCKET_KEY_SUFFIX_BYTES;
			byte[] key_array = SHA1Util.SHA1(allBs);
			String acceptKey = BASE64Util.byteArrayToBase64(key_array);
			HttpResponse httpResponse = new HttpResponse(request);

			httpResponse.setStatus(HttpResponseStatus.C101);

			Map<HeaderName, HeaderValue> respHeaders = new HashMap<>();
			respHeaders.put(HeaderName.Connection, HeaderValue.Connection.Upgrade);
			respHeaders.put(HeaderName.Upgrade, HeaderValue.Upgrade.WebSocket);
			respHeaders.put(HeaderName.Sec_WebSocket_Accept, HeaderValue.from(acceptKey));
			httpResponse.addHeaders(respHeaders);
			return httpResponse;
		}
		return null;
	}
}
