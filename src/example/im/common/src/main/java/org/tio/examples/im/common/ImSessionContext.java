package org.tio.examples.im.common;

/**
 * 
 * @author tanyaowu 
 *
 */
public class ImSessionContext {

	/**
	 * 是否已经握过手
	 */
	private boolean isHandshaked = false;

	/**
	 * 是否是走了websocket协议
	 */
	private boolean isWebsocket = false;

	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2017年2月21日 上午10:27:54
	 * 
	 */
	public ImSessionContext() {

	}

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2017年2月21日 上午10:27:54
	 * 
	 */
	public static void main(String[] args) {

	}

	/**
	 * @return the isWebsocket
	 */
	public boolean isWebsocket() {
		return isWebsocket;
	}

	/**
	 * @param isWebsocket the isWebsocket to set
	 */
	public void setWebsocket(boolean isWebsocket) {
		this.isWebsocket = isWebsocket;
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

}
