package it.younify.service;

import it.younify.adapter.PIVAlidator.PIVAlidator;
import it.younify.adapter.emailsender.EmailProvider;
import it.younify.model.dto.HostSignUpRequest;
import it.younify.model.entity.Host;
import it.younify.repository.HostRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HostSignUpService {

    private final HostRepository hostRepository;
    private final PasswordEncoder passwordEncoder;
    private final PIVAlidator pivalidator;
    private final Generate2FAService generate2FAService;

    public String signUp(@NotNull HostSignUpRequest hostSignUpRequest) {

        String PEC = hostSignUpRequest.getPEC();
        Optional<Host> alr = hostRepository.findByPEC(PEC);

        if (alr.isPresent()) {
            if(!alr.get().isEnabled()){
                String ID = alr.get().getID();
                generate2FAService.send2FARequest(ID);
                return ID;
            }

            return "PEC already exists and is enabled";
        }
        else{

            String hostName = hostSignUpRequest.getHostName();
            String password = hostSignUpRequest.getPassword();
            String PIVA = hostSignUpRequest.getPIVA();
            String confirmPassword = hostSignUpRequest.getConfirmPassword();
            String province = hostSignUpRequest.getProvince();
            String city = hostSignUpRequest.getCity();
            String uniList = hostSignUpRequest.getUniList();

            if(validatePassword(password,confirmPassword)){

                if(validatePEC(PEC, PIVA)){

                    String ID = "H-" + UUID.randomUUID();
                    password = passwordEncoder.encode(password);
                    Host host = new Host(ID, hostName, PIVA, PEC, password, province, city, uniList, false);
                    hostRepository.save(host);

                    generate2FAService.send2FARequest(ID);
                    return ID;
                }

                return "La PEC " + PEC + " non risulta associata alla P.IVA " + PIVA;
            }

            return "La password inserita non rispetta il pattern di sicurezza richiesto";
        }
    }

    private boolean validatePEC(String PEC, String PIVA){
        return pivalidator.PIVAlidatePEC(PIVA).equals(PEC);
    }

    private boolean validatePassword(String password, String confirmPassword) {

        return password.equals(confirmPassword) &&
                password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$");

    }
}

