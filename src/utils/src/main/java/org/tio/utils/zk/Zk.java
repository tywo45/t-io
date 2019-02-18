package org.tio.utils.zk;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.hutool.StrUtil;

/**
 * @author tanyaowu 
 * 2017年9月19日 下午3:06:34
 */
public class Zk {
	private static Logger log = LoggerFactory.getLogger(Zk.class);

	/**
	 * 
	 * @author: tanyaowu
	 */
	public Zk() {
	}

	private static final String CHARSET = "utf-8";

	public static CuratorFramework zkclient = null;
	//		static String nameSpace = "php";
	//	static {
	//		String zkhost = "192.168.1.41:2181";//AppConfig.getInstance().getString("zk.address", null);//"192.168.1.41:2181";//ZK host
	//		zkhost = AppConfig.getInstance().getString("zk.address", null);
	//
	//		if (StrUtil.isBlank(zkhost)) {
	//			log.error("请配置好zookeeper地址:{}", "zk.address");
	//
	//		}
	//
	//		RetryPolicy rp = new ExponentialBackoffRetry(500, Integer.MAX_VALUE);//Retry mechanism
	//		Builder builder = CuratorFrameworkFactory.builder().connectString(zkhost).connectionTimeoutMs(5000).sessionTimeoutMs(5000).retryPolicy(rp);
	//		//				builder.namespace(nameSpace);
	//		CuratorFramework zclient = builder.build();
	//		zkclient = zclient;
	//		zkclient.start();// Implemented in the front
	//		//				zkclient.newNamespaceAwareEnsurePath(nameSpace);
	//
	//	}

	/**
	 * 
	 * @param address
	 * @param clientDecorator
	 * @author tanyaowu
	 * @throws Exception
	 */
	public static void init(String address, ClientDecorator clientDecorator) throws Exception {
		//		String zkhost = "192.168.1.41:2181";//AppConfig.getInstance().getString("zk.address", null);//"192.168.1.41:2181";//ZK host
		//		zkhost = AppConfig.getInstance().getString("zk.address", null);

		if (StrUtil.isBlank(address)) {
			log.error("zk address is null");
			throw new RuntimeException("zk address is null");
		}

		//		RetryPolicy rp = new ExponentialBackoffRetry(500, Integer.MAX_VALUE);//Retry mechanism
		RetryPolicy rp = new RetryForever(500);
		Builder builder = CuratorFrameworkFactory.builder().connectString(address).connectionTimeoutMs(15 * 1000).sessionTimeoutMs(60 * 1000).retryPolicy(rp);
		//				builder.namespace(nameSpace);
		zkclient = builder.build();

		if (clientDecorator != null) {
			clientDecorator.decorate(zkclient);
		}

		//		zkclient.start();
	}

	/**
	 * Start the client. Most mutator methods will not work until the client is started
	 * @author tanyaowu
	 */
	public static void start() {
		Zk.zkclient.start();
	}

	/**
	 * 
	 * @param path
	 * @param content
	 * @param createMode
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 * @创建时间:　2013年8月3日 上午10:39:00
	 */
	public static void createOrUpdate(String path, String content, CreateMode createMode) throws Exception {
		if (content != null) {
			createOrUpdate(path, content.getBytes(CHARSET), createMode);
			return;
		}
		createOrUpdate(path, (byte[]) null, createMode);
	}

	/**
	 * 
	 * @param path
	 * @param content
	 * @param createMode
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 * @创建时间:　2013年8月3日 上午11:06:54
	 */
	public static void createOrUpdate(String path, byte[] content, CreateMode createMode) throws Exception {
		if (!createMode.isSequential()) {
			if (exists(path)) {
				log.info("节点已经存在:{}", path);
				if (content != null) {
					setData(path, content);
				}
				return;
			}
		}

		try {
			zkclient.create().creatingParentsIfNeeded().withMode(createMode).forPath(path, content);
		} catch (NodeExistsException e) {
			//			node exists skip it
			//			log.error(e.toString(), e);
		}

		return;
	}

	public static void createContainers(String path) throws Exception {
		zkclient.createContainers(path);
	}

	/**
	 * 
	 * @param path
	 * @throws Exception
	 * @author: tanyaowu
	 * @创建时间:　2013年8月3日 上午10:39:31
	 */
	public static void delete(String path) throws Exception {
		zkclient.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
		log.info("{} deleted", path);
	}

	/**
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 * @创建时间:　2013年8月1日 下午5:51:16
	 */
	public static boolean exists(String path) throws Exception {
		Stat stat = zkclient.checkExists().forPath(path);
		if (stat == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 * @创建时间:　2013年8月3日 上午11:13:04
	 */
	public static byte[] getBytes(String path) throws Exception {
		return zkclient.getData().forPath(path);
	}

	/**
	 * 
	 * @param path
	 * @param charset
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 * @创建时间:　2013年8月9日 上午11:47:48
	 */
	public static String getString(String path, String charset) throws Exception {
		byte[] bs = getBytes(path);
		if (bs != null && bs.length > 0) {
			return new String(bs, charset);
		}
		return null;
	}

	public static String getString(String path) throws Exception {
		return getString(path, "utf-8");
	}

	/**
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 * @创建时间:　2013年8月3日 上午11:13:00
	 */
	public static List<String> getChildren(String path) throws Exception {
		List<String> paths = zkclient.getChildren().forPath(path);
		return paths;
	}

	/**
	 * 
	 * @param path
	 * @param localpath
	 * @param createMode
	 * @throws Exception
	 * @author: tanyaowu
	 * @创建时间:　2013年8月3日 上午11:05:06
	 */
	public static void upload(String path, String localpath, CreateMode createMode) throws Exception {
		byte[] bs = Files.readAllBytes(new File(localpath).toPath());
		setData(path, bs);
	}

	/**
	 * 
	 * @param path
	 * @param bs
	 * @throws Exception
	 * @author: tanyaowu
	 * @创建时间:　2013年8月3日 上午11:08:40
	 */
	public static void setData(String path, byte[] bs) throws Exception {
		if (bs != null) {
			if (Zk.exists(path)) {
				zkclient.setData().forPath(path, bs);
			} else {
				try {
					zkclient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, bs);
				} catch (NodeExistsException e) {
					//节点已经存在, skip it
					//log.error(e.toString(), e);
				}
			}
		}
	}

	/**
	 * 
	 * @param path
	 * @param content
	 * @throws Exception
	 * @author: tanyaowu
	 * @创建时间:　2013年8月3日 上午11:10:38
	 */
	public static void setData(String path, String content) throws Exception {
		if (false == StrUtil.isBlank(content)) {
			setData(path, content.getBytes(CHARSET));
		}
	}

	//	/**
	//	 * 暂未实现
	//	 * @param path
	//	 * @param content
	//	 * @throws Exception
	//	 *
	//	 * @author: tanyaowu
	//	 * @创建时间:　2016年12月13日 下午4:00:26
	//	 *
	//	 */
	//	@Deprecated
	//	public static void addListener(String path, String content) throws Exception {
	//
	//		zkclient.get.getCuratorListenable().addListener(listener);
	//		//		zkclient.getCuratorListenable().addListener(listener);;
	//
	//	}

	/**
	 * 
	 * @param path
	 * @param pathChildrenCacheListener
	 * @throws Exception
	 * @author: tanyaowu
	 * @创建时间:　2013年8月3日 下午1:46:19
	 */
	@SuppressWarnings("resource")
	public static void addPathChildrenCacheListener(String path, PathChildrenCacheListener pathChildrenCacheListener) throws Exception {
		PathChildrenCache cache = new PathChildrenCache(zkclient, path, true);
		cache.start();

		//		System.out.println("监听开始/zk........");
		//		PathChildrenCacheListener plis = new PathChildrenCacheListener()
		//		{
		//
		//			@Override
		//			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception
		//			{
		//				switch (event.getType())
		//				{
		//				case CHILD_ADDED:
		//				{
		//					System.out.println("Node added: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
		//					break;
		//				}
		//
		//				case CHILD_UPDATED:
		//				{
		//					System.out.println("Node changed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
		//					break;
		//				}
		//
		//				case CHILD_REMOVED:
		//				{
		//					System.out.println("Node removed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
		//					break;
		//				}
		//				}
		//
		//			}
		//		};
		//		//注册监听 
		cache.getListenable().addListener(pathChildrenCacheListener);

	}

}
