package org.tio.websocket.common;

import java.util.List;

import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;

/**
 *
 * @author tanyaowu
 *
 */
public class WsSessionContext {

	/**
	 * 是否已经握过手
	 */
	private boolean isHandshaked = false;

	/**
	 * websocket 握手请求包
	 */
	private HttpRequest handshakeRequest = null;

	/**
	 * websocket 握手响应包
	 */
	private HttpResponse handshakeResponse = null;

	private String token = null;

	//websocket 协议用到的，有时候数据包是分几个到的，注意那个fin字段，本im暂时不支持
	private List<byte[]> lastParts = null;

	/**
	 *
	 *
	 * @author tanyaowu
	 * 2017年2月21日 上午10:27:54
	 *
	 */
	public WsSessionContext() {

	}

	/**
	 * @return the handshakeRequest
	 */
	public HttpRequest getHandshakeRequest() {
		return handshakeRequest;
	}

	/**
	 * @return the handshakeResponse
	 */
	public HttpResponse getHandshakeResponse() {
		return handshakeResponse;
	}

	/**
	 * @return the lastPart
	 */
	public List<byte[]> getLastParts() {
		return lastParts;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @return the isHandshaked
	 */
	public boolean isHandshaked() {
		return isHandshaked;
	}

	/**
	 * @param isHandshaked the isHandshaked to set
	 */
	public void setHandshaked(boolean isHandshaked) {
		this.isHandshaked = isHandshaked;
	}

	/**
	 * @param handshakeRequest the handshakeRequest to set
	 */
	public void setHandshakeRequest(HttpRequest handshakeRequest) {
		this.handshakeRequest = handshakeRequest;
	}

	/**
	 * @param handshakeResponse the handshakeResponse to set
	 */
	public void setHandshakeResponse(HttpResponse handshakeResponse) {
		this.handshakeResponse = handshakeResponse;
	}

	/**
	 * @param lastParts the lastPart to set
	 */
	public void setLastParts(List<byte[]> lastParts) {
		this.lastParts = lastParts;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

}
