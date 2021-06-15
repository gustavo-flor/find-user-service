package com.github.gustavoflor.finduserservice.infrastructure.configuration;

import com.github.gustavoflor.finduserservice.core.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;

import javax.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
@DependsOn("mongoTemplate")
public class TermIndexConfig {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndexes() {
        TextIndexDefinition termIndex = new TextIndexDefinition.TextIndexDefinitionBuilder()
                .onField("name")
                .onField("username")
                .withDefaultLanguage("portuguese")
                .build();
        mongoTemplate.indexOps(User.class).ensureIndex(termIndex);
    }

}
