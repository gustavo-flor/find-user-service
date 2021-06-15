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

    private List<E> data;
    private Long from = 0L;
    private Long size = 15L;

    public static <T> Page<T> of(List<T> data, Pageable pageable) {
        return Page.<T>builder()
                .data(data)
                .from(pageable.getFrom())
                .size(pageable.getSize())
                .build();
    }

}
