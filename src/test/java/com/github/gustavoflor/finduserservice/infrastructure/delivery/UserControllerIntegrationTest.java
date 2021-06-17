package com.github.gustavoflor.finduserservice.infrastructure.delivery;

import com.github.gustavoflor.finduserservice.core.User;
import com.github.gustavoflor.finduserservice.core.UserTestHelper;
import com.github.gustavoflor.finduserservice.infrastructure.persistence.UserRepository;
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

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserControllerIntegrationTest {

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
    void shouldSearchByTerm() throws Exception {
        User batman = UserTestHelper.create("Bruce Wayne", "batman", 2);
        User superman = UserTestHelper.create("Clark Kent", "superman", 1);
        userRepository.insert(List.of(batman, superman));

        doFindRequest("?query=batman")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id", is(batman.getId())));
    }

    @Test
    void shouldSearchWithoutCaseSensitive() throws Exception {
        User goku = UserTestHelper.create("Goku", "goku", 1);
        User vegeta = UserTestHelper.create("Vegeta", "vegeta", 1);
        User picollo = UserTestHelper.create("Picollo", "picollo", 1);
        userRepository.insert(List.of(goku, vegeta, picollo));

        doFindRequest("?query=piCOlLo").andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id", is(picollo.getId())));
    }

    @Test
    void shouldSearchWithDiacrictInsensitive() throws Exception {
        User zeCarioca = UserTestHelper.create("Zé carioca", "carioca", 1);
        User picaPau = UserTestHelper.create("Pica Pau", "pica.pau", 1);
        userRepository.insert(List.of(zeCarioca, picaPau));

        doFindRequest("?query=ze")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id", is(zeCarioca.getId())));
    }

    @Test
    void shouldSearchWithStemming() throws Exception {
        User pedro = UserTestHelper.create("Pedro", "pedro", 1);
        User lucas = UserTestHelper.create("Lucas", "lucas", 1);
        userRepository.insert(List.of(pedro, lucas));

        doFindRequest("?query=pedra")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id", is(pedro.getId())));
    }

    @Test
    void shouldNotSearchWithPhraseOnStemming() throws Exception {
        User pedro = UserTestHelper.create("Pedro", "pedro", 1);
        User lucas = UserTestHelper.create("Lucas", "lucas", 1);
        userRepository.insert(List.of(pedro, lucas));

        doFindRequest("?query=\"pedra\"")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    void shouldSortByRelevance() throws Exception {
        User anakinSkywalker = UserTestHelper.create("Anakin Skywalker", "anakin.skywalker", 2);
        User lukeSkywalker = UserTestHelper.create("Luke Skywalker", "luke.skywalker", 1);
        User chewbacca = UserTestHelper.create("Chewbacca", "chewbacca", 3);
        userRepository.insert(List.of(chewbacca, anakinSkywalker, lukeSkywalker));

        doFindRequest("?query=skywalker")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id", is(lukeSkywalker.getId())))
                .andExpect(jsonPath("$.data[1].id", is(anakinSkywalker.getId())));
    }

    @Test
    void shouldPaginate() throws Exception {
        User martyMcFly = UserTestHelper.create("Marty McFly", "marty.mcfly", 1);
        User doctorBrown = UserTestHelper.create("Doctor Brown", "doctor.brown", 1);
        User charlieBrown = UserTestHelper.create("Charlie Brown", "charlie.brown", 2);
        userRepository.insert(List.of(martyMcFly, doctorBrown, charlieBrown));

        doFindRequest("?from=1&size=1&query=brown")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id", is(charlieBrown.getId())));
    }

    private ResultActions doFindRequest(String query) throws Exception {
        return mockMvc.perform(get(ENDPOINT + query).accept(MediaType.APPLICATION_JSON));
    }

}
