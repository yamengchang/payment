package com.pay.dao.base;

import java.io.*;
import java.util.*;

/**
 * 数据库分页
 * @param <T> 数据类型
 * @author 唐东峰
 */
public class Page<T> implements Serializable {

	private static final long serialVersionUID = -5789913418349925461L;
	/**
	 * 页码，从一开始
	 */
	private int page;
	/**
	 * 一页多少行
	 */
	private int limit;
	/**
	 * 分页查询之后的数据
	 */
	private List<T> data;
	/**
	 * 当前也是否是最后一页
	 */
	private boolean end;
	/**
	 * 总页数
	 */
	private Integer total;
	public Page() {
		super();
	}
	public Page(int page, int limit) {
		this.page = page;
		this.limit = limit;
	}
	public Page(int page, int limit, List<T> data) {
		this.page = page;
		this.limit = limit;
		this.data = data;
		this.end = null == data || data.size() < limit;
	}
	public Page(int page, int limit, List<T> data, int total) {
		this.page = page;
		this.limit = limit;
		this.data = data;
		this.end = null == data || data.size() < limit;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
		this.end = null == data || data.size() < limit;
	}

	public boolean getEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}
}