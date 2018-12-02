package org.tio.utils.ui.layui.table;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu
 * 2017年8月22日 下午2:17:55
 */
public class LayuiPage {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(LayuiPage.class);

	public static LayuiPage fail() {
		return new LayuiPage(2);
	}

	public static LayuiPage ok(Collection<?> data, long count) {
		return new LayuiPage(data, count);
	}

	private int code = 0;

	private String msg = null;

	private long count = 0; //数据总量

	private Collection<?> data = null;

	public LayuiPage() {
	}

	/**
	 *
	 * @author: tanyaowu
	 */
	public LayuiPage(Collection<?> data, long count) {
		this.data = data;
		this.count = count;
	}

	public LayuiPage(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public long getCount() {
		return count;
	}

	public Collection<?> getData() {
		return data;
	}

	public String getMsg() {
		return msg;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public void setData(Collection<Object> data) {
		this.data = data;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
