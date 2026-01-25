package it.younify.modules.sgm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.younify.modules.sgac.model.entity.Role;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.UserRepository;
import it.younify.modules.sgac.service.JwtService;
import it.younify.modules.sgm.model.dto.request.MatchRequest;
import it.younify.modules.sgm.repository.MatchRepository;
import it.younify.modules.sgn.facade.NotificationFacade;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SGMSystemTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private MatchRepository matchRepository;

    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;

    @MockitoBean
    private NotificationFacade notificationFacade;

    private User requester;
    private User target;
    private Cookie authCookie;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ObjectMapper objectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper;
        }
    }

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


        requester = User.builder()
                .ID("U-" + UUID.randomUUID())
                .firstName("Requester")
                .lastName("Requester")
                .email("req@unisa.it")
                .password(passwordEncoder.encode("Pass1!"))
                .dateOfBirth(LocalDate.now())
                .role(Role.USER)
                .enabled(true)
                .university("Unisa")
                .cdl("Informatica")
                .contactsInfo("re.unisa")
                .build();
        userRepository.saveAndFlush(requester);

        target = User.builder()
                .ID("U-" + UUID.randomUUID())
                .firstName("Target")
                .lastName("Target")
                .email("target@unisa.it")
                .password(passwordEncoder.encode("Pass1!"))
                .dateOfBirth(LocalDate.now())
                .role(Role.USER)
                .enabled(true)
                .university("Unisa")
                .cdl("Informatica")
                .contactsInfo("target.unisa")
                .build();
        userRepository.saveAndFlush(target);

        String jwtToken = jwtService.generateToken(requester);

        authCookie = new Cookie("access_token", jwtToken);
        authCookie.setHttpOnly(true);
        authCookie.setPath("/");
    }



    // TC3_SGM: Azione Match OK (M=1) e Notifica OK (N=1) -> [OK]
    @Test
    void requestMatch_TC3_Success() throws Exception {

        MatchRequest request = new MatchRequest();
        request.setReceiverID(target.getID());

        mockMvc.perform(post("/api/match/send")
                        .cookie(authCookie)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertEquals(1, matchRepository.count());
    }
}