package com.example.foodbe.utils;

import com.example.foodbe.payload.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class PageMapperUtils2 {

    public <D,E> PageResponse<D> toPageResponseDto(Page<E> page, Function<E,D> mapper){

        List<D> listDto = page.getContent().stream()
                .map(mapper)
                .toList();

        return PageResponse.<D>builder()
                .content(listDto)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .build();
    }

}
