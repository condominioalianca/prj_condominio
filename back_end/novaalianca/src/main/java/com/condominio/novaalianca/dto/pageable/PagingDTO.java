package com.condominio.novaalianca.dto.pageable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagingDTO {
    private int number;
    private int size;
    private boolean last;
    private Long totalElements;
    private int totalPages;
    private boolean first;
    private int numberOfElements;
    private boolean empty;

}
