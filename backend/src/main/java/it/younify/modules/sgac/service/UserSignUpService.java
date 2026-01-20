package it.younify.modules.sgac.service;

import it.younify.core.exception.BadRequestException;
import it.younify.core.exception.ResourceAlreadyExistsException;
import it.younify.modules.sgac.model.dto.request.UserSignUpRequest;
import it.younify.modules.sgac.model.dto.response.AccountSummary;
import it.younify.modules.sgac.model.entity.Role;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.UserRepository;
import it.younify.modules.sgac.service.validator.CdlValidationService;
import it.younify.modules.sgac.service.validator.EmailValidationService;
import it.younify.modules.sgac.service.validator.UniversityValidationService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

/**
 * The {@code UserSignUpService} class is responsible for handling the sign-up process for users.
 * It validates the user's information, creates a new user account, and triggers the 2FA process.
 *
 * @see it.younify.modules.sgac.repository.UserRepository
 * @see it.younify.modules.sgac.service.Generate2FAService
 */
@Service
@RequiredArgsConstructor
public class UserSignUpService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final Generate2FAService generate2FAService;
    private final CdlValidationService cdlValidationService;
    private final EmailValidationService emailValidationService;
    private final UniversityValidationService universityValidationService;

    /**
     * Registers a new user.
     * <p>
     * This method performs several validation checks:
     * <ul>
     *     <li>Checks if a user with the given email already exists. If so, and the account is not enabled, it re-sends the 2FA code.</li>
     *     <li>Validates the password complexity and confirmation.</li>
     *     <li>Validates that the email address belongs to a recognized Italian university domain.</li>
     * </ul>
     * If all validations pass, it creates a new {@link User} account, saves it to the database,
     * and sends a 2FA code to the user's email.
     *
     * @param userSignUpRequest The request containing the user's sign-up information.
     * @return The ID of the newly created user account.
     * @throws ResourceAlreadyExistsException if a user with the given email already exists and is enabled.
     * @throws BadRequestException if the password or email is invalid.
     */
    @Transactional(rollbackFor = Exception.class)
    public AccountSummary signUp(@NotNull UserSignUpRequest userSignUpRequest) {

        String firstName = userSignUpRequest.getFirstName();
        String email = userSignUpRequest.getEmail();

        Optional<User> opt = userRepository.findByEmail(email);

        if(opt.isPresent()) {

            if(!opt.get().isEnabled()) {

                String ID = opt.get().getID();
                Instant expiryDate = generate2FAService.send2FARequest(ID);

                return AccountSummary.builder()
                        .ID(ID)
                        .accountName(firstName)
                        .accountEmail(email)
                        .expiryDate(expiryDate)
                        .build();
            }

            throw new ResourceAlreadyExistsException("This user already exists. Proceed to login", "ALREADY_REGISTERED");
        }
        else {

            String lastName = userSignUpRequest.getLastName();
            String password = userSignUpRequest.getPassword();
            String confirmPassword = userSignUpRequest.getConfirmPassword();
            String noProfileImage = "system/nopicture.webp";
            String cdl = userSignUpRequest.getCdl();
            String university = userSignUpRequest.getUniversity();
            LocalDate dateOfBirth = userSignUpRequest.getDateOfBirth();
            String contactsInfo = userSignUpRequest.getContactsInfo();

            if(validateData(university, cdl, dateOfBirth)){

                if(emailValidationService.validateEmail(email)) {

                    if(validatePassword(password, confirmPassword)) {

                        String ID = "U-" + UUID.randomUUID();
                        password = passwordEncoder.encode(password);

                        User user = User.builder()
                                .ID(ID)
                                .firstName(firstName)
                                .lastName(lastName)
                                .email(email)
                                .password(password)
                                .profileImage(noProfileImage)
                                .cdl(cdl)
                                .university(university)
                                .dateOfBirth(dateOfBirth)
                                .contactsInfo(contactsInfo)
                                .enabled(false)
                                .role(Role.USER)
                                .build();

                        userRepository.save(user);

                        Instant expiryDate = generate2FAService.send2FARequest(ID);

                        return AccountSummary.builder()
                                .ID(ID)
                                .accountName(firstName)
                                .accountEmail(email)
                                .expiryDate(expiryDate)
                                .build();
                    }

                    throw new BadRequestException("The password must contain at least 8 characters, one uppercase letter, " +
                            "one number, and one special symbol", "PASSWORD_NOT_SAFE");
                }

                throw new BadRequestException("The email " + email + " does not belong to any Italian university", "EMAIL_NOT_VALID");
            }

            throw new BadRequestException("Some data are not valid", "DATA_NOT_VALID");
        }
    }


    private boolean validateData(String university, String cdl, LocalDate dateOfBirth) {

        LocalDate today = LocalDate.now();

        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();
        int academicStartYear = (currentMonth < 9) ? currentYear - 1 : currentYear;
        int maxBirthYear = academicStartYear - 18;

        LocalDate cutoffDate = LocalDate.of(maxBirthYear, 4, 30);

        return universityValidationService.isValidUniversity(university) &&
                cdlValidationService.isValidCdl(cdl) &&
                dateOfBirth.isBefore(cutoffDate);
    }


    private boolean validatePassword(String password, String confirmPassword) {

        return password.equals(confirmPassword) &&
                password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!.]).{8,}$");

    }
}
