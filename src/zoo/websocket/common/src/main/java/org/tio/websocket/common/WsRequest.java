package org.tio.websocket.common;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tanyaowu
 * 2017年7月30日 上午10:09:46
 */
public class WsRequest extends WsPacket {
	private static final Logger log = LoggerFactory.getLogger(WsRequest.class);

	private static final long serialVersionUID = -3361865570708714596L;

	public static WsRequest fromText(String text, String charset) {
		WsRequest wsRequest = new WsRequest();
		try {
			wsRequest.setBody(text.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			log.error(e.toString(), e);
		}
		wsRequest.setWsEof(true);
		wsRequest.setWsOpcode(Opcode.TEXT);
		return wsRequest;
	}

	public static WsRequest fromBytes(byte[] bytes) {
		WsRequest wsRequest = new WsRequest();
		wsRequest.setBody(bytes);
		wsRequest.setWsEof(true);
		wsRequest.setWsOpcode(Opcode.BINARY);
		return wsRequest;
	}
}
