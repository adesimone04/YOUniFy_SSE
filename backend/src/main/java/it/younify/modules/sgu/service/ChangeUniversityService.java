package it.younify.modules.sgu.service;

import it.younify.core.exception.BadRequestException;
import it.younify.core.utils.TwoFactorUtility;
import it.younify.modules.sgac.adapter.emailsender.EmailProvider;
import it.younify.modules.sgac.model.dto.response.AccountSummary;
import it.younify.modules.sgac.model.entity.Code2FA;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.Code2FARepository;
import it.younify.modules.sgac.repository.UserRepository;
import it.younify.modules.sgac.service.Generate2FAService;
import it.younify.modules.sgac.service.validator.CdlValidationService;
import it.younify.modules.sgac.service.validator.EmailValidationService;
import it.younify.modules.sgac.service.validator.UniversityValidationService;
import it.younify.modules.sgu.model.dto.request.EditUniversityRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ChangeUniversityService {

    private final UserRepository userRepository;
    private final Code2FARepository code2FARepository;
    private final EmailProvider emailProvider;

    private final CdlValidationService cdlValidationService;
    private final UniversityValidationService universityValidationService;
    private final EmailValidationService emailValidationService;

    @Value("${application.security.2fa.expiration}")
    private int expiration2FA;

    @Transactional(rollbackFor = Exception.class)
    public AccountSummary changeUniversity(EditUniversityRequest editUniversityRequest, User user){

        String email  = editUniversityRequest.getEmail();
        String university =  editUniversityRequest.getUniversity();
        String cdl = editUniversityRequest.getCdl();

        if(validateData(email, university, cdl)){

            if(!email.equals(user.getEmail()) || !university.equals(user.getUniversity())){

                Instant expiryDate = send2FAChangeUniversity(user, email);

                return AccountSummary.builder()
                        .ID(user.getID())
                        .accountName(user.getFirstName())
                        .accountEmail(email)
                        .expiryDate(expiryDate)
                        .university(university)
                        .cdl(cdl)
                        .build();
            }

            user.setCdl(cdl);
            userRepository.save(user);
        }
        else throw new BadRequestException("Some data are not valid", "DATA_NOT_VALID");

        return AccountSummary.builder()
                .expiryDate(null)
                .build();
    }


    private boolean validateData(String email, String university, String cdl){

        return emailValidationService.validateEmail(email) &&
                cdlValidationService.isValidCdl(cdl) &&
                universityValidationService.isValidUniversity(university);

    }

    private Instant send2FAChangeUniversity(User user, String email){

        String code2FA = TwoFactorUtility.generateCode();
        String message = "Your code 2FA is: " + code2FA;

        code2FARepository.deleteByUser(user);
        code2FARepository.flush();

        emailProvider.send2FAEmail(email, message);
        Instant expiryDate = Instant.now().plusSeconds(expiration2FA);

        code2FARepository.save(
                Code2FA.builder()
                        .code(code2FA)
                        .expiryDate(expiryDate)
                        .errorCount(0)
                        .user(user).
                        build()
        );

        return expiryDate;
    }
}
