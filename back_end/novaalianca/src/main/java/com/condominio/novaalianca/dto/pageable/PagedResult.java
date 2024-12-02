package com.condominio.novaalianca.dto.pageable;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PagedResult<T> {

	public PagedResult(Page<T> page){
		super();
		this.content = page.getContent();
		this.page =  page.getNumber();
		this.totalElements =  page.getTotalElements();
		this.totalPages = page.getTotalPages();
		this.pageSize = page.getNumberOfElements();
	}

	public PagedResult(List<T> content,Page<?> page){
		super();
		this.content = content;
		this.page =  page.getNumber();
		this.totalElements =  page.getTotalElements();
		this.totalPages = page.getTotalPages();
		this.pageSize = page.getNumberOfElements();
	}

	public PagedResult(List<T> content, int page, long totalElements, long totalPages,int pageSize) {
		super();
		this.content = content;
		this.page = page;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
		this.pageSize = pageSize;
	}

	public PagedResult(List<T> content, int page, int pageSize, long totalElements) {
		super();
		this.content = content;
		this.page = page;
		this.pageSize = pageSize;
		this.totalElements = totalElements;
		this.totalPages = (totalElements / pageSize) + (totalElements % pageSize == 0 ? 0 : 1);
	}


	private List<T> content;

	private int page;

	private long totalElements;

	private long totalPages;

	private int pageSize;

	public boolean isEmpty() {
		return content==null || content.isEmpty();
	}

}
