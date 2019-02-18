package org.tio.utils;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu 
 * 2017年9月15日 下午4:09:59
 */
public class Uuid {
	private static Logger log = LoggerFactory.getLogger(Uuid.class);

	/**
	 * 
	 * @author: tanyaowu
	 */
	public Uuid() {
	}

	/**
	 * 系统启动时，重设此两值，只
	 */
	private static Integer	workid			= ThreadLocalRandom.current().nextInt(0, 31);
	private static boolean	workidSetted	= false;

	/**
	 * 
	 */
	private static Integer	datacenterid		= ThreadLocalRandom.current().nextInt(0, 31);
	private static boolean	datacenteridSetted	= false;

	public static int getWorkid() {
		return workid;
	}

	public static void setWorkid(Integer workid) {
		synchronized (log) {
			if (workidSetted) {
				if (!Objects.equals(workid, Uuid.workid)) {
					log.error("workid只允许设置一次");
				}
				return;
			}
			if (workid == null) {
				log.error("workid不允许为null");
				return;
			}
			Uuid.workid = workid;
			workidSetted = true;
		}

	}

	public static int getDatacenterid() {
		return datacenterid;
	}

	public static void setDatacenterid(Integer datacenterid) {
		synchronized (log) {
			if (datacenteridSetted) {
				if (!Objects.equals(datacenterid, Uuid.datacenterid)) {
					log.error("datacenterid只允许设置一次");
				}
				return;
			}
			if (datacenterid == null) {
				log.error("datacenterid不允许为null");
				return;
			}

			Uuid.datacenterid = datacenterid;
			datacenteridSetted = true;
		}
	}

}
