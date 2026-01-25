package it.younify.modules.sgm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.younify.modules.sgac.service.JwtService;
import it.younify.modules.sgm.facade.MatchFacade;
import it.younify.modules.sgm.model.dto.request.MatchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MatchController.class)
class SGMControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private MatchFacade matchFacade;


    @MockitoBean private JwtService jwtService;
    @MockitoBean private UserDetailsService userDetailsService;
    @MockitoBean private AuthenticationManager authenticationManager;
    @MockitoBean private AuthenticationProvider authenticationProvider;


    @TestConfiguration
    static class TestConfig {
        @Bean
        public ObjectMapper objectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper;
        }
    }

    // TC1_SGM: Azione Match (Selected) = Nessuna selezione (M=0) -> [ERR]
    @Test
    @WithMockUser(roles = "USER")
    void sendMatch_TC1_SelectionMissing() throws Exception {
        MatchRequest request = new MatchRequest();
        request.setReceiverID("");

        mockMvc.perform(post("/api/match/send")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}