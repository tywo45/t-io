package org.tio.examples.showcase.server;

import java.io.IOException;

import org.tio.examples.showcase.common.ShowcasePacket;
import org.tio.examples.showcase.common.ShowcaseSessionContext;
import org.tio.server.AioServer;
import org.tio.server.ServerGroupContext;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;

/**
 * 
 * @author tanyaowu 
 * 2017年3月27日 上午12:16:31
 */
public class ShowcaseServerStarter
{
	static ServerAioHandler<ShowcaseSessionContext, ShowcasePacket, Object> aioHandler = new ShowcaseServerAioHandler();
	static ServerAioListener<ShowcaseSessionContext, ShowcasePacket, Object> aioListener = new ShowcaseServerAioListener();
	static ServerGroupContext<ShowcaseSessionContext, ShowcasePacket, Object> serverGroupContext = new ServerGroupContext<>(aioHandler, aioListener);
	static AioServer<ShowcaseSessionContext, ShowcasePacket, Object> aioServer = new AioServer<>(serverGroupContext); //可以为空
	
	static String serverIp = null;
	static int serverPort = org.tio.examples.showcase.common.Const.PORT;

	public static void main(String[] args) throws IOException
	{
		aioServer.start(serverIp, serverPort);
	}
}