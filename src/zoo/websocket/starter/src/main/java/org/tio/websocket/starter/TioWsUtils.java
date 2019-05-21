package org.tio.websocket.starter;

import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.intf.Packet;
import org.tio.utils.json.Json;
import org.tio.websocket.common.Opcode;
import org.tio.websocket.common.WsResponse;

import java.io.UnsupportedEncodingException;

/**
 * @author fanpan26
 * */
public class TioWsUtils {

    private static Opcode opcode;

    private static Opcode getOpcode(){
        if (opcode == null) {
            String binaryType = SpringContextHolder.getWebSocketServerProperties().getBinaryType();
            opcode = binaryType.equals("text") ? Opcode.TEXT : Opcode.BINARY;
        }
        return opcode;
    }

    public static final void send(ChannelContext channelContext,Object msg){
        Tio.send(channelContext,wsPacket(msg));
    }

    public static final void sendToAll(Object msg){
        if (msg == null){
            return;
        }
        Tio.sendToAll(SpringContextHolder.getSerGorupContext(),wsPacket(msg));
    }

    public static final void sendToGroup(String groupId,Object msg) {
        Tio.sendToGroup(SpringContextHolder.getSerGorupContext(), groupId, wsPacket(msg));
    }

    public static final void sendToUser(Integer userId,Object msg){
        sendToUser(userId.toString(),msg);
    }

    public static final void sendToUser(Long userId,Object msg){
        sendToUser(userId.toString(),msg);
    }

    public static final void sendToUser(String userId,Object msg) {
        if (msg == null) {
            return;
        }
        Tio.sendToUser(SpringContextHolder.getSerGorupContext(), userId, wsPacket(msg));
    }

    public static Packet wsPacket(Object msg){
        if (msg instanceof Packet){
            return (Packet)msg;
        }
        WsResponse response = new WsResponse();
        response.setWsOpcode(getOpcode());
        final String json = Json.toJson(msg);
        response.setWsBodyText(json);
        response.setBody(getBytes(json));
        return response;
    }

    private static byte[] getBytes(String value) {
        try {
            return value.getBytes("utf-8");
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return null;
    }
}
