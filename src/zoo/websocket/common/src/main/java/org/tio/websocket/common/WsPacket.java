package org.tio.websocket.common;

import org.tio.core.intf.Packet;

/**
 *
 * @author tanyaowu
 * 2017年7月30日 上午10:09:51
 */
public class WsPacket extends Packet {

	//	private static Logger log = LoggerFactory.getLogger(WsPacket.class);
	//
	//	//不包含cookie的头部
	//	protected Map<String, String> headers = null;

	private static final long	serialVersionUID		= 4506947563506841436L;
	/**
	 * 消息体最多为多少
	 */
	public static final int		MAX_LENGTH_OF_BODY		= (int) (1024 * 1024 * 2.1);	//只支持多少M数据
	public static final int		MINIMUM_HEADER_LENGTH	= 2;
	public static final int		MAX_BODY_LENGTH			= 1024 * 512;					//最多接受的1024 * 512(半M)数据
	public static final String	CHARSET_NAME			= "utf-8";
	/**
	 * 是否是握手包
	 */
	private boolean				isHandShake				= false;
	/**
	 * 消息体
	 */
	private byte[]				body;
	/**
	 *  byte[][] bodys和body的作用一样，当业务数据用多个byte[]比较方便时，就可以用 byte[][] bodys
	 *  服务器发往客户端时，此字段才可能会有值(业务层进行性能优化时才用得着这个字段)
	 */
	private byte[][]			bodys;
	private boolean				wsEof;
	private Opcode				wsOpcode				= Opcode.BINARY;
	private boolean				wsHasMask;
	private long				wsBodyLength;
	private byte[]				wsMask;
	private String				wsBodyText;												//当为文本时才有此字段

	public WsPacket() {

	}

	public WsPacket(byte[] body) {
		this();
		this.body = body;
	}

	/**
	 * @return the body
	 */
	public byte[] getBody() {
		return body;
	}

	/**
	 * @return the wsBodyLength
	 */
	public long getWsBodyLength() {
		return wsBodyLength;
	}

	/**
	 * @return the wsBodyText
	 */
	public String getWsBodyText() {
		return wsBodyText;
	}

	/**
	 * @return the wsMask
	 */
	public byte[] getWsMask() {
		return wsMask;
	}

	/**
	 * @return the wsOpcode
	 */
	public Opcode getWsOpcode() {
		return wsOpcode;
	}

	/**
	 * 是否是握手包
	 * @return the isHandShake
	 */
	public boolean isHandShake() {
		return isHandShake;
	}

	/**
	 * @return the wsEof
	 */
	public boolean isWsEof() {
		return wsEof;
	}

	/**
	 * @return the wsHasMask
	 */
	public boolean isWsHasMask() {
		return wsHasMask;
	}

	/**
	 * @see org.tio.core.intf.Packet#logstr()
	 *
	 * @return
	 * @author tanyaowu
	 * 2017年2月22日 下午3:15:18
	 *
	 */
	@Override
	public String logstr() {
		return "websocket";

	}

	/**
	 * @param body the body to set
	 */
	public void setBody(byte[] body) {
		this.body = body;
	}

	/**
	 * @param isHandShake the isHandShake to set
	 */
	public void setHandShake(boolean isHandShake) {
		this.isHandShake = isHandShake;
	}

	/**
	 * @param wsBodyLength the wsBodyLength to set
	 */
	public void setWsBodyLength(long wsBodyLength) {
		this.wsBodyLength = wsBodyLength;
	}

	/**
	 * @param wsBodyText the wsBodyText to set
	 */
	public void setWsBodyText(String wsBodyText) {
		this.wsBodyText = wsBodyText;
	}

	/**
	 * @param wsEof the wsEof to set
	 */
	public void setWsEof(boolean wsEof) {
		this.wsEof = wsEof;
	}

	/**
	 * @param wsHasMask the wsHasMask to set
	 */
	public void setWsHasMask(boolean wsHasMask) {
		this.wsHasMask = wsHasMask;
	}

	/**
	 * @param wsMask the wsMask to set
	 */
	public void setWsMask(byte[] wsMask) {
		this.wsMask = wsMask;
	}

	/**
	 * @param wsOpcode the wsOpcode to set
	 */
	public void setWsOpcode(Opcode wsOpcode) {
		this.wsOpcode = wsOpcode;
	}

	public byte[][] getBodys() {
		return bodys;
	}

	public void setBodys(byte[][] bodys) {
		this.bodys = bodys;
	}

	//	/**
	//	 * @return the headers
	//	 */
	//	public Map<String, String> getHeaders() {
	//		return headers;
	//	}
	//
	//	/**
	//	 * @param headers the headers to set
	//	 */
	//	public void setHeaders(Map<String, String> headers) {
	//		this.headers = headers;
	//	}
}
