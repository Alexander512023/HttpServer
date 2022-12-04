package com.goryaninaa.web.HttpServer.json.deserializer;

import java.util.List;
import java.util.Objects;

public class ReqresListUsers {
	private int page;
	private int perPage;
	private int total;
	private int totalPages;
	private List<Person> data;
	private Support support;
	
	public ReqresListUsers() {
	}
	
	public ReqresListUsers(int page, int perPage, int total, int totalPages, List<Person> data, Support support) {
		super();
		this.page = page;
		this.perPage = perPage;
		this.total = total;
		this.totalPages = totalPages;
		this.data = data;
		this.support = support;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPerPage() {
		return perPage;
	}

	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public List<Person> getData() {
		return data;
	}

	public void setData(List<Person> data) {
		this.data = data;
	}

	public Support getSupport() {
		return support;
	}

	public void setSupport(Support support) {
		this.support = support;
	}

	@Override
	public int hashCode() {
		return Objects.hash(data, page, perPage, support, total, totalPages);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReqresListUsers other = (ReqresListUsers) obj;
		return this.getData().containsAll(other.getData()) && page == other.page && perPage == other.perPage
				&& Objects.equals(support, other.support) && total == other.total && totalPages == other.totalPages;
	}
	
}
