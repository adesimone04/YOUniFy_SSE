package it.younify.modules.sge.controller;

import it.younify.modules.sgac.service.JwtService;
import it.younify.modules.sge.facade.EventFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
class SGEControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventFacade eventFacade;

    @MockitoBean private JwtService jwtService;
    @MockitoBean private UserDetailsService userDetailsService;
    @MockitoBean private AuthenticationManager authenticationManager;
    @MockitoBean private AuthenticationProvider authenticationProvider;


    private MockMultipartFile validPoster() {
        return new MockMultipartFile("poster", "locandina.jpg", "image/jpeg", "fake-image-content".getBytes());
    }

    private String futureDate() {
        return Instant.now().plus(10, ChronoUnit.DAYS).toString();
    }


    // TC1_SGE: Titolo Evento vuoto -> [ERR]
    @Test
    @WithMockUser(roles = "HOST")
    void createEvent_TC1_TitleEmpty() throws Exception {
        mockMvc.perform(multipart("/api/event/post")
                        .file(validPoster())
                        .param("name", "")
                        .param("description", "Descrizione valida")
                        .param("dateAndTime", futureDate())
                        .param("street", "Via Roma 1")
                        .param("city", "Salerno")
                        .param("CAP", "84084")
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }


    // TC2_SGE: Titolo Evento > 100 caratteri -> [ERR]
    @Test
    @WithMockUser(roles = "HOST")
    void createEvent_TC2_TitleTooLong() throws Exception {
        String longTitle = "a".repeat(101);

        mockMvc.perform(multipart("/api/event/post")
                        .file(validPoster())
                        .param("name", longTitle)
                        .param("description", "Descrizione valida")
                        .param("dateAndTime", futureDate())
                        .param("street", "Via Roma 1")
                        .param("city", "Salerno")
                        .param("CAP", "84084")
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }


    // TC3_SGE: Data e ora non inseriti -> [ERR]
    @Test
    @WithMockUser(roles = "HOST")
    void createEvent_TC3_DateNull() throws Exception {
        mockMvc.perform(multipart("/api/event/post")
                        .file(validPoster())
                        .param("name", "Serata Karaoke")
                        .param("description", "Descrizione valida")
                        // .param("dateAndTime", ...)
                        .param("street", "Via Roma 1")
                        .param("city", "Salerno")
                        .param("CAP", "84084")
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }


    // TC4_SGE: Data e ora nel passato -> [ERR]
    @Test
    @WithMockUser(roles = "HOST")
    void createEvent_TC4_DateInPast() throws Exception {
        String pastDate = "2012-12-21T00:00:00Z";

        mockMvc.perform(multipart("/api/event/post")
                        .file(validPoster())
                        .param("name", "Serata Karaoke")
                        .param("description", "Descrizione valida")
                        .param("dateAndTime", pastDate)
                        .param("street", "Via Roma 1")
                        .param("city", "Salerno")
                        .param("CAP", "84084")
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }



    // TC5_SGE: Luogo (Indirizzo/Street) vuoto -> [ERR]
    @Test
    @WithMockUser(roles = "HOST")
    void createEvent_TC5_StreetEmpty() throws Exception {
        mockMvc.perform(multipart("/api/event/post")
                        .file(validPoster())
                        .param("name", "Serata Karaoke")
                        .param("description", "Descrizione valida")
                        .param("dateAndTime", futureDate())
                        .param("street", "")
                        .param("city", "Salerno")
                        .param("CAP", "84084")
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }


    // TC7_SGE: Nessuna locandina selezionata -> [ERR]
    @Test
    @WithMockUser(roles = "HOST")
    void createEvent_TC7_PosterMissing() throws Exception {
        mockMvc.perform(multipart("/api/event/post")
                        // .file(validPoster()) // INPUT: File mancante
                        .param("name", "Serata Karaoke")
                        .param("description", "Descrizione valida")
                        .param("dateAndTime", futureDate())
                        .param("street", "Via Roma 1")
                        .param("city", "Salerno")
                        .param("CAP", "84084")
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }
}