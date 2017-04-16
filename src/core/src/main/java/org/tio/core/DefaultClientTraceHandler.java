package org.tio.core;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.tio.core.intf.ClientTraceHandler;
import org.tio.core.intf.Packet;
import org.tio.json.Json;

import com.xiaoleilu.hutool.date.DatePattern;
import com.xiaoleilu.hutool.date.DateUtil;

/**
 * @author tanyaowu 
 * 2017年4月16日 下午6:45:21
 */
public class DefaultClientTraceHandler<SessionContext, P extends Packet, R> implements ClientTraceHandler<SessionContext, P, R>
{
	private static Logger log = LoggerFactory.getLogger(DefaultClientTraceHandler.class);

	private Logger clientTraceLog = LoggerFactory.getLogger("tio-client-trace-log");

	/**
	 * 
	 * @author: tanyaowu
	 */
	public DefaultClientTraceHandler()
	{
	}

	/** 
	 * @param channelContext
	 * @param clientAction
	 * @param packet
	 * @param msg
	 * @author: tanyaowu
	 */
	@Override
	public void traceClient(ChannelContext<SessionContext, P, R> channelContext, ClientAction clientAction, Packet packet, Map<String, Object> msg)
	{
		Map<String, Object> map = new HashMap<>();
		map.put("time", DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN));
		map.put("action", clientAction);
		map.put("c_id", channelContext.getId());

		MDC.put("tio_client", channelContext.getClientNodeTraceFilename());

		if (packet != null)
		{
			map.put("p_id", packet.getId());
			map.put("p_respId", packet.getRespId());

			map.put("packet", packet.logstr());
		}

		if (msg != null)
		{
			map.putAll(msg);
		}
		clientTraceLog.info(Json.toJson(map));
	}
}
