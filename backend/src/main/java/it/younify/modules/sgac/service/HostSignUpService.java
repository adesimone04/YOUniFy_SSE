package it.younify.modules.sgac.service;

import it.younify.modules.sgac.adapter.PIVAlidator.PIVAlidator;
import it.younify.core.exception.BadRequestException;
import it.younify.core.exception.ResourceAlreadyExistsException;
import it.younify.modules.sgac.model.dto.request.HostSignUpRequest;
import it.younify.modules.sgac.model.dto.response.AccountSummary;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.repository.HostRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * The {@code HostSignUpService} class is responsible for handling the sign-up process for hosts.
 * It validates the host's information, creates a new host account, and triggers the 2FA process.
 *
 * @see it.younify.modules.sgac.repository.HostRepository
 * @see it.younify.modules.sgac.adapter.PIVAlidator.PIVAlidator
 * @see it.younify.modules.sgac.service.Generate2FAService
 */
@Service
@RequiredArgsConstructor
public class HostSignUpService {

    private final HostRepository hostRepository;
    private final PasswordEncoder passwordEncoder;
    private final PIVAlidator pivalidator;
    private final Generate2FAService generate2FAService;

    /**
     * Registers a new host.
     * <p>
     * This method performs several validation checks:
     * <ul>
     *     <li>Checks if a host with the given PEC already exists. If so, and the account is not enabled, it re-sends the 2FA code.</li>
     *     <li>Validates the password complexity and confirmation.</li>
     *     <li>Validates that the provided PEC is associated with the given "Partita IVA" with {@link PIVAlidator}.</li>
     * </ul>
     * If all validations pass, it creates a new {@link Host} account, saves it to the database,
     * and sends a 2FA code to the host's PEC.
     *
     * @param hostSignUpRequest The request containing the host's sign-up information.
     * @return The ID of the newly created host account.
     * @throws ResourceAlreadyExistsException if a host with the given PEC already exists and is enabled.
     * @throws BadRequestException if the password is invalid or the PEC does not match the "Partita IVA".
     */
    @Transactional(rollbackFor = Exception.class)
    public AccountSummary signUp(@NotNull HostSignUpRequest hostSignUpRequest) {

        String hostName = hostSignUpRequest.getHostName();
        String PEC = hostSignUpRequest.getPEC();

        Optional<Host> opt = hostRepository.findByPEC(PEC);

        if (opt.isPresent()) {

            if(!opt.get().isEnabled()){

                String ID = opt.get().getID();
                Instant expiryDate = generate2FAService.send2FARequest(ID);

                return AccountSummary.builder()
                        .ID(ID)
                        .accountName(hostName)
                        .accountEmail(PEC)
                        .expiryDate(expiryDate)
                        .build();
            }

            throw new ResourceAlreadyExistsException("This host already exists. Proceed to login");
        }
        else{

            String PIVA = hostSignUpRequest.getPIVA();
            boolean association = hostSignUpRequest.isAssociation();
            String password = hostSignUpRequest.getPassword();
            String confirmPassword = hostSignUpRequest.getConfirmPassword();
            String province = hostSignUpRequest.getProvince();
            String city = hostSignUpRequest.getCity();
            String uniList = hostSignUpRequest.getUniList();

            if(association || validatePEC(PEC, PIVA)){

                if(PEC.matches("^[a-zA-Z0-9._%+-]+@.*(pec|legal|cert|sicurezza|posta).*\\.[a-zA-Z]{2,}$")) {

                    if(validatePassword(password, confirmPassword)){

                        String ID = "H-" + UUID.randomUUID();
                        password = passwordEncoder.encode(password);
                        Host host = new Host(ID, hostName, PIVA, PEC, password, province, city, uniList, false);
                        hostRepository.save(host);

                        Instant expiryDate = generate2FAService.send2FARequest(ID);

                        return AccountSummary.builder()
                                .ID(ID)
                                .accountName(hostName)
                                .accountEmail(PEC)
                                .expiryDate(expiryDate)
                                .build();
                    }

                    throw new BadRequestException("The password must contain at least 8 characters, one uppercase letter, " +
                            "one number, and one special symbol", "PASSWORD_NOT_SAFE");
                }

                throw new BadRequestException("The PEC address " + PEC + " is not valid", "PEC_ADDRESS_NOT_VALID");
            }

            throw new BadRequestException("The PEC address " + PEC + " is not associated with VAT number " + PIVA, "PEC_ADDRESS_DOESNT_MATCH_VATCODE");
        }
    }


    private boolean validatePEC(String PEC, String PIVA) {
        return pivalidator.PIVAlidatePEC(PIVA).equals(PEC);
    }

    private boolean validatePassword(String password, String confirmPassword) {

        return password.equals(confirmPassword) &&
                password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!.]).{8,}$");

    }
}