/**
 * 
 */
package org.tio.examples.im.client.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.utils.SystemTimer;
import org.tio.examples.im.client.ui.JFrameMain;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.packets.Command;
import org.tio.examples.im.common.packets.JoinReqBody;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * 
 * @filename:	 com.talent.im.client.handler.bs.AuthorizationRespHandler
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2014年7月1日 下午1:31:50
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2014年7月1日</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */

public class AuthRespHandler implements ImAioHandlerIntf {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(AuthRespHandler.class);

	/**
	 * 
	 */
	public AuthRespHandler() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Config conf = ConfigFactory.load("app.conf");
		int bar1 = conf.getInt("client.count");
		//		Config foo = conf.getConfig("foo");
		//		int bar2 = foo.getInt("bar");

		System.out.println(bar1);
	}

	/** 
	 * @see org.tio.examples.im.client.handler.ImAioHandlerIntf#handler(org.tio.examples.im.common.ImPacket, org.tio.core.ChannelContext)
	 * 
	 * @param packet
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 * 2016年12月6日 下午2:23:24
	 * 
	 */
	@Override
	public Object handler(ImPacket packet, ChannelContext<ImSessionContext, ImPacket, Object> channelContext) throws Exception {
		String group = JFrameMain.getInstance().getGroupField().getText();
		JoinReqBody reqBody = JoinReqBody.newBuilder().setGroup(group).setTime(SystemTimer.currentTimeMillis()).build();
		byte[] body = reqBody.toByteArray();

		ImPacket respPacket = new ImPacket();
		respPacket.setCommand(Command.COMMAND_JOIN_GROUP_REQ);
		respPacket.setBody(body);
		Aio.send(channelContext, respPacket);
		return null;
	}
}
