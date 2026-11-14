package com.localmart.backend.common;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

// One page of results + info the app needs to show "next page" buttons.
// Generic <T>: works for products, orders, anything.
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {

    // Page of entities (E) → page of DTOs (T)
    public static <E, T> PageResponse<T> from(Page<E> page, Function<E, T> mapper) {
        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
