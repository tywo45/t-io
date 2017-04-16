package org.tio.examples.showcase.common;

/**
 * 消息类型定义
 * @author tanyaowu 
 * 2017年3月26日 下午8:18:13
 */
public interface Type
{

	/**
	 * 登录消息请求
	 */
	byte LOGIN_REQ = 1;
	/**
	 * 登录消息响应
	 */
	byte LOGIN_RESP = 2;
	
	/**
	 * 进入群组消息请求
	 */
	byte JOIN_GROUP_REQ = 3;
	/**
	 * 进入群组消息响应
	 */
	byte JOIN_GROUP_RESP = 4;
	
	/**
	 * 点对点消息请求
	 */
	byte P2P_REQ = 5;
	/**
	 * 点对点消息响应
	 */
	byte P2P_RESP = 6;
	
	/**
	 * 群聊消息请求
	 */
	byte GROUP_MSG_REQ = 7;
	/**
	 * 群聊消息响应
	 */
	byte GROUP_MSG_RESP = 8;
	
	/**
	 * 心跳
	 */
	byte HEART_BEAT_REQ = 99;

}
