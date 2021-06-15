package com.github.gustavoflor.finduserservice.infrastructure.delivery;

import com.github.gustavoflor.finduserservice.core.User;
import com.github.gustavoflor.finduserservice.core.UserTestHelper;
import com.github.gustavoflor.finduserservice.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserControllerTest {

    private static final String ENDPOINT = "/search";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void shouldNotFindWhenQueryIsNull() throws Exception {
        doFindRequest("?query=").andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotFindWhenSizeIsNull() throws Exception {
        doFindRequest("?query=Marty McFly&size=").andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotFindWhenFromIsNull() throws Exception {
        doFindRequest("?query=Marty McFly&from=").andExpect(status().isBadRequest());
    }

    @Test
    void shouldSearchByTerm() throws Exception {
        User batman = UserTestHelper.create("Bruce Wayne", "batman", 2);
        User superman = UserTestHelper.create("Clark Kent", "superman", 1);
        userRepository.insert(List.of(batman, superman));

        doFindRequest("?query=" + batman.getUsername())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id", is(batman.getId())));
    }

    @Test
    void shouldSortByRelevance() throws Exception {
        String lukesName = "Luke Skywalker";
        User luke = UserTestHelper.create(lukesName, "luke", 2);
        User anakinSkywalker = UserTestHelper.create("Anakin Skywalker", "anakin.skywalker", 3);
        User lukeSkywalker = UserTestHelper.create(lukesName, "luke.skywalker", 1);
        User skywalker = UserTestHelper.create(lukesName, "skywalker", 3);
        userRepository.insert(List.of(luke, anakinSkywalker, lukeSkywalker, skywalker));

        doFindRequest("?query=" + lukesName)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].id", is(lukeSkywalker.getId())))
                .andExpect(jsonPath("$.data[1].id", is(luke.getId())))
                .andExpect(jsonPath("$.data[2].id", is(skywalker.getId())));
    }

    @Test
    void shouldPaginate() throws Exception {
        String brownsName = "Brown";
        User martyMcFly = UserTestHelper.create("Marty McFly", "marty.mcfly", 1);
        User doctorBrown = UserTestHelper.create(brownsName, "doctor.brow", 1);
        User charlieBrown = UserTestHelper.create(brownsName, "charlie.brown", 2);
        userRepository.insert(List.of(martyMcFly, doctorBrown, charlieBrown));

        doFindRequest("?from=1&size=1&query=" + brownsName)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id", is(charlieBrown.getId())));
    }

    private ResultActions doFindRequest(String query) throws Exception {
        return mockMvc.perform(get(ENDPOINT + query).accept(MediaType.APPLICATION_JSON));
    }

}
