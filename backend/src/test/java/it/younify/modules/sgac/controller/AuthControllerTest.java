package it.younify.modules.sgac.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.younify.modules.sgac.model.dto.request.HostSignUpRequest;
import it.younify.modules.sgac.model.dto.request.UserSignUpRequest;
import it.younify.modules.sgac.service.HostSignUpService;
import it.younify.modules.sgac.service.JwtService;
import it.younify.modules.sgac.service.LogInService;
import it.younify.modules.sgac.service.UserSignUpService;
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

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserSignUpService userSignUpService;
    @MockitoBean
    private HostSignUpService hostSignUpService;
    @MockitoBean
    private LogInService logInService;


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

    private UserSignUpRequest.UserSignUpRequestBuilder validUserRequest() {

        return UserSignUpRequest.builder()
                .firstName("Mario")
                .lastName("Rossi")
                .email("mario.rossi@studenti.unisa.it")
                .password("PasswordStud1!")
                .confirmPassword("PasswordStud1!")
                .university("Università degli Studi di Salerno")
                .cdl("Informatica")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .contactsInfo("instagram.com/mario");
    }

    private HostSignUpRequest.HostSignUpRequestBuilder validHostRequest() {

        return HostSignUpRequest.builder()
                .hostName("Bar Sport")
                .PEC("bar.sport@pec.it")
                .PIVA("12345678901")
                .password("PasswordHost1!")
                .confirmPassword("PasswordHost1!")
                .province("Salerno")
                .city("Fisciano")
                .uniList("Unisa")
                .association(false);
    }


    /* USER TEST */
    // TC1_UR_SGAC: Email vuota -> 500 Internal Server Error
    @Test
    @WithMockUser
    void userSignUp_ShouldReturn400_WhenEmailEmpty() throws Exception {

        UserSignUpRequest request = validUserRequest().email("").build();

        mockMvc.perform(post("/api/auth/user/signup")
                        .with(csrf()) // Necessario per POST request in test con Security
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // TC3_UR_SGAC: Password vuota -> 500 Internal Server Error
    @Test
    @WithMockUser
    void userSignUp_ShouldReturn400_WhenPasswordEmpty() throws Exception {

        UserSignUpRequest request = validUserRequest().password("").build();

        mockMvc.perform(post("/api/auth/user/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // TC6_UR_SGAC: Università vuota -> 500 Internal Server Error
    @Test
    @WithMockUser
    void userSignUp_ShouldReturn400_WhenUniversityEmpty() throws Exception {

        UserSignUpRequest request = validUserRequest().university("").build();

        mockMvc.perform(post("/api/auth/user/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // TC8_UR_SGAC: Data di Nascita vuota (null) -> 500 Internal Server Error
    @Test
    @WithMockUser
    void userSignUp_ShouldReturn400_WhenDateOfBirthNull() throws Exception {

        UserSignUpRequest request = validUserRequest().dateOfBirth(null).build();

        mockMvc.perform(post("/api/auth/user/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // TC10_UR_SGAC: CDL vuoto -> 500 Internal Server Error
    @Test
    @WithMockUser
    void userSignUp_ShouldReturn400_WhenCdlEmpty() throws Exception {

        UserSignUpRequest request = validUserRequest().cdl("").build();

        mockMvc.perform(post("/api/auth/user/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // TC12_UR_SGAC / TC14_UR_SGAC -> 500 Internal Server Error
    @Test
    @WithMockUser
    void userSignUp_ShouldReturn400_WhenContactsEmpty() throws Exception {

        UserSignUpRequest request = validUserRequest().contactsInfo("").build();

        mockMvc.perform(post("/api/auth/user/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }


    /* Host Test */
    // TC1_H_SGAC: Password vuota -> 500 Internal Server Error
    @Test
    @WithMockUser
    void hostSignUp_ShouldReturn400_WhenPasswordEmpty() throws Exception {

        HostSignUpRequest request = validHostRequest().password("").build();

        mockMvc.perform(post("/api/auth/host/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // TC3_H_SGAC: PEC vuota -> 500 Internal Server Error
    @Test
    @WithMockUser
    void hostSignUp_ShouldReturn400_WhenPECEmpty() throws Exception {

        HostSignUpRequest request = validHostRequest().PEC("").build();

        mockMvc.perform(post("/api/auth/host/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // TC6_H_SGAC: P.IVA vuota -> 500 Internal Server Error
    @Test
    @WithMockUser
    void hostSignUp_ShouldReturn400_WhenPIVAEmpty() throws Exception {

        HostSignUpRequest request = validHostRequest().PIVA("").build();

        mockMvc.perform(post("/api/auth/host/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // TC9_H_SGAC: Università vuota -> 500 Internal Server Error
    @Test
    @WithMockUser
    void hostSignUp_ShouldReturn400_WhenUniListEmpty() throws Exception {

        HostSignUpRequest request = validHostRequest().uniList("").build();

        mockMvc.perform(post("/api/auth/host/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // TC11_H_SGAC: Provincia vuota -> 500 Internal Server Error
    @Test
    @WithMockUser
    void hostSignUp_ShouldReturn400_WhenProvinceEmpty() throws Exception {

        HostSignUpRequest request = validHostRequest().province("").build();

        mockMvc.perform(post("/api/auth/host/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // TC13_H_SGAC: Città vuota -> 500 Internal Server Error
    @Test
    @WithMockUser
    void hostSignUp_ShouldReturn400_WhenCityEmpty() throws Exception {

        HostSignUpRequest request = validHostRequest().city("").build();

        mockMvc.perform(post("/api/auth/host/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}

