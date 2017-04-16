/**
 * 
 */
package org.tio.examples.im.client.handler;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.examples.im.client.ui.JFrameMain;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.packets.JoinGroupResult;
import org.tio.examples.im.common.packets.JoinRespBody;

/**
 * 
 * @filename:	 com.talent.im.client.handler.bs.ChatRespHandler
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
public class JoinRespHandler implements ImAioHandlerIntf
{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(JoinRespHandler.class);

	/**
	 * 
	 */
	public JoinRespHandler()
	{

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}

//	@Override
//	public Map<String, Object> onReceived(ImReqPacket packet, ChannelContext<ImClientChannelContextExt> channelContext) throws Exception
//	{
//
//		ImClientChannelContextExt ext = channelContext.getSessionContext();
//		String bodyStr = null;
//		if (packet.getBody() != null)
//		{
//			bodyStr = new String(packet.getBody(), Const.CHARSET_UTF8);
//		}
//		JoinRespBody body = Json.toBean(bodyStr, JoinRespBody.class);
//		if (Objects.equals(JoinGroupResultVo.Code.OK, body.getResult().getCode()))
//		{
//			String group = body.getGroup();
//			//			log.info("join group {}", group);
//			String xx = channelContext.getId() + "(" + ext.getLoginname() + ")" + "进入组:" + group;
//			JFrameMain.getInstance().getMsgTextArea().append(xx + System.lineSeparator());
//			//顺利进入组
//		} else
//		{
//			//被拒绝
//			//			log.error("refused to join in group {}", body.getGroup());
//			String xx = channelContext.getId() + "(" + ext.getLoginname() + ")" + "被拒绝进入组" + body.getGroup();
//			JFrameMain.getInstance().getMsgTextArea().append(xx + System.lineSeparator());
//		}
//
//		return null;
//	}

	/** 
	 * @see org.tio.examples.im.client.handler.ImAioHandlerIntf#handler(org.tio.examples.im.common.ImPacket, org.tio.core.ChannelContext)
	 * 
	 * @param packet
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 * 2016年12月6日 下午2:25:44
	 * 
	 */
	@Override
	public Object handler(ImPacket packet, org.tio.core.ChannelContext<ImSessionContext, ImPacket, Object> channelContext) throws Exception
	{
		if (packet.getBody() == null)
		{
			throw new Exception("body is null");
		}
		
		JoinRespBody respBody = JoinRespBody.parseFrom(packet.getBody());
		
		
		if (Objects.equals(JoinGroupResult.JOIN_GROUP_RESULT_OK, respBody.getResult()))
		{
			
			String group = respBody.getGroup();
//			channelContext.getGroupContext().getGroups().bind(group, channelContext);
			org.tio.core.Aio.bindGroup(channelContext, group);
			//			log.info("join group {}", group);
//			String xx = ClientNodes.getKey(channelContext) + "进入组:" + group;
//			JFrameMain.getInstance().getMsgTextArea().append(xx + System.lineSeparator());
			//顺利进入组
		} else
		{
			//被拒绝
			//			log.error("refused to join in group {}", body.getGroup());
			String xx = channelContext + "被拒绝进入组" + respBody.getGroup();
			JFrameMain.getInstance().getMsgTextArea().append(xx + System.lineSeparator());
		}
		
		
		
		
		return null;
	}
}
