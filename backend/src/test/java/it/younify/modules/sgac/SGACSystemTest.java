package it.younify.modules.sgac;

import tools.jackson.databind.ObjectMapper;
import it.younify.modules.sgac.adapter.PIVAlidator.PIVAlidator;
import it.younify.modules.sgac.adapter.emailsender.EmailProvider;
import it.younify.modules.sgac.model.dto.request.HostSignUpRequest;
import it.younify.modules.sgac.model.dto.request.UserSignUpRequest;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.Code2FARepository;
import it.younify.modules.sgac.repository.HostRepository;
import it.younify.modules.sgac.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SGACSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private  HostRepository hostRepository;

    @Autowired
    private Code2FARepository code2FARepository;

    @MockitoBean
    private EmailProvider emailProvider;

    @MockitoBean
    private PIVAlidator pivalidator;

    //TC16_UR_SGAC - Happy Path
    @Test
    void systemTest_UserSignUp_FullFlow() throws Exception {

        UserSignUpRequest request = UserSignUpRequest.builder()
                .firstName("System")
                .lastName("Tester")
                .email("mario.rossi@studenti.unisa.it")
                .password("PasswordSicura1!")
                .confirmPassword("PasswordSicura1!")
                .university("Università degli Studi di Salerno")
                .cdl("L-31: Scienze e tecnologie informatiche")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .contactsInfo("instagram.com/systemtest")
                .build();


        mockMvc.perform(post("/api/auth/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accountEmail").value("mario.rossi@studenti.unisa.it"));


        User savedUser = userRepository.findByEmail("mario.rossi@studenti.unisa.it").orElse(null);
        assertNotNull(savedUser, "L'utente dovrebbe essere stato salvato nel DB");

        assertFalse(savedUser.isEnabled(), "L'utente deve essere disabilitato prima della 2FA");


        assertTrue(code2FARepository.findByOwnerId(savedUser.getID()).isPresent(), "Il codice 2FA deve essere stato generato e salvato");
    }


    // TC17_H_SGAC - Happy Path
    @Test
    void systemTest_HostSignUp_FullFlow() throws Exception {
        when(pivalidator.PIVAlidatePEC("12485671007")).thenReturn("openapi@legalmail.it");

        HostSignUpRequest request = HostSignUpRequest.builder()
                .hostName("Bar Sistema")
                .PEC("openapi@legalmail.it")
                .PIVA("12485671007")
                .password("PasswordHost1!")
                .confirmPassword("PasswordHost1!")
                .province("Salerno")
                .city("Fisciano")
                .uniList("Università degli Studi di Salerno")
                .association(false)
                .build();


        mockMvc.perform(post("/api/auth/host/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());


        assertTrue(hostRepository.findByPEC("openapi@legalmail.it").isPresent());
    }
}