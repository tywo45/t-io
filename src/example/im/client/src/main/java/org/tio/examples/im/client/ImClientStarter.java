package org.tio.examples.im.client;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.AioClient;
import org.tio.client.ClientGroupContext;
import org.tio.client.ReconnConf;
import org.tio.client.intf.ClientAioHandler;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.intf.AioListener;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;

/**
 * 
 * @author tanyaowu 
 *
 */
public class ImClientStarter
{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ImClientStarter.class);

	/**
	 * 
	 *
	 * @author: tanyaowu
	 * @throws IOException 
	 * 2016年11月17日 下午5:59:24
	 * 
	 */
	public ImClientStarter() throws IOException
	{
		aioClientHandler = new ImClientAioHandler();
		aioListener = new ImClientAioListener();
		clientGroupContext = new ClientGroupContext<>(aioClientHandler, aioListener, reconnConf);
//		clientGroupContext.setReadBufferSize(2048);
//		clientGroupContext.setByteOrder(ByteOrder.BIG_ENDIAN);
		clientGroupContext.setEncodeCareWithChannelContext(true);
		aioClient = new AioClient<>(clientGroupContext);
	}


	private AioClient<ImSessionContext, ImPacket, Object> aioClient;

	private ClientGroupContext<ImSessionContext, ImPacket, Object> clientGroupContext = null;

	private ClientAioHandler<ImSessionContext, ImPacket, Object> aioClientHandler = null;

	private ClientAioListener<ImSessionContext, ImPacket, Object> aioListener = null;
	
	private static ReconnConf<ImSessionContext, ImPacket, Object> reconnConf = new ReconnConf<ImSessionContext, ImPacket, Object>(5000L);

	//--------------

	public static String SERVER_IP = "118.178.88.70"; //服务器的IP地址

	public static int SERVER_PORT = 9321; //服务器的PORT

	public static AtomicLong SEQ = new AtomicLong();

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * @throws IOException 
	 * 2016年11月17日 下午5:59:24
	 * 
	 */
	public static void main(String[] args) throws Exception
	{
		org.tio.examples.im.client.ui.JFrameMain.main(args);
	}

	/**
	 * @return the aioClient
	 */
	public AioClient<ImSessionContext, ImPacket, Object> getAioClient()
	{
		return aioClient;
	}

	/**
	 * @param aioClient the aioClient to set
	 */
	public void setAioClient(AioClient<ImSessionContext, ImPacket, Object> aioClient)
	{
		this.aioClient = aioClient;
	}

	/**
	 * @return the clientGroupContext
	 */
	public ClientGroupContext<ImSessionContext, ImPacket, Object> getClientGroupContext()
	{
		return clientGroupContext;
	}

	/**
	 * @param clientGroupContext the clientGroupContext to set
	 */
	public void setClientGroupContext(ClientGroupContext<ImSessionContext, ImPacket, Object> clientGroupContext)
	{
		this.clientGroupContext = clientGroupContext;
	}

	/**
	 * @return the aioClientHandler
	 */
	public ClientAioHandler<ImSessionContext, ImPacket, Object> getAioClientHandler()
	{
		return aioClientHandler;
	}

	/**
	 * @param aioClientHandler the aioClientHandler to set
	 */
	public void setAioClientHandler(ClientAioHandler<ImSessionContext, ImPacket, Object> aioClientHandler)
	{
		this.aioClientHandler = aioClientHandler;
	}

	/**
	 * @return the aioListener
	 */
	public AioListener<ImSessionContext, ImPacket, Object> getAioListener()
	{
		return aioListener;
	}

	/**
	 * @param aioListener the aioListener to set
	 */
	public void setAioListener(ClientAioListener<ImSessionContext, ImPacket, Object> aioListener)
	{
		this.aioListener = aioListener;
	}

}
