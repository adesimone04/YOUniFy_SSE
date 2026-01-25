package it.younify.modules.sgac.service;

import it.younify.core.exception.BadRequestException;
import it.younify.modules.sgac.model.dto.request.UserSignUpRequest;
import it.younify.modules.sgac.repository.UserRepository;
import it.younify.modules.sgac.service.validator.CdlValidationService;
import it.younify.modules.sgac.service.validator.EmailValidationService;
import it.younify.modules.sgac.service.validator.UniversityValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSignUpServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private CdlValidationService cdlValidationService;
    @Mock private EmailValidationService emailValidationService;
    @Mock private UniversityValidationService universityValidationService;

    @InjectMocks
    private UserSignUpService userSignUpService;

    private UserSignUpRequest.UserSignUpRequestBuilder validRequestBuilder;

    @BeforeEach
    void setUp() {

        validRequestBuilder = UserSignUpRequest.builder()
                .firstName("Mario")
                .lastName("Rossi")
                .email("mario.rossi@studenti.unisa.it")
                .password("PasswordStud1!")
                .confirmPassword("PasswordStud1!")
                .university("Università degli Studi di Salerno")
                .cdl("L-31: Scienze e tecnologie informatiche")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .contactsInfo("instagram.com/mario_rossi");
    }

    // TC2_UR_SGAC: Email non valida
    @Test
    void signUp_ShouldThrow_WhenEmailInvalid() {

        UserSignUpRequest request = validRequestBuilder
                .email("mario.rossi@gmail.com")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(universityValidationService.isValidUniversity(anyString())).thenReturn(true);
        when(cdlValidationService.isValidCdl(anyString())).thenReturn(true);
        when(emailValidationService.validateEmail(anyString())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> userSignUpService.signUp(request));
    }

    // TC4_UR_SGAC / TC5_UR_SGAC: Password invalida
    @Test
    void signUp_ShouldThrow_WhenPasswordInvalid() {

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(universityValidationService.isValidUniversity(anyString())).thenReturn(true);
        when(cdlValidationService.isValidCdl(anyString())).thenReturn(true);
        when(emailValidationService.validateEmail(anyString())).thenReturn(true);

        // Corta
        UserSignUpRequest shortPass = validRequestBuilder
                .password("Short1!")
                .confirmPassword("Short1!")
                .build();
        assertThrows(BadRequestException.class, () -> userSignUpService.signUp(shortPass));

        // Semplice
        UserSignUpRequest simplePass = validRequestBuilder
                .password("solominuscole123!")
                .confirmPassword("solominuscole123!")
                .build();
        assertThrows(BadRequestException.class, () -> userSignUpService.signUp(simplePass));
    }

    // TC7_UR_SGAC: Università non valida
    @Test
    void signUp_ShouldThrow_WhenUniversityInvalid() {

        UserSignUpRequest request = validRequestBuilder
                .university("Università della strada")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(universityValidationService.isValidUniversity(anyString())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> userSignUpService.signUp(request));
    }

    // TC9_UR_SGAC: Età non valida
    @Test
    void signUp_ShouldThrow_WhenTooYoung() {

        UserSignUpRequest request = validRequestBuilder
                .dateOfBirth(LocalDate.now())
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(universityValidationService.isValidUniversity(anyString())).thenReturn(true);
        when(cdlValidationService.isValidCdl(anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userSignUpService.signUp(request));
    }

    // TC11_UR_SGAC: CDL non valido
    @Test
    void signUp_ShouldThrow_WhenCdlInvalid() {

        UserSignUpRequest request = validRequestBuilder
                .cdl("L-500: Scienze del Fumetto")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(universityValidationService.isValidUniversity(anyString())).thenReturn(true);
        when(cdlValidationService.isValidCdl(anyString())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> userSignUpService.signUp(request));
    }
}