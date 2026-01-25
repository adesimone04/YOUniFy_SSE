package it.younify.modules.sge.service;

import it.younify.core.exception.FileStorageException;
import it.younify.core.service.ImageStorageService;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.service.validator.GeoValidationService;
import it.younify.modules.sge.model.dto.request.EventPostRequest;
import it.younify.modules.sge.model.entity.Event;
import it.younify.modules.sge.repository.EventRepository;
import it.younify.modules.sgtr.service.CensorshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SGEServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private GeoValidationService geoValidationService;
    @Mock private ImageStorageService imageStorageService;
    @Mock private CensorshipService censorshipService;
    @Mock private SecurityContext securityContext;

    @InjectMocks private EventPostService eventPostService;

    private EventPostRequest validRequest;
    private Host host;

    @BeforeEach
    void setUp() {
        host = Host.builder().ID("host1").hostName("Host Test").build();
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(host, null));

        validRequest = new EventPostRequest();
        validRequest.setName("Serata Karaoke");
        validRequest.setDescription("Descrizione evento");
        validRequest.setDateAndTime(Instant.now().plusSeconds(86400));
        validRequest.setStreet("Via Giovanni Paolo II, 132");
        validRequest.setCity("Fisciano");
        validRequest.setCAP("84084");
    }

    //TC8_SGE (Formato File)
    @Test
    void createEvent_TC8_PosterInvalidFormat() {

        MockMultipartFile invalidPoster = new MockMultipartFile("poster", "SerataKaraoke.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "content".getBytes());
        validRequest.setPoster(invalidPoster);

        doThrow(new FileStorageException("File non valido", "INVALID_FORMAT"))
                .when(imageStorageService).uploadImage(eq(invalidPoster), anyString(), anyString());


        assertThrows(FileStorageException.class, () -> eventPostService.post(validRequest));

        verify(eventRepository, never()).save(any());
    }

    //TC9_SGE (Formato File)
    @Test
    void createEvent_TC9_Success() {

        validRequest.setCity("Fisciano");
        MockMultipartFile poster = new MockMultipartFile("poster", "SerataKaraoke.jpg", "image/jpeg", "img".getBytes());
        validRequest.setPoster(poster);

        when(eventRepository.findExistingEvent(any(), any(), any())).thenReturn(Optional.empty());
        when(imageStorageService.uploadImage(eq(poster), anyString(), anyString()))
                .thenReturn("events/fake-image.webp");
        when(censorshipService.containsSensitiveContent(anyString())).thenReturn(false);

        eventPostService.post(validRequest);

        verify(eventRepository, times(1)).save(any(Event.class));
    }
}