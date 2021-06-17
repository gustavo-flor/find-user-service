package com.github.gustavoflor.finduserservice.core;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class UserTestHelper {

    public User create(String name, String username, Integer relevance) {
        return User.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .username(username)
                .relevance(relevance)
                .build();
    }

    public User create(String name, String username, Integer relevance, Float textScore) {
        User user = create(name, username, relevance);
        user.setTextScore(textScore);
        return user;
    }

}
