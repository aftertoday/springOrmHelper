package com.yong.base;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class Page {

	private Integer pageSize;
	private Integer total;
	private Integer totalPage;
	private Integer currentPage;
	private List list = new ArrayList();
	
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
	public Integer getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}
	
	public Integer getStartIndex() {
		return currentPage*pageSize + 1;
	}

	
}
