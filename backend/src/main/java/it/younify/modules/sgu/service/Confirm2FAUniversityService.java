package it.younify.modules.sgu.service;

import it.younify.core.exception.BadRequestException;
import it.younify.core.exception.ResourceNotFoundException;
import it.younify.modules.sgac.adapter.emailsender.EmailProvider;
import it.younify.modules.sgac.model.entity.Code2FA;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.Code2FARepository;
import it.younify.modules.sgac.repository.UserRepository;
import it.younify.modules.sgac.service.validator.EmailValidationService;
import it.younify.modules.sgac.service.validator.UniversityValidationService;
import it.younify.modules.sgu.model.dto.request.Confirm2FAUniversityRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * This service handles the business logic for confirming a university change using a two-factor authentication (2FA) code.
 * It validates the 2FA code, updates the user's university and email, and handles error cases such as incorrect or expired codes.
 *
 * @see Confirm2FAUniversityRequest
 * @see Code2FARepository
 * @see UserRepository
 * @see EmailProvider
 * @see EmailValidationService
 * @see UniversityValidationService
 */
@Service
@RequiredArgsConstructor
public class Confirm2FAUniversityService {

    private final EmailProvider emailProvider;

    private final Code2FARepository code2FARepository;
    private final UserRepository userRepository;

    private final EmailValidationService emailValidationService;
    private final UniversityValidationService universityValidationService;

    /**
     * Confirms a university change with a 2FA code for the currently authenticated user.
     * This method validates the 2FA code, checks for expiration, and manages retry attempts.
     * If the code is correct, it updates the user's email and university, sends a confirmation email, and deletes the 2FA code.
     * The operation is transactional, but rolls back only for critical exceptions, not for {@link BadRequestException} which is used for flow control.
     *
     * @param confirm2FAUniversityRequest The DTO containing the 2FA code and new university details.
     * @throws BadRequestException if the 2FA code is incorrect, expired, or if the maximum number of attempts is reached. Also thrown for invalid data.
     * @throws ResourceNotFoundException if the 2FA code or user account is not found.
     */
    @Transactional(rollbackFor = Exception.class, noRollbackFor = BadRequestException.class)
    public void confirm2FA(@NonNull Confirm2FAUniversityRequest confirm2FAUniversityRequest){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;

        String ID = confirm2FAUniversityRequest.getID();
        if(ID.equals(user.getID())) {

            Optional<Code2FA> opt2FA = code2FARepository.findByOwnerId(ID);
            if (opt2FA.isPresent()) {

                Code2FA code2FA = opt2FA.get();
                if (code2FA.getExpiryDate().isAfter(Instant.now())) {

                    String inserted2FA = confirm2FAUniversityRequest.getCodeInserted();
                    String generated2FA = code2FA.getCode();

                    if (generated2FA.equals(inserted2FA)) {

                        String university = confirm2FAUniversityRequest.getUniversity();
                        String email = confirm2FAUniversityRequest.getEmail();

                        if(emailValidationService.validateEmail(email) &&
                                universityValidationService.isValidUniversity(university)){

                            user.setEmail(email);
                            user.setUniversity(university);

                            userRepository.save(user);
                            code2FARepository.delete(code2FA);

                            String message = "Nuova università, nuove avventure! \n" + "In bocca al lupo per questo nuovo capitolo su YOUniFy, " + user.getFirstName() + "!";
                            emailProvider.sendWelcomeEmail(user.getEmail(), message);

                            return;
                        }

                        throw new BadRequestException("Some data are not valid", "DATA_NOT_VALID");
                    }
                    else {

                        code2FA.setErrorCount(code2FA.getErrorCount() + 1);
                        if (code2FA.getErrorCount() == 3) {

                            code2FARepository.delete(code2FA);
                            throw new BadRequestException("You achieved the maximum number of attempts: 3", "MAX_ATTEMPTS");
                        }

                        code2FARepository.save(code2FA);
                        throw new BadRequestException("The code is incorrect. Retry, number of attempts remained:" + (3 - code2FA.getErrorCount()), "TRY_2FA_AGAIN");
                    }
                }
                else {

                    code2FARepository.delete(code2FA);
                    throw new BadRequestException("The 2FA code is expired", "2FA_CODE_EXPIRED");
                }
            }

            throw new ResourceNotFoundException("2FA code not found", "ACCOUNT_OR_2FA_NOT_FOUND");
        }

        throw new ResourceNotFoundException("ID account not exist", "ACCOUNT_OR_2FA_NOT_FOUND");
    }
}
