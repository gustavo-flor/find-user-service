package com.github.gustavoflor.finduserservice.infrastructure.delivery;

import com.github.gustavoflor.finduserservice.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private ResultActions doFindRequest(String query) throws Exception {
        return mockMvc.perform(get(ENDPOINT + query).accept(MediaType.APPLICATION_JSON));
    }

}
