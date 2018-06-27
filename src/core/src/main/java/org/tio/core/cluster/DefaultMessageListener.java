package org.tio.core.cluster;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Tio;
import org.tio.core.GroupContext;
import org.tio.core.intf.Packet;
import org.tio.utils.json.Json;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 默认的集群消息监听类
 * 作者: 陈磊(Cooppor)
 * 日期: 2018-05-28 15:08
 */
public class DefaultMessageListener implements MessageListener<TioClusterVo> {

    private static Logger log = LoggerFactory.getLogger(DefaultMessageListener.class);

    /**
     * 收到了多少次topic
     */
    private static final AtomicLong RECEIVED_TOPIC_COUNT = new AtomicLong();

    private GroupContext groupContext;

    public DefaultMessageListener(GroupContext groupContext) {
        this.groupContext = groupContext;
    }

    @Override
    public void onMessage(String channel, TioClusterVo tioClusterVo) {
		log.info("收到topic:{}, count:{}, tioClusterVo:{}", channel, RECEIVED_TOPIC_COUNT.incrementAndGet(), Json.toJson(tioClusterVo));
		String clientid = tioClusterVo.getClientId();
		if (StringUtils.isBlank(clientid)) {
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
			Tio.sendToAll(groupContext, packet);
		}

		//发送给指定组
		String group = tioClusterVo.getGroup();
		if (StringUtils.isNotBlank(group)) {
			Tio.sendToGroup(groupContext, group, packet);
		}

		//发送给指定用户
		String userid = tioClusterVo.getUserid();
		if (StringUtils.isNotBlank(userid)) {
			Tio.sendToUser(groupContext, userid, packet);
		}
		
		//发送给指定token
		String token = tioClusterVo.getToken();
		if (StringUtils.isNotBlank(token)) {
			Tio.sendToToken(groupContext, token, packet);
		}

		//发送给指定ip
		String ip = tioClusterVo.getIp();
		if (StringUtils.isNotBlank(ip)) {
			Tio.sendToIp(groupContext, ip, packet);
		}
		
		//发送给指定channelId
		String channelId = tioClusterVo.getChannelId();
		if (StringUtils.isNotBlank(channelId)) {
			Tio.sendToId(groupContext, channelId, packet);
		}
	}
}
