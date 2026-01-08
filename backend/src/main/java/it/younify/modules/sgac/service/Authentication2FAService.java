package it.younify.modules.sgac.service;

import it.younify.core.exception.ResourceNotFoundException;
import it.younify.modules.sgac.adapter.emailsender.EmailProvider;
import it.younify.modules.sgac.model.dto.request.Authentication2FARequest;
import it.younify.modules.sgac.model.dto.response.AccountSummary;
import it.younify.modules.sgac.model.entity.Code2FA;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.Code2FARepository;
import it.younify.modules.sgac.repository.HostRepository;
import it.younify.modules.sgac.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import it.younify.core.exception.BadRequestException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

/**
 * The {@code Authentication2FAService} class handles the logic for two-factor authentication (2FA).
 * This service is responsible for confirming the 2FA code provided by the user after the initial login.
 * It works for both {@link User} and {@link Host} account types.
 *
 * @see it.younify.modules.sgac.repository.Code2FARepository
 * @see it.younify.modules.sgac.service.Generate2FAService
 * @see it.younify.modules.sgac.adapter.emailsender.EmailProvider
 */
@Service
@RequiredArgsConstructor
public class Authentication2FAService {

    private final UserRepository userRepository;
    private final HostRepository hostRepository;
    private final Code2FARepository code2FARepository;
    private final EmailProvider emailProvider;

    /**
     * Confirms the two-factor authentication code for a user or host.
     * This method validates the provided 2FA code. If the code is correct, the account is enabled,
     * and a welcome email is sent. If the code is incorrect or expired, it manages the number of attempts
     * and may trigger a new code to be sent.
     *
     * @param authentication2FARequest The request containing the account ID and the 2FA code.
     * @return An {@link AccountSummary} containing the account name and email upon successful confirmation.
     * @throws BadCredentialsException If the account ID does not exist.
     * @throws BadRequestException If the 2FA code is expired, incorrect, or the maximum number of attempts has been reached.
     * @see it.younify.modules.sgac.model.dto.request.Authentication2FARequest
     * @see it.younify.modules.sgac.model.dto.response.AccountSummary
     */
    public AccountSummary confirm2FA(@NotNull Authentication2FARequest authentication2FARequest) {

        String ID = authentication2FARequest.getID();

        Optional<Code2FA> opt2FA = code2FARepository.findByOwnerId(ID);
        if (opt2FA.isPresent()) {

            Code2FA code2FA = opt2FA.get();
            if (code2FA.getExpiryDate().isAfter(Instant.now())) {

                String inserted2FA = authentication2FARequest.getCodeInserted();
                String generated2FA = code2FA.getCode();
                if (generated2FA.equals(inserted2FA)) {

                    if (ID.startsWith("U")) {

                        Optional<User> optU = userRepository.findById(ID);
                        if (optU.isPresent()) {

                            User user = optU.get();
                            user.setEnabled(true);
                            userRepository.save(user);

                            code2FARepository.delete(code2FA);

                            String message = "Benvenuto " + user.getFirstName() + " nella community di YOUniFy";
                            emailProvider.sendWelcomeEmail(user.getEmail(), message);

                              return AccountSummary.builder()
                                    .accountName(user.getFirstName())
                                    .accountEmail(user.getEmail())
                                    .build();
                        }
                    }
                    else {

                        Optional<Host> optH = hostRepository.findById(ID);
                        if (optH.isPresent()) {

                            Host host = optH.get();
                            host.setEnabled(true);
                            hostRepository.save(host);

                            code2FARepository.delete(code2FA);

                            String message = "Siamo felici di lavorare con te " + host.getHostName();

                            //in production switch the API for send on Host PEC
                            emailProvider.sendWelcomeEmail("younifytester@gmail.com", message);

                             return AccountSummary.builder()
                                    .accountName(host.getHostName())
                                    .accountEmail(host.getPEC())
                                    .build();
                        }
                    }
                }
                else {

                    code2FA.setErrorCount(code2FA.getErrorCount() + 1);
                    if (code2FA.getErrorCount() == 3) {

                        code2FARepository.delete(code2FA);
                        throw new BadRequestException("You achieved the maximum number of attempts: 3");
                    }

                    throw new BadRequestException("The code is incorrect. Retry, number of attempts remained:" + (3 - code2FA.getErrorCount()));
                }
            }
            else {

                code2FARepository.delete(code2FA);
                throw new BadRequestException("The 2FA code is expired");
            }
        }

        throw new ResourceNotFoundException("ID account not exist or 2FA code not found");
    }
}
