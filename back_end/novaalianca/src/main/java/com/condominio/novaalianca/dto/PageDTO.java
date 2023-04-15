package com.condominio.novaalianca.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter

@Builder
public class PageDTO<T> extends PageImpl<T> {
    private static final long serialVersionUID = 1L;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PageDTO(@JsonProperty("content") List<T> content,
                   @JsonProperty("number") int number,
                   @JsonProperty("size") int size,
                   @JsonProperty("totalElements") Long totalElements,
                   @JsonProperty("pageable") JsonNode pageable,
                   @JsonProperty("last") boolean last,
                   @JsonProperty("totalPages") int totalPages,
                   @JsonProperty("sort") JsonNode sort,
                   @JsonProperty("first") boolean first,
                   @JsonProperty("numberOfElements") int numberOfElements) {

        super(content, PageRequest.of(number, size), totalElements);
    }

    public PageDTO(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public PageDTO(List<T> content) {
        super(content);
    }

    public PageDTO() {
        super(new ArrayList<>());
    }
}