package com.condominio.novaalianca.builder;

import com.condominio.novaalianca.dto.pageable.PageWrapper;
import com.condominio.novaalianca.dto.pageable.PagingDTO;
import org.springframework.data.domain.Page;

public final class PagingDTOBuilder {
    private PagingDTOBuilder() {}

    public static <T> PagingDTO from(Page<T> p) {
        PagingDTO dto = new PagingDTO();
        dto.setTotalElements(p.getTotalElements());
        dto.setSize(p.getSize());
        dto.setNumber(p.getNumber()+1); // frontend is always one number ahead
        dto.setLast(p.isLast());
        dto.setTotalPages(p.getTotalPages());
        dto.setFirst(p.isFirst());
        dto.setNumberOfElements(p.getNumberOfElements());
        dto.setEmpty(p.isEmpty());

        return dto;
    }

//    public static <T> PagingDTO from(long totalElements, int pageSize, int pageNumber) {
//      PagingDTO dto = new PagingDTO();
//
//      dto.setTotalResults(totalElements);
//      dto.setSize(pageSize);
//      dto.setNumber(pageNumber+1); // frontend is always one number ahead
//        dto.setLast(p.isLast());
//        dto.setTotalPages();
//        dto.setFirst();
//        dto.getNumberOfElements()
//        dto.setEmpty();
//
//      return dto;
//  }


}
