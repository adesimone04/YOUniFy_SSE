package it.younify.modules.sge.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.Role; // Assicurati di importare l'enum Role
import it.younify.modules.sgac.repository.HostRepository;
import it.younify.modules.sgac.service.JwtService; // Import necessario
import it.younify.modules.sge.repository.EventRepository;
import jakarta.servlet.http.Cookie; // Import necessario
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder; // Import necessario
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; // Import CSRF
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SGESystemTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private EventRepository eventRepository;
    @Autowired private HostRepository hostRepository;

    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;

    private Host host;
    private Cookie authCookie;

    @BeforeEach
    void setUp() {

        host = Host.builder()
                .ID("H-" + UUID.randomUUID())
                .hostName("Org")
                .PEC("org@pec.it")
                .password(passwordEncoder.encode("Pass1!"))
                .PIVA("12345678901")
                .role(Role.HOST)
                .province("Salerno")
                .city("Fisciano")
                .uniList("Unisa")
                .build();

        hostRepository.saveAndFlush(host);

        String jwtToken = jwtService.generateToken(host);


        authCookie = new Cookie("access_token", jwtToken);
        authCookie.setHttpOnly(true);
        authCookie.setPath("/");
    }

    // TC9_SGE: Creazione Evento con successo -> [OK]
    @Test
    void systemTest_TC9_CreateEvent_Success() throws Exception {

        String futureDate = Instant.now().plus(20, ChronoUnit.DAYS).toString();

        ClassPathResource resource = new ClassPathResource("img.jpg");
        byte[] imageBytes = Files.readAllBytes(resource.getFile().toPath());

        MockMultipartFile poster = new MockMultipartFile(
                "poster",
                "img.jpg",
                "image/jpeg",
                imageBytes
        );



        mockMvc.perform(multipart("/api/event/post")
                        .file(poster)
                        .param("name", "Serata Karaoke")
                        .param("description", "Descrizione")
                        .param("dateAndTime", futureDate)
                        .param("street", "Via Roma 1")
                        .param("city", "Fisciano")
                        .param("CAP", "84084")
                        .cookie(authCookie)
                        .with(csrf()))
                .andExpect(status().isOk());

        assertEquals(1, eventRepository.count());
    }
}