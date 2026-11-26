package it.younify.modules.sgac.service;

import it.younify.core.exception.BadRequestException;
import it.younify.modules.sgac.adapter.emailsender.EmailProvider;
import it.younify.modules.sgac.model.dto.request.Authentication2FARequest;
import it.younify.modules.sgac.model.entity.Code2FA;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.Code2FARepository;
import it.younify.modules.sgac.repository.HostRepository;
import it.younify.modules.sgac.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Authentication2FAServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private HostRepository hostRepository;
    @Mock private Code2FARepository code2FARepository;
    @Mock private EmailProvider emailProvider;

    @InjectMocks
    private Authentication2FAService authentication2FAService;

    // TC16_H_SGAC / TC15_UR_SGAC: Codice 2FA errato
    @Test
    void confirm2FA_ShouldThrow_WhenCodeIncorrect() {

        String userId = "U-123";
        Authentication2FARequest request = new Authentication2FARequest(userId, "WRONG1");

        Code2FA storedCode = Code2FA.builder()
                .code("123456")
                .expiryDate(Instant.now().plus(3, ChronoUnit.MINUTES))
                .errorCount(0)
                .build();

        when(code2FARepository.findByOwnerId(userId)).thenReturn(Optional.of(storedCode));

        assertThrows(BadRequestException.class, () -> authentication2FAService.confirm2FA(request));

        // Verifica incremento contatore errori
        verify(code2FARepository, times(1)).save(storedCode);
    }

    // TC17_H_SGAC / TC16_UR_SGAC: Codice 2FA corretto
    @Test
    void confirm2FA_Host_ShouldSucceed() {

        String hostId = "H-123";
        Authentication2FARequest request = new Authentication2FARequest(hostId, "123456");

        Code2FA storedCode = Code2FA.builder()
                .code("123456")
                .expiryDate(Instant.now().plus(3, ChronoUnit.MINUTES))
                .errorCount(0)
                .build();

        Host host = new Host();
        host.setID(hostId);
        host.setHostName("Test Bar");
        host.setPEC("openapi@legalmail.it");

        when(code2FARepository.findByOwnerId(hostId)).thenReturn(Optional.of(storedCode));
        when(hostRepository.findById(hostId)).thenReturn(Optional.of(host));

        assertDoesNotThrow(() -> authentication2FAService.confirm2FA(request));

        verify(hostRepository).save(host); // Verifica abilitazione account
        verify(emailProvider).sendWelcomeEmail(anyString(), anyString());
    }

    // TC16_UR_SGAC: Codice 2FA corretto (User)
    @Test
    void confirm2FA_User_ShouldSucceed() {

        String userId = "U-123";
        Authentication2FARequest request = new Authentication2FARequest(userId, "123456");

        Code2FA storedCode = Code2FA.builder()
                .code("123456")
                .expiryDate(Instant.now().plus(3, ChronoUnit.MINUTES))
                .errorCount(0)
                .build();

        User user = new User();
        user.setID(userId);
        user.setFirstName("Mario");
        user.setEmail("mario.rossi@studenti.unisa.it");

        when(code2FARepository.findByOwnerId(userId)).thenReturn(Optional.of(storedCode));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> authentication2FAService.confirm2FA(request));

        verify(userRepository).save(user); // Verifica abilitazione account
        verify(emailProvider).sendWelcomeEmail(anyString(), anyString());
    }
}