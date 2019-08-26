package org.tio.websocket.server.handler;

import org.tio.core.ChannelContext;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.websocket.common.WsRequest;

/**
 *
 * @author tanyaowu
 * 2017年7月30日 上午9:34:59
 */
public interface IWsMsgHandler {
	/**
	 * <li>对httpResponse参数进行补充并返回，如果返回null表示不想和对方建立连接，框架会断开连接，如果返回非null，框架会把这个对象发送给对方</li>
	 * <li>注：请不要在这个方法中向对方发送任何消息，因为这个时候握手还没完成，发消息会导致协议交互失败。</li>
	 * <li>对于大部分业务，该方法只需要一行代码：return httpResponse;</li>
	 * @param httpRequest
	 * @param httpResponse
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author tanyaowu
	 */
	public HttpResponse handshake(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) throws Exception;

	/**
	 * 握手成功后触发该方法
	 * @param httpRequest
	 * @param httpResponse
	 * @param channelContext
	 * @throws Exception
	 * @author tanyaowu
	 */
	public void onAfterHandshaked(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) throws Exception;

	/**
	 * <li>当收到Opcode.BINARY消息时，执行该方法。也就是说如何你的ws是基于BINARY传输的，就会走到这个方法</li>
	 * @param wsRequest
	 * @param bytes
	 * @param channelContext
	 * @return 可以是WsResponse、byte[]、ByteBuffer、String或null，如果是null，框架不会回消息
	 * @throws Exception
	 * @author tanyaowu
	 */
	Object onBytes(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception;

	/**
	 * 当收到Opcode.CLOSE时，执行该方法，业务层在该方法中一般不需要写什么逻辑，空着就好
	 * @param wsRequest
	 * @param bytes
	 * @param channelContext
	 * @return 可以是WsResponse、byte[]、ByteBuffer、String或null，如果是null，框架不会回消息
	 * @throws Exception
	 * @author tanyaowu
	 */
	Object onClose(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception;

	/**
	 * <li>当收到Opcode.TEXT消息时，执行该方法。也就是说如何你的ws是基于TEXT传输的，就会走到这个方法</li>
	 * @param wsRequest
	 * @param text
	 * @param channelContext
	 * @return 可以是WsResponse、byte[]、ByteBuffer、String或null，如果是null，框架不会回消息
	 * @throws Exception
	 * @author tanyaowu
	 */
	Object onText(WsRequest wsRequest, String text, ChannelContext channelContext) throws Exception;
}
