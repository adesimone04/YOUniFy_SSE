package it.younify.modules.sgac.service;

import it.younify.core.exception.BadRequestException;
import it.younify.modules.sgac.adapter.PIVAlidator.PIVAlidator;
import it.younify.modules.sgac.model.dto.request.HostSignUpRequest;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.repository.HostRepository;
import it.younify.modules.sgac.service.validator.GeoValidationService;
import it.younify.modules.sgac.service.validator.UniversityValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HostSignUpServiceTest {

    @Mock private HostRepository hostRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private PIVAlidator pivalidator;
    @Mock private Generate2FAService generate2FAService;
    @Mock private GeoValidationService geoValidationService;
    @Mock private UniversityValidationService universityValidationService;

    @InjectMocks
    private HostSignUpService hostSignUpService;

    private HostSignUpRequest.HostSignUpRequestBuilder validRequestBuilder;

    @BeforeEach
    void setUp() {

        validRequestBuilder = HostSignUpRequest.builder()
                .hostName("Bar Sport")
                .PEC("openapi@legalmail.it")
                .PIVA("12485671007")
                .password("PasswordHost1!")
                .confirmPassword("PasswordHost1!")
                .province("Salerno")
                .city("Fisciano")
                .uniList("Università degli Studi di Salerno")
                .association(false);
    }

    // TC4, TC5: Password invalida (corta o semplice)
    @Test
    void signUp_ShouldThrow_WhenPasswordInvalid() {

        when(hostRepository.findByPEC(anyString())).thenReturn(Optional.empty());
        when(geoValidationService.validateGeo(anyString(), anyString())).thenReturn(true);
        when(universityValidationService.isValidUniversity(anyList())).thenReturn(true);
        when(pivalidator.PIVAlidatePEC(anyString())).thenReturn("bar.sport@pec.it");

        // Caso Password Corta
        HostSignUpRequest shortPassRequest = validRequestBuilder
                .password("short1!")
                .confirmPassword("short1!")
                .build();
        assertThrows(BadRequestException.class, () -> hostSignUpService.signUp(shortPassRequest));

        // Caso Password Semplice
        HostSignUpRequest simplePassRequest = validRequestBuilder
                .password("passwordlungasemplice")
                .confirmPassword("passwordlungasemplice")
                .build();
        assertThrows(BadRequestException.class, () -> hostSignUpService.signUp(simplePassRequest));
    }

    // TC2: PEC non valida (pattern)
    @Test
    void signUp_ShouldThrow_WhenPECInvalidPattern() {

        HostSignUpRequest request = validRequestBuilder
                .PEC("mario.rossi-hostpec.it") // Manca la @ o dominio errato
                .build();

        when(hostRepository.findByPEC(anyString())).thenReturn(Optional.empty());
        when(geoValidationService.validateGeo(anyString(), anyString())).thenReturn(true);
        when(universityValidationService.isValidUniversity(anyList())).thenReturn(true);
        when(pivalidator.PIVAlidatePEC(anyString())).thenReturn(request.getPEC());

        assertThrows(BadRequestException.class, () -> hostSignUpService.signUp(request));
    }

    // TC8: PIVA non associata alla PEC
    @Test
    void signUp_ShouldThrow_WhenPIVANotMatchPEC() {
        HostSignUpRequest request = validRequestBuilder.build();

        when(hostRepository.findByPEC(anyString())).thenReturn(Optional.empty());
        when(geoValidationService.validateGeo(anyString(), anyString())).thenReturn(true);
        when(universityValidationService.isValidUniversity(anyList())).thenReturn(true);

        // Simuliamo che il validatore restituisca una PEC diversa da quella inserita
        when(pivalidator.PIVAlidatePEC(request.getPIVA())).thenReturn("altra.mail@pec.it");

        assertThrows(BadRequestException.class, () -> hostSignUpService.signUp(request));
    }

    // TC10: Università non valida
    @Test
    void signUp_ShouldThrow_WhenUniversityInvalid() {

        HostSignUpRequest request = validRequestBuilder
                .uniList("Università della strada")
                .build();

        when(hostRepository.findByPEC(anyString())).thenReturn(Optional.empty());
        when(geoValidationService.validateGeo(anyString(), anyString())).thenReturn(true);

        // Simuliamo fallimento validazione università
        when(universityValidationService.isValidUniversity(anyList())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> hostSignUpService.signUp(request));
    }

    // TC12, TC14: Geo non valida (Città/Provincia errate)
    @Test
    void signUp_ShouldThrow_WhenGeoInvalid() {

        HostSignUpRequest request = validRequestBuilder
                .city("Mosca") // Città non congruente o estera
                .build();

        when(hostRepository.findByPEC(anyString())).thenReturn(Optional.empty());

        // Simuliamo fallimento validazione geografica
        when(geoValidationService.validateGeo(anyString(), anyString())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> hostSignUpService.signUp(request));
    }

    // Caso di Successo
    @Test
    void signUp_ShouldSucceed_WhenDataCorrect() {

        HostSignUpRequest request = validRequestBuilder.build();

        // Tutti i controlli passano
        when(hostRepository.findByPEC(anyString())).thenReturn(Optional.empty());
        when(geoValidationService.validateGeo(anyString(), anyString())).thenReturn(true);
        when(universityValidationService.isValidUniversity(anyList())).thenReturn(true);
        when(pivalidator.PIVAlidatePEC(anyString())).thenReturn(request.getPEC());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");

        assertDoesNotThrow(() -> hostSignUpService.signUp(request));

        verify(hostRepository, times(1)).save(any(Host.class));
        verify(generate2FAService, times(1)).send2FARequest(anyString());
    }
}