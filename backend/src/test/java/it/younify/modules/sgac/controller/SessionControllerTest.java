package it.younify.modules.sgac.controller;

import tools.jackson.databind.ObjectMapper;
import it.younify.modules.sgac.model.dto.request.Authentication2FARequest;
import it.younify.modules.sgac.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessionController.class)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private Authentication2FAService authentication2FAService;
    @MockitoBean
    private Generate2FAService generate2FAService;
    @MockitoBean
    private RefreshTokenService refreshTokenService;
    @MockitoBean
    private LogOutService logOutService;

    @MockitoBean private JwtService jwtService;
    @MockitoBean private UserDetailsService userDetailsService;
    @MockitoBean private AuthenticationManager authenticationManager;
    @MockitoBean private AuthenticationProvider authenticationProvider;

    // TC15_H_SGAC (Conferma 2FA Host - Codice Vuoto)
    @Test
    void confirm2FA_TC15_H_SGAC_EmptyCode() throws Exception {

        Authentication2FARequest request = new Authentication2FARequest();

        request.setID("host_mario_id");
        request.setCodeInserted("");

        mockMvc.perform(post("/api/auth/2FA")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}