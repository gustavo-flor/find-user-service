package com.github.gustavoflor.finduserservice.infrastructure.delivery;

import com.github.gustavoflor.finduserservice.core.User;
import com.github.gustavoflor.finduserservice.core.UserTestHelper;
import com.github.gustavoflor.finduserservice.infrastructure.persistence.UserRepository;
import com.github.gustavoflor.finduserservice.infrastructure.shared.Page;
import com.github.gustavoflor.finduserservice.infrastructure.shared.Pageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private static final String ENDPOINT = "/search";

    @Mock
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userRepository)).build();
    }

    @Test
    void shouldNotFindWhenQueryIsNull() throws Exception {
        doFindRequest("?query=").andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotFindWhenSizeIsNull() throws Exception {
        doFindRequest("?query=\"Marty McFly\"&size=").andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotFindWhenSizeIsNotNumeric() throws Exception {
        doFindRequest("?query=\"Marty McFly\"&size=Lorem").andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotFindWhenSizeIsZero() throws Exception {
        doFindRequest("?query=\"Marty McFly\"&size=0").andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotFindWhenSizeIsNegative() throws Exception {
        doFindRequest("?query=\"Marty McFly\"&size=-1").andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotFindWhenFromIsNull() throws Exception {
        doFindRequest("?query=\"Marty McFly\"&from=").andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotFindWhenFromIsNotNumeric() throws Exception {
        doFindRequest("?query=\"Marty McFly\"&from=Lorem").andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotFindWhenFromIsNegative() throws Exception {
        doFindRequest("?query=\"Marty McFly\"&from=-1").andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotFindWhenDebugIsNull() throws Exception {
        doFindRequest("?query=\"Marty McFly\"&debug=").andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotFindWhenDebugIsNotBoolean() throws Exception {
        doFindRequest("?query=\"Marty McFly\"&debug=Lorem").andExpect(status().isBadRequest());
    }

    @Test
    void shouldShowOnlyNonDebugFields() throws Exception {
        User martyMcfly = UserTestHelper.create("Marty McFly", "marty.mcfly", null, null);
        Mockito.doReturn(Page.of(List.of(martyMcfly), new Pageable())).when(userRepository).findAll(Mockito.any(), Mockito.any());
        doFindRequest("?query=\"Marty McFly\"")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id", is(martyMcfly.getId())))
                .andExpect(jsonPath("$.data[0].textScore").doesNotExist())
                .andExpect(jsonPath("$.data[0].relevance").doesNotExist());
    }

    @Test
    void shouldShowAllFields() throws Exception {
        User martyMcfly = UserTestHelper.create("Marty McFly", "marty.mcfly", 1, 1.5F);
        Mockito.doReturn(Page.of(List.of(martyMcfly), new Pageable())).when(userRepository).findAll(Mockito.any(), Mockito.any());
        doFindRequest("?query=\"Marty McFly\"")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id", is(martyMcfly.getId())))
                .andExpect(jsonPath("$.data[0].textScore",is(martyMcfly.getTextScore().doubleValue())))
                .andExpect(jsonPath("$.data[0].relevance", is(martyMcfly.getRelevance())));
    }

    private ResultActions doFindRequest(String query) throws Exception {
        return mockMvc.perform(get(ENDPOINT + query).accept(MediaType.APPLICATION_JSON));
    }

}
