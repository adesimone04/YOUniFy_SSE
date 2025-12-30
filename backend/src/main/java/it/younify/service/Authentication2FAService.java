package it.younify.service;

import it.younify.adapter.emailsender.EmailProvider;
import it.younify.model.dto.request.Authentication2FARequest;
import it.younify.model.dto.response.AccountSummary;
import it.younify.model.entity.Code2FA;
import it.younify.model.entity.Host;
import it.younify.model.entity.User;
import it.younify.repository.Code2FARepository;
import it.younify.repository.HostRepository;
import it.younify.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import it.younify.exception.BadRequestException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Authentication2FAService {

    private final UserRepository userRepository;
    private final HostRepository hostRepository;
    private final Code2FARepository code2FARepository;
    private final EmailProvider emailProvider;
    private final Generate2FAService generate2FAService;

    public AccountSummary confirm2FA(@NotNull Authentication2FARequest authentication2FARequest) {

        String ID = authentication2FARequest.getID();

        Optional<Code2FA> opt2FA = code2FARepository.findById(ID);
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
                        generate2FAService.send2FARequest(ID);

                        throw new BadRequestException("You achieved the maximum number of attempts: 3");
                    }

                    throw new BadRequestException("The code is incorrect. Retry, number of attempts remained:" + (3 - code2FA.getErrorCount()));
                }
            }
            else {

                code2FARepository.delete(code2FA);
                generate2FAService.send2FARequest(ID);
                throw new BadRequestException("The 2FA code is expired");
            }
        }

        throw new BadCredentialsException("ID account not exist");
    }
}
