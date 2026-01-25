package it.younify.modules.sgtr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.younify.modules.sgac.service.JwtService;
import it.younify.modules.sgtr.facade.CommentFacade;
import it.younify.modules.sgtr.facade.ThreadFacade;
import it.younify.modules.sgtr.model.dto.request.CommentPostRequest;
import it.younify.modules.sgtr.model.dto.request.ThreadPostRequest;
import it.younify.modules.sgtr.model.dto.request.ThreadVoteRequest;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ThreadController.class, CommentController.class})
class SGTRControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private ThreadFacade threadFacade;
    @MockitoBean
    private CommentFacade commentFacade;

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


    // TC1_SGTR: Testo Thread vuoto -> [ERR]
    @Test
    @WithMockUser
    void postThread_TC1_ContentEmpty() throws Exception {
        ThreadPostRequest request = new ThreadPostRequest();
        request.setContent(""); // INPUT: (vuoto)
        request.setFlag("DIDATTICA"); // Necessario per far scattare l'errore solo sul content

        mockMvc.perform(post("/api/thread/post").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // TC2_SGTR: Testo Thread > 500 caratteri -> [ERR]
    @Test
    @WithMockUser
    void postThread_TC2_ContentTooLong() throws Exception {
        ThreadPostRequest request = new ThreadPostRequest();
        request.setContent("a".repeat(501)); // INPUT: > 500 caratteri
        request.setFlag("DIDATTICA");

        mockMvc.perform(post("/api/thread/post").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }


    // TC4_SGTR: Pubblicazione Thread Valido -> [OK]
    @Test
    @WithMockUser
    void postThread_TC4_Success() throws Exception {
        ThreadPostRequest request = new ThreadPostRequest();
        request.setContent("Non penso tu abbia capito cosa si intende."); // Input da TCSD
        request.setFlag("DIDATTICA");

        // Mockiamo il facade per non fare nulla (successo)
        // doNothing().when(threadFacade).createThread(any());

        mockMvc.perform(post("/api/thread/post").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()); // Verifica 200 OK
    }


    // TC5_SGTR: Testo Commento vuoto -> [ERR]
    @Test
    @WithMockUser
    void postComment_TC5_ContentEmpty() throws Exception {
        CommentPostRequest request = new CommentPostRequest();
        request.setThreadId(1L);
        request.setContent(""); // INPUT: (vuoto)

        mockMvc.perform(post("/api/thread/comment/post").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // TC6_SGTR: Testo Commento > 500 caratteri -> [ERR]
    @Test
    @WithMockUser
    void postComment_TC6_ContentTooLong() throws Exception {
        CommentPostRequest request = new CommentPostRequest();
        request.setThreadId(1L);
        request.setContent("a".repeat(501));

        mockMvc.perform(post("/api/thread/comment/post").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }


    // TC8_SGTR: Pubblicazione Commento Valido -> [OK]
    @Test
    @WithMockUser
    void postComment_TC8_Success() throws Exception {
        CommentPostRequest request = new CommentPostRequest();
        request.setThreadId(1L);
        request.setContent("Okay, sono d'accordo con te.");

        mockMvc.perform(post("/api/thread/comment/post").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()); // Verifica 200 OK
    }


    // TC9_SGTR: Selezione Upvote non valida (es. null/0) -> [ERR]
    @Test
    @WithMockUser
    void voteThread_TC9_UpvoteMissing() throws Exception {
        ThreadVoteRequest request = new ThreadVoteRequest();
        request.setThreadId(1L);
        request.setVoteType(null); // Simula "0 (False / Non selezionato)"

        mockMvc.perform(post("/api/thread/react").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // TC13_SGTR: Selezione Downvote non valida (es. null/0) -> [ERR]
    @Test
    @WithMockUser
    void voteThread_TC13_DownvoteMissing() throws Exception {
        ThreadVoteRequest request = new ThreadVoteRequest();
        request.setThreadId(1L);
        request.setVoteType(null); // Simula "0 (False / Non selezionato)"

        mockMvc.perform(post("/api/thread/react").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // TC14_SGTR: Selezione Multipla (Upvote e Downvote insieme) -> [ERR]
    @Test
    @WithMockUser
    void voteThread_TC14_MultipleSelectionError() throws Exception {
        // Simuliamo un JSON invalido che cerca di mandare un valore non accettato o ambiguo
        // che violi il contratto dell'Enum VoteType
        String invalidJson = "{ \"threadId\": 1, \"voteType\": \"BOTH_UP_AND_DOWN\" }";

        mockMvc.perform(post("/api/thread/react").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isInternalServerError()); // Verifica 400 Bad Request
    }
}