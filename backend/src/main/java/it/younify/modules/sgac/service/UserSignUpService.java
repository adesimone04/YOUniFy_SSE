package it.younify.modules.sgac.service;

import it.younify.core.exception.BadRequestException;
import it.younify.core.exception.ResourceAlreadyExistsException;
import it.younify.modules.sgac.model.dto.request.UserSignUpRequest;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * The {@code UserSignUpService} class is responsible for handling the sign-up process for users.
 * It validates the user's information, creates a new user account, and triggers the 2FA process.
 *
 * @see it.younify.modules.sgac.repository.UserRepository
 * @see it.younify.modules.sgac.service.Generate2FAService
 */
@Service
@RequiredArgsConstructor
public class UserSignUpService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Generate2FAService generate2FAService;

    /**
     * Registers a new user.
     * <p>
     * This method performs several validation checks:
     * <ul>
     *     <li>Checks if a user with the given email already exists. If so, and the account is not enabled, it re-sends the 2FA code.</li>
     *     <li>Validates the password complexity and confirmation.</li>
     *     <li>Validates that the email address belongs to a recognized Italian university domain.</li>
     * </ul>
     * If all validations pass, it creates a new {@link User} account, saves it to the database,
     * and sends a 2FA code to the user's email.
     *
     * @param userSignUpRequest The request containing the user's sign-up information.
     * @return The ID of the newly created user account.
     * @throws ResourceAlreadyExistsException if a user with the given email already exists and is enabled.
     * @throws BadRequestException if the password or email is invalid.
     */
    public String signUp(@NotNull UserSignUpRequest userSignUpRequest) {

        String email = userSignUpRequest.getEmail();
        Optional<User> opt = userRepository.findByEmail(email);

        if(opt.isPresent()) {

            if(!opt.get().isEnabled()) {

                String ID = opt.get().getID();
                generate2FAService.send2FARequest(ID);
                return ID;
            }

            throw new ResourceAlreadyExistsException("This user already exists. Proceed to login");
        }
        else {

            String firstName = userSignUpRequest.getFirstName();
            String lastName = userSignUpRequest.getLastName();
            String password = userSignUpRequest.getPassword();
            String confirmPassword = userSignUpRequest.getConfirmPassword();
            String cdl = userSignUpRequest.getCdl();
            String university = userSignUpRequest.getUniversity();
            LocalDate dateOfBirth = userSignUpRequest.getDateOfBirth();
            String contactsInfo = userSignUpRequest.getContactsInfo();

            if(validateEmail(email)) {

                if(validatePassword(password, confirmPassword)) {

                    String ID = "U-" + UUID.randomUUID();
                    password = passwordEncoder.encode(password);
                    User user = new User(ID, firstName, lastName, email, password, cdl, university, dateOfBirth, contactsInfo, false);
                    userRepository.save(user);

                    generate2FAService.send2FARequest(ID);
                    return ID;
                }

                throw new BadRequestException("The password must contain at least 8 characters, one uppercase letter, " +
                        "one number, and one special symbol");
            }

            throw new BadRequestException("The email " + email + " does not belong to any Italian university");
        }
    }


    private boolean validateEmail(String email) {

        List<String> emailAccepted = List.of(
                "@studenti.unipd.it", "@polimi.it", "@studenti.unibg.it", "@studenti.uniroma1.it",
                "@edu.unifi.it", "@studio.unibo.it", "@studenti.unimi.it", "@stud.unive.it",
                "@studenti.unito.it", "@studenti.unipi.it", "@studenti.unina.it", "@community.unipa.it",
                "@studium.unict.it", "@studenti.unica.it", "@studenti.unime.it", "@stud.units.it",
                "@spes.uniud.it", "@studenti.uniss.it", "@studenti.univr.it", "@studenti.unipr.it",
                "@edu.unife.it", "@studenti.unige.it", "@universitadipavia.it", "@studenti.unicam.it",
                "@unipg.it", "@uniurb.it", "@unisi.it", "@student.univaq.it", "@studenti.unisa.it",
                "@unifg.it", "@studenti.unisalento.it", "@studenti.unical.it", "@studenti.unibas.it",
                "@studenti.uninsubria.it", "@univda.it", "@unibz.it", "@stud.iuav.it",
                "@studenti.polito.it", "@studenti.poliba.it", "@icatt.it", "@studenti.luiss.it",
                "@studenti.unitelmasapienza.it", "@studbocconi.it"
        );

        return email.matches("^[a-zA-Z0-9._%-]+@[a-zA-Z0-9.]+\\.it$") &&
                emailAccepted.stream().anyMatch(email::endsWith);
    }

    private boolean validatePassword(String password, String confirmPassword) {

        return password.equals(confirmPassword) &&
                password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$");

    }
}
