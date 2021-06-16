package com.github.gustavoflor.finduserservice.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.TextScore;

@Data
public abstract class Searchable {

    public static final String TEXT_SCORE_FIELD = "textScore";

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TextScore
    private Float textScore;

}
