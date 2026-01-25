package it.younify.modules.sgtr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.younify.modules.sgac.model.entity.Role;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.UserRepository;
import it.younify.modules.sgac.service.JwtService;
import it.younify.modules.sgtr.model.dto.request.CommentPostRequest;
import it.younify.modules.sgtr.model.dto.request.ThreadPostRequest;
import it.younify.modules.sgtr.model.dto.request.ThreadVoteRequest;
import it.younify.modules.sgtr.model.entity.Threads;
import it.younify.modules.sgtr.model.entity.VoteType;
import it.younify.modules.sgtr.repository.CommentRepository;
import it.younify.modules.sgtr.repository.ThreadRepository;
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
class SGTRSystemTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private ThreadRepository threadRepository;
    @Autowired private CommentRepository commentRepository;

    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;

    private User testUser;
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


        testUser = User.builder()
                .ID("U-" + UUID.randomUUID())
                .firstName("System").lastName("Tester").email("tester@studenti.unisa.it")
                .password("Pass123!")
                .university("Unisa").cdl("Informatica")
                .dateOfBirth(LocalDate.of(2000, 1, 1)).contactsInfo("test")
                .enabled(true)
                .role(Role.USER)
                .build();
        userRepository.saveAndFlush(testUser);


        String jwtToken = jwtService.generateToken(testUser);


        authCookie = new Cookie("access_token", jwtToken);
        authCookie.setHttpOnly(true);
        authCookie.setPath("/");
    }

    // TC4_SGTR: Thread valido -> [OK]
    @Test
    void systemTest_TC4_PostThread_Success() throws Exception {
        ThreadPostRequest request = new ThreadPostRequest();
        request.setContent("Non penso tu abbia capito cosa si intende.");
        request.setFlag("GENERALE");

        mockMvc.perform(post("/api/thread/post")
                        .cookie(authCookie)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertEquals(1, threadRepository.count());
    }

    // TC8_SGTR: Commento valido -> [OK]
    @Test
    void systemTest_TC8_PostComment_Success() throws Exception {

        Threads thread = Threads.builder()
                .content("Thread Base")
                .user(testUser)
                .flag(it.younify.modules.sgtr.model.entity.FlagCategory.GENERALE)
                .publicationDate(java.time.Instant.now())
                .CFU(0)
                .build();
        threadRepository.save(thread);

        CommentPostRequest request = new CommentPostRequest();
        request.setThreadId(thread.getID());
        request.setContent("Okay, sono d'accordo con te.");

        mockMvc.perform(post("/api/thread/comment/post")
                        .cookie(authCookie)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertEquals(1, commentRepository.count());
    }

    // TC12_SGTR: Upvote Coerente -> [OK]
    @Test
    void systemTest_TC12_VoteUp_Success() throws Exception {
        Threads thread = Threads.builder()
                .content("Thread da Votare")
                .user(testUser)
                .flag(it.younify.modules.sgtr.model.entity.FlagCategory.GENERALE)
                .publicationDate(java.time.Instant.now())
                .CFU(0)
                .build();
        threadRepository.save(thread);

        ThreadVoteRequest request = new ThreadVoteRequest();
        request.setThreadId(thread.getID());
        request.setVoteType(VoteType.UPVOTE);

        mockMvc.perform(post("/api/thread/react")
                        .cookie(authCookie)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertEquals(1, threadRepository.findById(thread.getID()).get().getCFU());
    }

    // TC16_SGTR: Downvote Coerente -> [OK]
    @Test
    void systemTest_TC16_VoteDown_Success() throws Exception {
        Threads thread = Threads.builder()
                .content("Thread da Votare")
                .user(testUser)
                .flag(it.younify.modules.sgtr.model.entity.FlagCategory.GENERALE)
                .publicationDate(java.time.Instant.now())
                .CFU(0)
                .build();
        threadRepository.save(thread);

        ThreadVoteRequest request = new ThreadVoteRequest();
        request.setThreadId(thread.getID());
        request.setVoteType(VoteType.DOWNVOTE);

        mockMvc.perform(post("/api/thread/react")
                        .cookie(authCookie)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertEquals(-1, threadRepository.findById(thread.getID()).get().getCFU());
    }
}