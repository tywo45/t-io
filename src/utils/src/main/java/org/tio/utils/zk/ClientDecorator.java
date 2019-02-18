package org.tio.utils.zk;

import org.apache.curator.framework.CuratorFramework;

/**
 * @author tanyaowu 
 * 2017年11月20日 上午9:52:01
 */
public interface ClientDecorator {

	/**
	 * 
	 * @param zclient
	 * @author tanyaowu
	 * @throws Exception 
	 */
	public void decorate(CuratorFramework zclient) throws Exception;

}
