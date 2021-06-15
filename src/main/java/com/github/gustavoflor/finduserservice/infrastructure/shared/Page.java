package com.github.gustavoflor.finduserservice.infrastructure.shared;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Page<E> {

    private Long from = 0L;
    private Long size = 15L;
    private List<E> data;

    public static <T> Page<T> of(List<T> data, Pageable pageable) {
        return Page.<T>builder()
                .from(pageable.getFrom())
                .size(pageable.getSize())
                .data(data)
                .build();
    }

}
