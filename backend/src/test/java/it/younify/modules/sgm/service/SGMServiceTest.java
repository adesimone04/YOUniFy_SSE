package it.younify.modules.sgm.service;

import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.UserRepository;
import it.younify.modules.sgm.model.dto.request.MatchRequest;
import it.younify.modules.sgm.model.entity.Match;
import it.younify.modules.sgm.repository.MatchRepository;
import it.younify.modules.sgn.facade.NotificationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SGMServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private NotificationFacade notificationFacade;
    @Mock private MatchRepository matchRepository;
    @Mock private SecurityContext securityContext;

    @InjectMocks private MatchSendService matchSendService;

    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {

        sender = User.builder().ID("sender123").firstName("Sender").lastName("Rossi").build();
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(sender, null));

        // Setup Receiver
        receiver = User.builder().ID("receiver456").firstName("Receiver").build();
    }

    // TC2_SGM: Azione Match OK (M=1) ma Notifica KO (N=0) -> [ERR]
    @Test
    void sendMatch_TC2_NotificationFailure() {
        MatchRequest request = new MatchRequest();
        request.setReceiverID("receiver456");

        when(userRepository.findById("receiver456")).thenReturn(Optional.of(receiver));

        // Simuliamo errore nell'invio della notifica
        doThrow(new RuntimeException("Notifica fallita")).when(notificationFacade).send(any());

        assertThrows(RuntimeException.class, () -> matchSendService.send(request));

        // Verifica che il save sia stato tentato (ma la transazione rollbackerebbe)
        verify(matchRepository).save(any(Match.class));
    }


    // TC3_SGM: Azione Match OK (M=1) e Notifica OK (N=1) -> [OK]
    @Test
    void sendMatch_TC3_Success() {
        // ARRANGE
        MatchRequest request = new MatchRequest();
        request.setReceiverID("receiver456");

        // Simuliamo che il receiver esista
        when(userRepository.findById("receiver456")).thenReturn(Optional.of(receiver));

        // Simuliamo che la notifica vada a buon fine (default del mock void è doNothing)
        // doNothing().when(notificationFacade).send(any());

        // ACT
        matchSendService.send(request);

        // ASSERT
        // Verifica che il match sia stato salvato
        verify(matchRepository, times(1)).save(any(Match.class));

        // Verifica che la notifica sia stata inviata
        verify(notificationFacade, times(1)).send(any());
    }
}