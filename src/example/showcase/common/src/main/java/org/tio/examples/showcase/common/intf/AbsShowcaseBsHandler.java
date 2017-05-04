package org.tio.examples.showcase.common.intf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.examples.showcase.common.Const;
import org.tio.examples.showcase.common.ShowcasePacket;
import org.tio.examples.showcase.common.ShowcaseSessionContext;
import org.tio.examples.showcase.common.packets.BaseBody;
import org.tio.json.Json;

/**
 * @author tanyaowu 
 * 2017年3月27日 下午9:56:16
 */
public abstract class AbsShowcaseBsHandler<T extends BaseBody> implements ShowcaseBsHandlerIntf
{
	private static Logger log = LoggerFactory.getLogger(AbsShowcaseBsHandler.class);

	/**
	 * 
	 * @author: tanyaowu
	 */
	public AbsShowcaseBsHandler()
	{
	}

	public abstract Class<T> bodyClass();

	public abstract Object handler(ShowcasePacket packet, T bsBody, ChannelContext<ShowcaseSessionContext, ShowcasePacket, Object> channelContext) throws Exception;

	@Override
	public Object handler(ShowcasePacket packet, ChannelContext<ShowcaseSessionContext, ShowcasePacket, Object> channelContext) throws Exception
	{
		String jsonStr = null;
		T bsBody = null;
		if (packet.getBody() != null)
		{
			jsonStr = new String(packet.getBody(), Const.CHARSET);
			bsBody = Json.toBean(jsonStr, bodyClass());
		}

		return handler(packet, bsBody, channelContext);
	}

}
