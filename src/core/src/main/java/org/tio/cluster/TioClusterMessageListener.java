package org.tio.cluster;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.TioConfig;
import org.tio.core.Tio;
import org.tio.core.intf.Packet;
import org.tio.utils.hutool.StrUtil;
import org.tio.utils.json.Json;

/**
 * 
 * @author tanyaowu
 */
public class TioClusterMessageListener {

	private static Logger log = LoggerFactory.getLogger(TioClusterMessageListener.class);

	/**
	 * 收到了多少次topic
	 */
	private static final AtomicLong RECEIVED_TOPIC_COUNT = new AtomicLong();

	private TioConfig tioConfig;

	public TioClusterMessageListener(TioConfig tioConfig) {
		this.tioConfig = tioConfig;
	}

	public void onMessage(CharSequence channel, TioClusterVo tioClusterVo) {
		log.info("收到topic:{}, count:{}, tioClusterVo:{}", channel, RECEIVED_TOPIC_COUNT.incrementAndGet(), Json.toJson(tioClusterVo));
		String clientid = tioClusterVo.getClientId();
		if (StrUtil.isBlank(clientid)) {
			log.error("clientid is null");
			return;
		}
		if (Objects.equals(TioClusterVo.CLIENTID, clientid)) {
			log.info("自己发布的消息，忽略掉,{}", clientid);
			return;
		}

		Packet packet = tioClusterVo.getPacket();
		if (packet == null) {
			log.error("packet is null");
			return;
		}
		packet.setFromCluster(true);

		//发送给所有
		boolean isToAll = tioClusterVo.isToAll();
		if (isToAll) {
			Tio.sendToAll(tioConfig, packet);
		}

		//发送给指定组
		String group = tioClusterVo.getGroup();
		if (StrUtil.isNotBlank(group)) {
			Tio.sendToGroup(tioConfig, group, packet);
		}

		//发送给指定用户
		String userid = tioClusterVo.getUserid();
		if (StrUtil.isNotBlank(userid)) {
			Tio.sendToUser(tioConfig, userid, packet);
		}

		//发送给指定token
		String token = tioClusterVo.getToken();
		if (StrUtil.isNotBlank(token)) {
			Tio.sendToToken(tioConfig, token, packet);
		}

		//发送给指定ip
		String ip = tioClusterVo.getIp();
		if (StrUtil.isNotBlank(ip)) {
			Tio.sendToIp(tioConfig, ip, packet);
		}

		//发送给指定channelId
		String channelId = tioClusterVo.getChannelId();
		if (StrUtil.isNotBlank(channelId)) {
			Tio.sendToId(tioConfig, channelId, packet);
		}

		//发送给指定bsId
		String bsId = tioClusterVo.getBsId();
		if (StrUtil.isNotBlank(bsId)) {
			Tio.sendToBsId(tioConfig, bsId, packet);
		}
	}
}
