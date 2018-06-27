package org.tio.webpack.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.kit.PropKit;

/**
 * @author tanyaowu
 * 2017年8月7日 下午1:49:05
 */
public class PropInit {
	private static Logger log = LoggerFactory.getLogger(PropInit.class);

	private static boolean inited = false;

	public static void init() {
		if (inited == false) {
			synchronized (log) {
				if (inited == false) {
					PropKit.use("app.properties");
					PropKit.append("app-env.properties");
					PropKit.append("app-host.properties");

//					//设置一下，用于生成高性能的uuid，这里耦合度略高，后面再优化一下
//					Integer workerid = PropKit.getInt("uuid.workerid");
//					Integer datacenter = PropKit.getInt("uuid.datacenter");
//
//					if (workerid != null) {
//						Uuid.setWorkid(workerid);
//					}
//					if (datacenter != null) {
//						Uuid.setDatacenterid(datacenter);
//					}
					inited = true;
				}
			}
		}
	}

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	/**
	 *
	 * @author tanyaowu
	 */
	public PropInit() {
	}
}
