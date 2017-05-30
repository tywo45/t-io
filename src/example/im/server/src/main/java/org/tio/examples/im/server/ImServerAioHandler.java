package org.tio.examples.im.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.examples.im.common.CommandStat;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.http.HttpRequestDecoder;
import org.tio.examples.im.common.http.HttpRequestPacket;
import org.tio.examples.im.common.http.HttpResponseEncoder;
import org.tio.examples.im.common.http.HttpResponsePacket;
import org.tio.examples.im.common.http.websocket.WebsocketDecoder;
import org.tio.examples.im.common.http.websocket.WebsocketEncoder;
import org.tio.examples.im.common.http.websocket.WebsocketPacket;
import org.tio.examples.im.common.http.websocket.WebsocketPacket.Opcode;
import org.tio.examples.im.common.packets.Command;
import org.tio.examples.im.common.utils.GzipUtils;
import org.tio.examples.im.server.handler.AuthReqHandler;
import org.tio.examples.im.server.handler.ChatReqHandler;
import org.tio.examples.im.server.handler.CloseReqHandler;
import org.tio.examples.im.server.handler.HandshakeReqHandler;
import org.tio.examples.im.server.handler.HeartbeatReqHandler;
import org.tio.examples.im.server.handler.ImBsHandlerIntf;
import org.tio.examples.im.server.handler.JoinReqHandler;
import org.tio.server.intf.ServerAioHandler;

/**
 * 
 * @author tanyaowu 
 *
 */
public class ImServerAioHandler implements ServerAioHandler<ImSessionContext, ImPacket, Object> {
	private static Logger log = LoggerFactory.getLogger(ImServerAioHandler.class);

	private static Map<Command, ImBsHandlerIntf> handlerMap = new HashMap<>();
	static {
		handlerMap.put(Command.COMMAND_HANDSHAKE_REQ, new HandshakeReqHandler());
		handlerMap.put(Command.COMMAND_AUTH_REQ, new AuthReqHandler());
		handlerMap.put(Command.COMMAND_CHAT_REQ, new ChatReqHandler());
		handlerMap.put(Command.COMMAND_JOIN_GROUP_REQ, new JoinReqHandler());
		handlerMap.put(Command.COMMAND_HEARTBEAT_REQ, new HeartbeatReqHandler());
		handlerMap.put(Command.COMMAND_CLOSE_REQ, new CloseReqHandler());

	}

	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2016年11月18日 上午9:13:15
	 * 
	 */
	public ImServerAioHandler() {
	}

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2016年11月18日 上午9:13:15
	 * 
	 */
	public static void main(String[] args) {
	}

	/** 
	 * @see org.tio.core.intf.AioHandler#handler(org.tio.core.intf.Packet)
	 * 
	 * @param packet
	 * @return
	 * @throws Exception 
	 * @author: tanyaowu
	 * 2016年11月18日 上午9:37:44
	 * 
	 */
	@Override
	public Object handler(ImPacket packet, ChannelContext<ImSessionContext, ImPacket, Object> channelContext) throws Exception {
		Command command = packet.getCommand();
		ImBsHandlerIntf handler = handlerMap.get(command);
		if (handler != null) {
			Object obj = handler.handler(packet, channelContext);
			CommandStat.getCount(command).handled.incrementAndGet();
			return obj;
		} else {
			CommandStat.getCount(command).handled.incrementAndGet();
			log.warn("找不到对应的命令码[{}]处理类", command);
			return null;
		}

	}

	/** 
	 * @see org.tio.core.intf.AioHandler#encode(org.tio.core.intf.Packet)
	 * 
	 * @param packet
	 * @return
	 * @author: tanyaowu
	 * 2016年11月18日 上午9:37:44
	 * 
	 */
	@Override
	public ByteBuffer encode(ImPacket packet, GroupContext<ImSessionContext, ImPacket, Object> groupContext, ChannelContext<ImSessionContext, ImPacket, Object> channelContext) {
		ImSessionContext imSessionContext = channelContext.getSessionContext();
		boolean isWebsocket = imSessionContext.isWebsocket();

		if (packet.getCommand() == Command.COMMAND_HANDSHAKE_RESP) {
			if (isWebsocket) {
				return HttpResponseEncoder.encode((HttpResponsePacket) packet, groupContext, channelContext);
			} else {
				ByteBuffer buffer = ByteBuffer.allocate(1);
				buffer.put(ImPacket.HANDSHAKE_BYTE);
				return buffer;
			}
		}

		if (isWebsocket) {
			return WebsocketEncoder.encode(packet, groupContext, channelContext);
		}

		byte[] body = packet.getBody();
		int bodyLen = 0;
		boolean isCompress = false;
		boolean is4ByteLength = false;
		if (body != null) {
			bodyLen = body.length;

			if (bodyLen > 200) {
				try {
					byte[] gzipedbody = GzipUtils.gZip(body);
					if (gzipedbody.length < body.length) {
						log.error("压缩前:{}, 压缩后:{}", body.length, gzipedbody.length);
						body = gzipedbody;
						packet.setBody(gzipedbody);
						bodyLen = gzipedbody.length;
						isCompress = true;
					}
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}

			if (bodyLen > Short.MAX_VALUE) {
				is4ByteLength = true;
			}
		}

		int allLen = packet.calcHeaderLength(is4ByteLength) + bodyLen;

		ByteBuffer buffer = ByteBuffer.allocate(allLen);
		buffer.order(groupContext.getByteOrder());

		byte firstbyte = ImPacket.encodeCompress(ImPacket.VERSION, isCompress);
		firstbyte = ImPacket.encodeHasSynSeq(firstbyte, packet.getSynSeq() > 0);
		firstbyte = ImPacket.encode4ByteLength(firstbyte, is4ByteLength);
		//		String bstr = Integer.toBinaryString(firstbyte);
		//		log.error("二进制:{}",bstr);

		buffer.put(firstbyte);
		buffer.put((byte) packet.getCommand().getNumber());

		//GzipUtils

		if (is4ByteLength) {
			buffer.putInt(bodyLen);
		} else {
			buffer.putShort((short) bodyLen);
		}

		if (packet.getSynSeq() != null && packet.getSynSeq() > 0) {
			buffer.putInt(packet.getSynSeq());
		}
		//		else
		//		{
		//			buffer.putInt(0);
		//		}
		//
		//		buffer.putInt(0);

		if (body != null) {
			buffer.put(body);
		}
		return buffer;
	}

	/**
	 * 心跳
	 */
	private static ImPacket heartbeatPacket = new ImPacket(Command.COMMAND_HEARTBEAT_REQ);

	/**
	 * 握手
	 */
	private static ImPacket handshakePacket = new ImPacket(Command.COMMAND_HANDSHAKE_REQ);

	/** 
	 * @see org.tio.core.intf.AioHandler#decode(java.nio.ByteBuffer)
	 * 
	 * @param buffer
	 * @return
	 * @throws AioDecodeException
	 * @author: tanyaowu
	 * 2016年11月18日 上午9:37:44
	 * 
	 */
	@Override
	public ImPacket decode(ByteBuffer buffer, ChannelContext<ImSessionContext, ImPacket, Object> channelContext) throws AioDecodeException {
		ImSessionContext imSessionContext = channelContext.getSessionContext();
		int initPosition = buffer.position();
		byte firstbyte = buffer.get(initPosition);

		if (!imSessionContext.isHandshaked()) {
			if (ImPacket.HANDSHAKE_BYTE == firstbyte) {
				buffer.position(1 + initPosition);
				return handshakePacket;
			} else {
				HttpRequestPacket httpRequestPacket = HttpRequestDecoder.decode(buffer);
				if (httpRequestPacket == null) {
					return null;
				}

				httpRequestPacket.setCommand(Command.COMMAND_HANDSHAKE_REQ);
				imSessionContext.setWebsocket(true);
				return httpRequestPacket;
			}
		}

		boolean isWebsocket = imSessionContext.isWebsocket();

		if (isWebsocket) {
			WebsocketPacket websocketPacket = WebsocketDecoder.decode(buffer, channelContext);
			if (websocketPacket == null) {
				return null;
			}

			Opcode opcode = websocketPacket.getWsOpcode();
			if (opcode == Opcode.BINARY) {
				byte[] wsBody = websocketPacket.getWsBody();
				if (wsBody == null || wsBody.length == 0) {
					throw new AioDecodeException("错误的websocket包，body为空");
				}

				Command command = Command.forNumber(wsBody[0]);
				ImPacket imPacket = new ImPacket(command);

				if (wsBody.length > 1) {
					byte[] dst = new byte[wsBody.length - 1];
					System.arraycopy(wsBody, 1, dst, 0, dst.length);
					imPacket.setBody(dst);
				}
				return imPacket;
			} else if (opcode == Opcode.PING || opcode == Opcode.PONG) {
				return heartbeatPacket;
			} else if (opcode == Opcode.CLOSE) {
				ImPacket imPacket = new ImPacket(Command.COMMAND_CLOSE_REQ);
				return imPacket;
			} else if (opcode == Opcode.TEXT) {
				throw new AioDecodeException("错误的websocket包，不支持TEXT类型的数据");
			} else {
				throw new AioDecodeException("错误的websocket包，错误的Opcode");
			}

		} else {
			if (ImPacket.HEARTBEAT_BYTE == firstbyte) {
				buffer.position(1 + initPosition);
				return heartbeatPacket;
			}
		}

		int readableLength = buffer.limit() - initPosition;

		int headerLength = ImPacket.LEAST_HEADER_LENGHT;
		ImPacket imPacket = null;
		firstbyte = buffer.get();
		byte version = ImPacket.decodeVersion(firstbyte);
		boolean isCompress = ImPacket.decodeCompress(firstbyte);
		boolean hasSynSeq = ImPacket.decodeHasSynSeq(firstbyte);
		boolean is4ByteLength = ImPacket.decode4ByteLength(firstbyte);
		if (hasSynSeq) {
			headerLength += 4;
		}
		if (is4ByteLength) {
			headerLength += 2;
		}
		if (readableLength < headerLength) {
			return null;
		}
		Byte code = buffer.get();
		Command command = Command.forNumber(code);
		int bodyLength = 0;
		if (is4ByteLength) {
			bodyLength = buffer.getInt();
		} else {
			bodyLength = buffer.getShort();
		}

		if (bodyLength > ImPacket.MAX_LENGTH_OF_BODY || bodyLength < 0) {
			throw new AioDecodeException("bodyLength [" + bodyLength + "] is not right, remote:" + channelContext.getClientNode());
		}

		int seq = 0;
		if (hasSynSeq) {
			seq = buffer.getInt();
		}

		//		@SuppressWarnings("unused")
		//		int reserve = buffer.getInt();//保留字段

		//		PacketMeta<ImPacket> packetMeta = new PacketMeta<>();
		int neededLength = headerLength + bodyLength;
		int test = readableLength - neededLength;
		if (test < 0) // 不够消息体长度(剩下的buffe组不了消息体)
		{
			//			packetMeta.setNeededLength(neededLength);
			return null;
		} else {
			imPacket = new ImPacket();
			imPacket.setCommand(command);

			if (seq != 0) {
				imPacket.setSynSeq(seq);
			}

			if (bodyLength > 0) {
				byte[] dst = new byte[bodyLength];
				buffer.get(dst);
				if (isCompress) {
					try {
						byte[] unGzippedBytes = GzipUtils.unGZip(dst);
						imPacket.setBody(unGzippedBytes);
						//						imPacket.setBodyLen(unGzippedBytes.length);
					} catch (IOException e) {
						throw new AioDecodeException(e);
					}
				} else {
					imPacket.setBody(dst);
					//					imPacket.setBodyLen(dst.length);
				}
			}

			//			packetMeta.setPacket(imPacket);
			return imPacket;

		}

	}

}
