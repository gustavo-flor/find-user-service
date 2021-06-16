package com.github.gustavoflor.finduserservice.infrastructure.shared;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pageable {

    @NotNull
    @PositiveOrZero
    private Long from = 0L;

    @NotNull
    @Positive
    private Long size = 15L;

    @NotBlank
    private String query;

    private boolean debug;

}
