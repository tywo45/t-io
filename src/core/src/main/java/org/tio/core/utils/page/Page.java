package org.tio.core.utils.page;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author tanyaowu 
 * 2017年5月10日 下午12:01:18
 */
public class Page<T> implements Serializable {

	private static final long serialVersionUID = 6551482606063638959L;
	private List<T> pageData = null; //当前页的数据
	private Integer pageIndex; //当前页码，从1开始，如果值小于等于0，则视为1
	private Integer pageSize; //每页记录数
	private Integer recordCount; //总条数

	/**
	 * 
	 * @param pageData
	 * @param pageIndex
	 * @param pageSize
	 * @param recordCount
	 * @author: tanyaowu
	 */
	public Page(List<T> pageData, Integer pageIndex, Integer pageSize, Integer recordCount) {
		this.pageData = pageData;
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.recordCount = recordCount;
	}

	public Page() {

	}

	/**
	 * @return the pageData
	 */
	public List<T> getPageData() {
		return pageData;
	}

	/**
	 * @param pageData the pageData to set
	 */
	public void setPageData(List<T> pageData) {
		this.pageData = pageData;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getRecordCount() {
		return recordCount;
	}

}
