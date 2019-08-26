package org.tio.websocket.common;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tanyaowu
 * 2017年7月30日 上午10:09:59
 */
public class WsResponse extends WsPacket {
	private static Logger log = LoggerFactory.getLogger(WsResponse.class);

	private static final long serialVersionUID = 963847148301021559L;

	public static WsResponse fromText(String text, String charset) {
		WsResponse wsResponse = new WsResponse();
		try {
			wsResponse.setBody(text.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			log.error(e.toString(), e);
		}
		wsResponse.setWsOpcode(Opcode.TEXT);
		return wsResponse;
	}

	public static WsResponse fromBytes(byte[] bytes) {
		WsResponse wsResponse = new WsResponse();
		wsResponse.setBody(bytes);
		wsResponse.setWsOpcode(Opcode.BINARY);
		return wsResponse;
	}
}
