package com.condominio.novaalianca.dto.pageable;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageWrapper<T> {

	private long pageSize;
    private long pageNumber;
    private long offset;
    private long total;
    private List<T> data;
}
