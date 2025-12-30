package it.younify.service;

import it.younify.adapter.PIVAlidator.PIVAlidator;
import it.younify.exception.BadRequestException;
import it.younify.exception.ResourceAlreadyExistsException;
import it.younify.model.dto.request.HostSignUpRequest;
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
        Optional<Host> opt = hostRepository.findByPEC(PEC);

        if (opt.isPresent()) {

            if(!opt.get().isEnabled()){

                String ID = opt.get().getID();
                generate2FAService.send2FARequest(ID);
                return ID;
            }

            throw new ResourceAlreadyExistsException("Email or password incorrect");
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

                throw new BadRequestException("The PEC address " + PEC + " is not associated with VAT number " + PIVA);
            }

            throw new BadRequestException("The password must contain at least 8 characters, one uppercase letter, " +
                    "one number, and one special symbol");
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

