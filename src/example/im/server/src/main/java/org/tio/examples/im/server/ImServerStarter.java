package org.tio.examples.im.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.server.AioServer;
import org.tio.server.ServerGroupContext;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;

/**
 * 
 * @author tanyaowu 
 *
 */
public class ImServerStarter {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ImServerStarter.class);

	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2016年11月17日 下午5:59:24
	 * 
	 */
	public ImServerStarter() {

	}

	static ServerGroupContext<ImSessionContext, ImPacket, Object> serverGroupContext = null;

	static AioServer<ImSessionContext, ImPacket, Object> aioServer = null;

	static ServerAioHandler<ImSessionContext, ImPacket, Object> aioHandler = null;

	static ServerAioListener<ImSessionContext, ImPacket, Object> aioListener = null;

	static String ip = null;//"127.0.0.1";

	static int port = 9321;

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * @throws IOException 
	 * 2016年11月17日 下午5:59:24
	 * 
	 */
	public static void main(String[] args) throws IOException {
		aioHandler = new ImServerAioHandler();
		aioListener = new ImServerAioListener();
		serverGroupContext = new ServerGroupContext<>(aioHandler, aioListener);
		serverGroupContext.setEncodeCareWithChannelContext(true);
		//		serverGroupContext.setReadBufferSize(2048);
		aioServer = new AioServer<>(serverGroupContext);
		aioServer.start(ip, port);
	}

}
