package org.tio.utils.resp;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu
 * 2017年8月18日 下午3:56:02
 */
public enum RespResult {
	/**
	 * 成功的响应
	 */
	OK(1),
	/**
	 * 失败的响应
	 */
	FAIL(2),
	/**
	 * 未知的响应
	 */
	UNKNOWN(3);

	public static RespResult from(int value) {
		RespResult[] values = RespResult.values();
		for (RespResult v : values) {
			if (Objects.equals(v.value, value)) {
				return v;
			}
		}
		Logger log = LoggerFactory.getLogger(RespResult.class);
		log.error("can not find RespResult by " + value);
		return null;
	}

	int value;

	private RespResult(int value) {
		this.value = value;
	}
}
