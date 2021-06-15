package com.github.gustavoflor.finduserservice.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.TextScore;

@Data
public abstract class Searchable {

    public static final String TEXT_SCORE_FIELD = "textScore";

    @JsonIgnore
    @TextScore
    private Float textScore;

}
