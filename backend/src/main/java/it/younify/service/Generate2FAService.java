package it.younify.service;

import it.younify.adapter.emailsender.EmailProvider;
import it.younify.config.TwoFactorUtility;
import it.younify.model.entity.Code2FA;
import it.younify.model.entity.Host;
import it.younify.model.entity.User;
import it.younify.repository.Code2FARepository;
import it.younify.repository.HostRepository;
import it.younify.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Generate2FAService {

    private final UserRepository userRepository;
    private final HostRepository hostRepository;
    private final Code2FARepository code2FARepository;
    private final EmailProvider emailProvider;

    @Value("${application.security.2fa.expiration}")
    private int expiration2FA;

    @Transactional
    public void send2FARequest(String ID){

        String code2FA = TwoFactorUtility.generateCode();
        String message = "Your code 2FA is: " + code2FA;

        if (ID.startsWith("U")) {

            Optional<User> optU = userRepository.findById(ID);
            if(optU.isPresent()) {

                User user = optU.get();
                code2FARepository.save(new Code2FA(user.getID(), code2FA, (Instant.now().plusMillis(expiration2FA)), 0));
                emailProvider.send2FAEmail(user.getEmail(), message);
            }
        }
        else{

            if(ID.startsWith("H")){

                Optional<Host> optH = hostRepository.findById(ID);
                if(optH.isPresent()) {

                    Host host = optH.get();
                    code2FARepository.save(new Code2FA(host.getID(), code2FA, (Instant.now().plusMillis(expiration2FA)), 0));
                    //in production switch the API for send on Host PEC
                    emailProvider.send2FAEmail("younifytester@gmail.com", message);
                }
            }
        }
        //throw exception for id not valid
    }
}
