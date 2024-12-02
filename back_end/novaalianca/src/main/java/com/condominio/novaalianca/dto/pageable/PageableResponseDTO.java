package com.condominio.novaalianca.dto.pageable;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageableResponseDTO<T> {
    private PagingDTO paging;
    private List<T> content;

}