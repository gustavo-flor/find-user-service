package com.github.gustavoflor.finduserservice.infrastructure.delivery;

import com.github.gustavoflor.finduserservice.core.User;
import com.github.gustavoflor.finduserservice.infrastructure.persistence.UserRepository;
import com.github.gustavoflor.finduserservice.infrastructure.shared.Page;
import com.github.gustavoflor.finduserservice.infrastructure.shared.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private static final String SORT_BY = "relevance";

    private final UserRepository userRepository;

    @CrossOrigin
    @Transactional
    @GetMapping("/search")
    public Page<User> search(@Valid Pageable pageable) {
        return userRepository.findAll(pageable, SORT_BY);
    }

}
