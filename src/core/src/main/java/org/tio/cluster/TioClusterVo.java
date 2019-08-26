package org.tio.cluster;

import java.util.UUID;

import org.tio.core.intf.Packet;

/**
 * 成员变量group, userid, ip等字段谁有值就发给谁，toAll为true则发给所有<br>
 * packet是不允许为null的
 * @author tanyaowu 
 * 2017年10月10日 下午3:10:29
 */
public class TioClusterVo implements java.io.Serializable {
	private static final long serialVersionUID = 6978027913776155664L;

	public static final String CLIENTID = UUID.randomUUID().toString();

	private Packet packet;

	private String clientId = CLIENTID;

	private String group;

	private String userid;

	private String token;

	private String ip;

	/**
	 * ChannelContext'id
	 */
	private String channelId;

	private String bsId;

	private boolean toAll = false;

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * 
	 * @author: tanyaowu
	 */
	public TioClusterVo() {
	}

	public TioClusterVo(Packet packet) {
		this.packet = packet;
	}

	public boolean isToAll() {
		return toAll;
	}

	public void setToAll(boolean toAll) {
		this.toAll = toAll;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getBsId() {
		return bsId;
	}

	public void setBsId(String bsId) {
		this.bsId = bsId;
	}
}
