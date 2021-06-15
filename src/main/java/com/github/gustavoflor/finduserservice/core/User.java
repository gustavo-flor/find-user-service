package com.github.gustavoflor.finduserservice.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends Searchable {

    @MongoId
    private String id;

    private String name;

    private String username;

    @JsonIgnore
    private Integer relevance;

}
