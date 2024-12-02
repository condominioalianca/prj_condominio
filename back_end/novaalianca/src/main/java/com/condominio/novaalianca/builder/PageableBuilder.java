package com.condominio.novaalianca.builder;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

public class PageableBuilder {

    private static final int DEFAULT_PAGE_SIZE = 100;
    private static final String ORDER_DESC = "DESC";
    private static final String ID_UNIDADE = "idUnidade";
    private static final String DT_EMISSAO = "dtEmissao";


    private PageableBuilder() {
    }

    public static Pageable from(Integer pageNumber, Integer pageSize, String orderBy, String order) {
        if (pageSize == null || pageSize == 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        if (pageNumber == null) {
            pageNumber = 0;
        } else if (pageNumber > 0) {
            pageNumber--; // frontend is always one number ahead
        }
        if (orderBy == null) {
            orderBy = DT_EMISSAO;
        }

        if (ORDER_DESC.equalsIgnoreCase(order)) {
            return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(orderBy)));
        } else {
            return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.by(orderBy)));
        }
    }

    public static Pageable from(Integer pageNumber, Integer pageSize) {

        return from(pageNumber, pageSize, null, null);
    }

    public static Pageable fromMultipleColumns(Integer pageNumber, Integer pageSize, Order... orders) {
        if (pageSize == null || pageSize == 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        if (pageNumber == null) {
            pageNumber = 0;
        } else if (pageNumber > 0) {
            pageNumber--; // frontend is always one number ahead
        }
        return PageRequest.of(pageNumber, pageSize, Sort.by(orders));
    }

}