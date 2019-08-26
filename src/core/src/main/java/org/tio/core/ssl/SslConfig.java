/**
 * 
 */
package org.tio.core.ssl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.tio.utils.hutool.ResourceUtil;
import org.tio.utils.hutool.StrUtil;

/**
 * @author tanyaowu
 *
 */
public class SslConfig {

	private InputStream	keyStoreInputStream		= null;
	private InputStream	trustStoreInputStream	= null;
	private String		passwd					= null;

	private KeyManagerFactory keyManagerFactory;

	private TrustManagerFactory trustManagerFactory;

	/**
	 * 
	 * @param keyStoreInputStream
	 * @param trustStoreInputStream
	 * @param passwd
	 * @throws Exception
	 */
	private SslConfig(InputStream keyStoreInputStream, InputStream trustStoreInputStream, String passwd) throws Exception {
		this.keyStoreInputStream = keyStoreInputStream;
		this.trustStoreInputStream = trustStoreInputStream;
		this.passwd = passwd;
		this.init();
	}

	/**
	 * 
	 * @param keyStoreFile 如果是以"classpath:"开头，则从classpath中查找，否则视为普通的文件路径
	 * @param trustStoreFile 如果是以"classpath:"开头，则从classpath中查找，否则视为普通的文件路径
	 * @param passwd 
	 * @throws FileNotFoundException
	 */
	public static SslConfig forServer(String keyStoreFile, String trustStoreFile, String passwd) throws Exception {
		InputStream keyStoreInputStream = null;
		InputStream trustStoreInputStream = null;
		if (StrUtil.startWithIgnoreCase(keyStoreFile, "classpath:")) {
			keyStoreInputStream = ResourceUtil.getResourceAsStream(keyStoreFile);
		} else {
			keyStoreInputStream = new FileInputStream(keyStoreFile);
		}

		if (StrUtil.startWithIgnoreCase(trustStoreFile, "classpath:")) {
			trustStoreInputStream = ResourceUtil.getResourceAsStream(trustStoreFile);
		} else {
			trustStoreInputStream = new FileInputStream(trustStoreFile);
		}
		return forServer(keyStoreInputStream, trustStoreInputStream, passwd);
	}

	/**
	 * 给服务器用的
	 * @param keyStoreInputStream
	 * @param trustStoreInputStream
	 * @param passwd
	 * @return
	 * @throws Exception
	 */
	public static SslConfig forServer(InputStream keyStoreInputStream, InputStream trustStoreInputStream, String passwd) throws Exception {
		SslConfig sslConfig = new SslConfig(keyStoreInputStream, trustStoreInputStream, passwd);
		return sslConfig;
	}

	/**
	 * 给客户端用的
	 * @return
	 * @throws Exception
	 */
	public static SslConfig forClient() throws Exception {
		SslConfig sslConfig = new SslConfig(null, null, null);
		return sslConfig;
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
		KeyStore keyStore = null;//KeyStore.getInstance("JKS");//KeyStore.getInstance(KeyStore.getDefaultType());
		KeyStore trustStore = null;//KeyStore.getInstance("JKS");//KeyStore.getInstance(KeyStore.getDefaultType());

		char[] passChars = null;

		if (passwd != null) {
			passChars = passwd.toCharArray();
		}

		if (keyStoreInputStream != null) {
			keyStore = KeyStore.getInstance("JKS");
			keyStore.load(keyStoreInputStream, passChars);
		}

		if (trustStoreInputStream != null) {
			trustStore = KeyStore.getInstance("JKS");
			trustStore.load(trustStoreInputStream, passChars);
		}

		keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		keyManagerFactory.init(keyStore, passChars);

		trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
		trustManagerFactory.init(trustStore);

		//		System.setProperty("javax.net.debug", "all");
	}

	public KeyManagerFactory getKeyManagerFactory() {
		return keyManagerFactory;
	}

	public void setKeyManagerFactory(KeyManagerFactory keyManagerFactory) {
		this.keyManagerFactory = keyManagerFactory;
	}

	public TrustManagerFactory getTrustManagerFactory() {
		return trustManagerFactory;
	}

	public void setTrustManagerFactory(TrustManagerFactory trustManagerFactory) {
		this.trustManagerFactory = trustManagerFactory;
	}

}
