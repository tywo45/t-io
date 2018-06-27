package org.tio.utils.ssl;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * @(#)SSLEngineSimpleDemo.java	1.1 05/04/12
 *
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * -Redistribution of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the
 *  distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind.
 * ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN
 * OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 * FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */

/**
 * A SSLEngine usage example which simplifies the presentation
 * by removing the I/O and multi-threading concerns.
 *
 * The demo creates two SSLEngines, simulating a client and server.
 * The "transport" layer consists two ByteBuffers:  think of them
 * as directly connected pipes.
 *
 * Note, this is a *very* simple example: real code will be much more
 * involved.  For example, different threading and I/O models could be
 * used, transport mechanisms could close unexpectedly, and so on.
 *
 * When this application runs, notice that several messages
 * (wrap/unwrap) pass before any application data is consumed or
 * produced.  (For more information, please see the SSL/TLS
 * specifications.)  There may several steps for a successful handshake,
 * so it's typical to see the following series of operations:
 *
 *	client		server		message
 *	======		======		=======
 *	wrap()		...		ClientHello     			客户端向服务器发送 ClientHello 消息
 *	...		unwrap()	ClientHello					服务器回复 ServerHello 消息
 *	...		wrap()		ServerHello/Certificate		服务器向客户端发送证书
 *	unwrap()	...		ServerHello/Certificate		客户端验证服务器证书的合法性
 *	wrap()		...		ClientKeyExchange			客户端随机产生一个用于后面通讯的“对称密码”
 *	wrap()		...		ChangeCipherSpec			如果服务器要求客户端的身份认证（在握手过程中为可选），客户端可以建立一个随机数然后对其进行数据签名，将这个含有签名的随机数和客户端自己的证书以及加密过的“预主密码”一起传给服务器
 *	wrap()		...		Finished					客户端向服务器发出信息,通知结束握手,单向在这里完成,双向没有这个?
 *	...		unwrap()	ClientKeyExchange			服务器检验客户端证书和签名随机数的合法性
 *	...		unwrap()	ChangeCipherSpec			服务器和客户端用相同的主密码即“通话密码”
 *	...		unwrap()	Finished					服务端向服务器发出信息,通知结束握手
 *	...		wrap()		ChangeCipherSpec
 *	...		wrap()		Finished
 *	unwrap()	...		ChangeCipherSpec
 *	unwrap()	...		Finished
 */

public class SSLEngineSimpleDemo {
	private static Logger log = LoggerFactory.getLogger(SSLEngineSimpleDemo.class);

	/*
	 * Enables logging of the SSLEngine operations.
	 */
	private static boolean logging = true;

	/*
	 * Enables the JSSE system debugging system property:
	 * 
	 * -Djavax.net.debug=all
	 * 
	 * This gives a lot of low-level information about operations underway,
	 * including specific handshake messages, and might be best examined after
	 * gaining some familiarity with this application.
	 */
	private static boolean debug = true;

	private SSLContext sslContext;

	private SSLContext clientSslContext;

	private SSLEngine clientEngine; // client Engine
	private ByteBuffer clientOut; // write side of clientEngine
	private ByteBuffer clientIn; // read side of clientEngine

	private SSLEngine serverEngine; // server Engine
	private ByteBuffer serverOut; // write side of serverEngine
	private ByteBuffer serverIn; // read side of serverEngine

	/*
	 * For data transport, this example uses local ByteBuffers. This isn't
	 * really useful, but the purpose of this example is to show SSLEngine
	 * concepts, not how to do network transport.
	 */
	private ByteBuffer clientToServer; // "reliable" transport client->server
	private ByteBuffer serverToClient; // "reliable" transport server->client

	/*
	 * The following is to set up the keystores.
	 */
	private static String keyStoreFile = "D:/svn_nb/nbyb/html/nbyb/web_server/nginx/conf/cert/214319727930849.jks";
	private static String trustStoreFile = "D:/svn_nb/nbyb/html/nbyb/web_server/nginx/conf/cert/214319727930849.jks";
	private static String passwd = "214319727930849";

	/*
	 * Main entry point for this demo.
	 */
	public static void main(String args[]) throws Exception {
		if (debug) {
			System.setProperty("javax.net.debug", "all");
		}

		SSLEngineSimpleDemo demo = new SSLEngineSimpleDemo();
		demo.runDemo();

		log.info("Demo Completed.");
	}

	/*
	 * Create an initialized SSLContext to use for this demo.
	 */
	public SSLEngineSimpleDemo() throws Exception {

		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

		char[] passChars = passwd.toCharArray();

		keyStore.load(new FileInputStream(keyStoreFile), passChars);
		trustStore.load(new FileInputStream(trustStoreFile), passChars);

		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		keyManagerFactory.init(keyStore, passChars);

		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
		trustManagerFactory.init(trustStore);

		sslContext = SSLContext.getInstance("SSL");
		sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

		
		
		
		KeyManagerFactory clientKeyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		clientKeyManagerFactory.init(null, null);

		TrustManagerFactory clientTrustManagerFactory = TrustManagerFactory.getInstance("SunX509");
		clientTrustManagerFactory.init((KeyStore)null);
		clientSslContext = SSLContext.getInstance("SSL");
		clientSslContext.init(clientKeyManagerFactory.getKeyManagers(), clientTrustManagerFactory.getTrustManagers(), null);
	}

	/*
	 * Using the SSLContext created during object creation, create/configure the
	 * SSLEngines we'll use for this demo.
	 */
	private void createSSLEngines() throws Exception {
		/*
		 * Configure the serverEngine to act as a server in the SSL/TLS
		 * handshake. Also, require SSL client authentication.
		 */
		serverEngine = sslContext.createSSLEngine();
		serverEngine.setUseClientMode(false);
		serverEngine.setNeedClientAuth(false);

		/*
		 * Similar to above, but using client mode instead.
		 */
		clientEngine = clientSslContext.createSSLEngine();
		clientEngine.setUseClientMode(true);
	}

	/*
	 * Create and size the buffers appropriately.
	 */
	private void createBuffers() {

		/*
		 * We'll assume the buffer sizes are the same between client and server.
		 */
		SSLSession session = clientEngine.getSession();
		int appBufferMax = session.getApplicationBufferSize();
		int netBufferMax = session.getPacketBufferSize();

		log("AppBufferMax:" + appBufferMax);
		log("NetBufferMax:" + netBufferMax);

		/*
		 * We'll make the input buffers a bit bigger than the max needed size,
		 * so that unwrap()s following a successful data transfer won't generate
		 * BUFFER_OVERFLOWS.
		 * 
		 * We'll use a mix of direct and indirect ByteBuffers for tutorial
		 * purposes only. In reality, only use direct ByteBuffers when they give
		 * a clear performance enhancement.
		 */
		clientIn = ByteBuffer.allocate(appBufferMax);
		serverIn = ByteBuffer.allocate(appBufferMax);

		clientToServer = ByteBuffer.allocate(netBufferMax);
		serverToClient = ByteBuffer.allocate(netBufferMax);

		clientOut = ByteBuffer.wrap("Hi Server, I'm Client".getBytes());
		serverOut = ByteBuffer.wrap("Hello Client, I'm Server".getBytes());
	}

	/*
	 * Run the demo.
	 * 
	 * Sit in a tight loop, both engines calling wrap/unwrap regardless of
	 * whether data is available or not. We do this until both engines report
	 * back they are closed.
	 * 
	 * The main loop handles all of the I/O phases of the SSLEngine's lifetime:
	 * 
	 * initial handshaking application data transfer engine closing
	 * 
	 * One could easily separate these phases into separate sections of code.
	 */
	private void runDemo() throws Exception {
		boolean dataDone = false;

		createSSLEngines();
		createBuffers();

		SSLEngineResult clientResult; // results from client's last operation
		SSLEngineResult serverResult; // results from server's last operation

		/*
		 * Examining the SSLEngineResults could be much more involved, and may
		 * alter the overall flow of the application.
		 * 
		 * For example, if we received a BUFFER_OVERFLOW when trying to write to
		 * the output pipe, we could reallocate a larger pipe, but instead we
		 * wait for the peer to drain it.
		 */
		while (!isEngineClosed(clientEngine) || !isEngineClosed(serverEngine)) {

			log("================");
			clientResult = clientEngine.wrap(clientOut, clientToServer);
			log("client wrap: ", clientResult);
			runDelegatedTasks(clientResult, clientEngine);

			serverResult = serverEngine.wrap(serverOut, serverToClient);
			log("server wrap: ", serverResult);
			runDelegatedTasks(serverResult, serverEngine);

			clientToServer.flip();
			serverToClient.flip();

			log("----");

			clientResult = clientEngine.unwrap(serverToClient, clientIn);
			log("client unwrap: ", clientResult);
			runDelegatedTasks(clientResult, clientEngine);
			if (clientResult.bytesConsumed() > 500) {
				log("收到证书");
			}

			serverResult = serverEngine.unwrap(clientToServer, serverIn);
			log("server unwrap: ", serverResult);
			runDelegatedTasks(serverResult, serverEngine);
			if (serverResult.bytesConsumed() > 500) {
				log("收到证书");
			}

			clientToServer.compact();
			serverToClient.compact();

			/*
			 * After we've transfered all application data between the client
			 * and server, we close the clientEngine's outbound stream. This
			 * generates a close_notify handshake message, which the server
			 * engine receives and responds by closing itself.
			 * 
			 * In normal operation, each SSLEngine should call closeOutbound().
			 * To protect against truncation attacks, SSLEngine.closeInbound()
			 * should be called whenever it has determined that no more input
			 * data will ever be available (say a closed input stream).
			 */
			if (!dataDone && (clientOut.limit() == serverIn.position()) && (serverOut.limit() == clientIn.position())) {

				log(new String(serverIn.array()));
				log(new String(clientOut.array()));

				log(new String(serverOut.array()));
				log(new String(clientIn.array()));

				/*
				 * A sanity check to ensure we got what was sent.
				 */
				checkTransfer(serverOut, clientIn);
				checkTransfer(clientOut, serverIn);

				log("\tClosing clientEngine's *OUTBOUND*...");
				clientEngine.closeOutbound();
				serverEngine.closeOutbound();
				dataDone = true;
			}
		}
	}

	/*
	 * If the result indicates that we have outstanding tasks to do, go ahead
	 * and run them in this thread.
	 */
	private static void runDelegatedTasks(SSLEngineResult result, SSLEngine engine) throws Exception {

		if (result.getHandshakeStatus() == HandshakeStatus.NEED_TASK) {
			Runnable runnable;
			while ((runnable = engine.getDelegatedTask()) != null) {
				log("\trunning delegated task...");
				runnable.run();
			}
			HandshakeStatus hsStatus = engine.getHandshakeStatus();
			if (hsStatus == HandshakeStatus.NEED_TASK) {
				throw new Exception("handshake shouldn't need additional tasks");
			}
			log("\tnew HandshakeStatus: " + hsStatus);
		}
	}

	private static boolean isEngineClosed(SSLEngine engine) {
		return (engine.isOutboundDone() && engine.isInboundDone());
	}

	/*
	 * Simple check to make sure everything came across as expected.
	 */
	private static void checkTransfer(ByteBuffer a, ByteBuffer b) throws Exception {
		a.flip();
		b.flip();

		if (!a.equals(b)) {
			throw new Exception("Data didn't transfer cleanly");
		} else {
			log("\tData transferred cleanly");
		}

		a.position(a.limit());
		b.position(b.limit());
		a.limit(a.capacity());
		b.limit(b.capacity());
	}

	/*
	 * Logging code
	 */
	private static boolean resultOnce = true;

	private static void log(String str, SSLEngineResult result) {
		if (!logging) {
			return;
		}
		if (resultOnce) {
			resultOnce = false;
			log.info("The format of the SSLEngineResult is: \n" + "\t\"getStatus() / getHandshakeStatus()\" +\n" + "\t\"bytesConsumed() / bytesProduced()\"\n");
		}
		HandshakeStatus hsStatus = result.getHandshakeStatus();
		log(str + result.getStatus() + "/" + hsStatus + ", " + result.bytesConsumed() + "/" + result.bytesProduced() + " bytes");
		if (hsStatus == HandshakeStatus.FINISHED) {
			log("\t...ready for application data");
		}
	}

	private static void log(String str) {
		if (logging) {
			log.info(str);
		}
	}
}