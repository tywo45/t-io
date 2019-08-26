package org.tio.http.common.session.id.impl;

import org.tio.http.common.HttpConfig;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.session.id.ISessionIdGenerator;
import org.tio.utils.hutool.Snowflake;

/**
 * @author tanyaowu
 * 2017年8月15日 上午10:58:22
 */
public class SnowflakeSessionIdGenerator implements ISessionIdGenerator {

	private Snowflake snowflake;

	//	/**
	//	 *
	//	 * @author tanyaowu
	//	 */
	//	public SnowflakeSessionIdGenerator() {
	//		snowflake = new Snowflake(RandomUtil.randomInt(0, 31), RandomUtil.randomInt(0, 31));
	//	}

	/**
	 *
	 * @author tanyaowu
	 */
	public SnowflakeSessionIdGenerator(int workerId, int datacenterId) {
		snowflake = new Snowflake(workerId, datacenterId);
	}

	/**
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public String sessionId(HttpConfig httpConfig, HttpRequest request) {
		return String.valueOf(snowflake.nextId());
	}
}
