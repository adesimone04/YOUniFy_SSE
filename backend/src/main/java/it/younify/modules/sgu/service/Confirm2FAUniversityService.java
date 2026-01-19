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
import it.younify.modules.sgu.model.dto.request.Confirm2FAUniversity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Confirm2FAUniversityService {

    private final EmailProvider emailProvider;

    private final Code2FARepository code2FARepository;
    private final UserRepository userRepository;

    private final EmailValidationService emailValidationService;
    private final UniversityValidationService universityValidationService;

    @Transactional(rollbackFor = Exception.class, noRollbackFor = BadRequestException.class)
    public void confirm2FA(Confirm2FAUniversity confirm2FAUniversity){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;

        String ID = confirm2FAUniversity.getID();
        if(ID.equals(user.getID())) {

            Optional<Code2FA> opt2FA = code2FARepository.findByOwnerId(ID);
            if (opt2FA.isPresent()) {

                Code2FA code2FA = opt2FA.get();
                if (code2FA.getExpiryDate().isAfter(Instant.now())) {

                    String inserted2FA = confirm2FAUniversity.getCodeInserted();
                    String generated2FA = code2FA.getCode();

                    if (generated2FA.equals(inserted2FA)) {

                        String university = confirm2FAUniversity.getUniversity();
                        String email = confirm2FAUniversity.getEmail();

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
