package it.younify.modules.sgac.service;

import it.younify.core.exception.ResourceNotFoundException;
import it.younify.modules.sgac.adapter.emailsender.EmailProvider;
import it.younify.core.utils.TwoFactorUtility;
import it.younify.modules.sgac.model.entity.Code2FA;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.Code2FARepository;
import it.younify.modules.sgac.repository.HostRepository;
import it.younify.modules.sgac.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

/**
 * The {@code Generate2FAService} class is responsible for generating and sending two-factor authentication (2FA) codes.
 * This service creates a new 2FA code, persists it to the database, and sends it to the user or host via email.
 *
 * @see it.younify.modules.sgac.repository.Code2FARepository
 * @see it.younify.modules.sgac.adapter.emailsender.EmailProvider
 */
@Service
@RequiredArgsConstructor
public class Generate2FAService {

    private final UserRepository userRepository;
    private final HostRepository hostRepository;
    private final Code2FARepository code2FARepository;
    private final EmailProvider emailProvider;

    @Value("${application.security.2fa.expiration}")
    private int expiration2FA;

    /**
     * Generates and sends a new 2FA code to the email address associated with the given account ID.
     * This method is transactional, ensuring that the code is saved to the database before the email is sent.
     * It determines whether the ID belongs to a {@link User} or a {@link Host} and acts accordingly.
     * <p>
     * Note: A new code is generated and sent, and a corresponding {@link Code2FA} entity is persisted.
     *
     * @param ID of the user or host account.
     * @see it.younify.modules.sgac.model.entity.User
     * @see it.younify.modules.sgac.model.entity.Host
     * @see it.younify.modules.sgac.model.entity.Code2FA
     */
    @Transactional(rollbackFor = Exception.class)
    public Instant send2FARequest(String ID) {

        String code2FA = TwoFactorUtility.generateCode();
        String message = "Your code 2FA is: " + code2FA;

        if (ID.startsWith("U")) {

            Optional<User> optU = userRepository.findById(ID);

            if(optU.isPresent()){

                User user = optU.get();
                if(!user.isEnabled()){

                    code2FARepository.deleteByUser(user);
                    code2FARepository.flush();

                    Instant expiry = Instant.now().plusSeconds(expiration2FA);

                    code2FARepository.save(
                            Code2FA.builder()
                                    .code(code2FA)
                                    .expiryDate(expiry)
                                    .errorCount(0)
                                    .user(user).
                                    build()
                    );

                    emailProvider.send2FAEmail(user.getEmail(), message);
                    return expiry;
                }
                throw new BadCredentialsException("The account ID is already enabled");
            }
        }
        else{

            if(ID.startsWith("H")){

                Optional<Host> optH = hostRepository.findById(ID);

                if(optH.isPresent()){

                    Host host = optH.get();
                    if(!host.isEnabled()){

                        code2FARepository.deleteByHost(host);
                        code2FARepository.flush();

                        Instant expiry = Instant.now().plusSeconds(expiration2FA);

                        code2FARepository.save(
                                Code2FA.builder()
                                        .code(code2FA)
                                        .expiryDate(expiry)
                                        .errorCount(0)
                                        .host(host)
                                        .build()
                        );

                        //in production switch the API for send on Host PEC
                        emailProvider.send2FAEmail("younifytester@gmail.com", message);
                        return expiry;
                    }
                    throw new BadCredentialsException("The account ID is already enabled");
                }
            }
        }

        throw new ResourceNotFoundException("ID account not exist");
    }
}
