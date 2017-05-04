package org.tio.examples.showcase.server.handler;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.examples.showcase.common.ShowcasePacket;
import org.tio.examples.showcase.common.ShowcaseSessionContext;
import org.tio.examples.showcase.common.Type;
import org.tio.examples.showcase.common.intf.AbsShowcaseBsHandler;
import org.tio.examples.showcase.common.packets.JoinGroupRespBody;
import org.tio.examples.showcase.common.packets.LoginReqBody;
import org.tio.examples.showcase.common.packets.LoginRespBody;
import org.tio.json.Json;

/**
 * @author tanyaowu 
 * 2017年3月27日 下午9:51:28
 */
public class LoginReqHandler extends AbsShowcaseBsHandler<LoginReqBody>
{
	private static Logger log = LoggerFactory.getLogger(LoginReqHandler.class);

	/**
	 * 
	 * @author: tanyaowu
	 */
	public LoginReqHandler()
	{
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args)
	{

	}

	/** 
	 * @return
	 * @author: tanyaowu
	 */
	@Override
	public Class<LoginReqBody> bodyClass()
	{
		return LoginReqBody.class;
	}

	java.util.concurrent.atomic.AtomicLong tokenSeq = new AtomicLong();

	private String newToken()
	{
		return System.currentTimeMillis() + "_" + tokenSeq.incrementAndGet();
	}

	/** 
	 * @param packet
	 * @param bsBody
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 */
	@Override
	public Object handler(ShowcasePacket packet, LoginReqBody bsBody, ChannelContext<ShowcaseSessionContext, ShowcasePacket, Object> channelContext) throws Exception
	{
		log.info("收到登录请求消息:{}", Json.toJson(bsBody));
		LoginRespBody loginRespBody = new LoginRespBody();
		loginRespBody.setCode(JoinGroupRespBody.Code.SUCCESS);
		loginRespBody.setToken(newToken());

		String userid = bsBody.getLoginname();
		Aio.bindUser(channelContext, userid);
		
		ShowcaseSessionContext showcaseSessionContext = channelContext.getSessionContext();
		showcaseSessionContext.setUserid(userid);

		ShowcasePacket respPacket = new ShowcasePacket();
		respPacket.setType(Type.LOGIN_RESP);
		respPacket.setBody(Json.toJson(loginRespBody).getBytes(ShowcasePacket.CHARSET));
		Aio.send(channelContext, respPacket);

		return null;
	}
}
