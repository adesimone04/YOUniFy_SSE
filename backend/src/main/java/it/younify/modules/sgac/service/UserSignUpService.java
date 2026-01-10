package it.younify.modules.sgac.service;

import it.younify.core.exception.BadRequestException;
import it.younify.core.exception.ResourceAlreadyExistsException;
import it.younify.modules.sgac.model.dto.request.UserSignUpRequest;
import it.younify.modules.sgac.model.dto.response.AccountSummary;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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

    private static final List<String> DOMAINS_ACCEPTED = List.of(

            "studenti.univpm.it", "univda.it", "studenti.uniba.it", "studenti.poliba.it",
            "studenti.lum.it", "studenti.unisannio.it", "studenti.unibg.it", "studio.unibo.it",
            "student.unibz.it", "unibz.it", "studenti.unibs.it", "studenti.unica.it",
            "studenti.unicam.it", "studenti.unimol.it", "studenti.unicas.it", "liuc.it",
            "studenti.liuc.it", "studium.unict.it", "studenti.unicz.it", "studenti.unich.it",
            "studenti.uninsubria.it", "studenti.unical.it", "unikorestudent.it", "edu.unife.it",
            "edu.unifi.it", "unifg.it", "studenti.unifg.it", "studenti.unige.it",
            "student.univaq.it", "gssi.it", "studenti.unisalento.it", "studenti.unimc.it",
            "studenti.unime.it", "studenti.unimi.it", "campus.unimib.it", "mail.polimi.it",
            "studbocconi.it", "icatt.it", "studenti.iulm.it", "studenti.unisr.it",
            "st.hunimed.eu", "studenti.unimore.it", "studenti.unina.it", "studenti.unicampania.it",
            "studenti.unior.it", "studenti.uniparthenope.it", "studenti.unisob.na.it", "studenti.unipd.it",
            "community.unipa.it", "studenti.unipr.it", "universitadipavia.it", "iusspavia.it",
            "studenti.unipg.it", "unistrapg.it", "studenti.unipi.it", "sns.it",
            "santannapisa.it", "studenti.unibas.it", "studenti.unirc.it", "unistrada.it",
            "studenti.uniroma1.it", "students.uniroma2.eu", "stud.uniroma3.it", "studenti.uniroma4.it",
            "studenti.luiss.it", "lumsastud.it", "unier.it", "alumni.unicampus.it",
            "unilink.it", "unint.eu", "studenti.unisa.it", "studenti.uniss.it",
            "student.unisi.it", "unistrasi.it", "studenti.unite.it", "studenti.unito.it",
            "edu.unito.it", "studenti.polito.it", "studenti.unitn.it", "stud.units.it",
            "sissa.it", "spes.uniud.it", "campus.uniurb.it", "stud.unive.it",
            "stud.iuav.it", "studenti.univr.it", "studenti.unitus.it"
    );


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
    @Transactional(rollbackFor = Exception.class)
    public AccountSummary signUp(@NotNull UserSignUpRequest userSignUpRequest) {

        String firstName = userSignUpRequest.getFirstName();
        String email = userSignUpRequest.getEmail();

        Optional<User> opt = userRepository.findByEmail(email);

        if(opt.isPresent()) {

            if(!opt.get().isEnabled()) {

                String ID = opt.get().getID();
                Instant expiryDate = generate2FAService.send2FARequest(ID);

                return AccountSummary.builder()
                        .ID(ID)
                        .accountName(firstName)
                        .accountEmail(email)
                        .expiryDate(expiryDate)
                        .build();
            }

            throw new ResourceAlreadyExistsException("This user already exists. Proceed to login", "ALREADY_REGISTERED");
        }
        else {

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

                    Instant expiryDate = generate2FAService.send2FARequest(ID);

                    return AccountSummary.builder()
                            .ID(ID)
                            .accountName(firstName)
                            .accountEmail(email)
                            .expiryDate(expiryDate)
                            .build();
                }

                throw new BadRequestException("The password must contain at least 8 characters, one uppercase letter, " +
                        "one number, and one special symbol", "PASSWORD_NOT_SAFE");
            }

            throw new BadRequestException("The email " + email + " does not belong to any Italian university", "EMAIL_NOT_VALID");
        }
    }


    private boolean validateEmail(String email) {

        return email.matches("^[a-zA-Z0-9._%-]+@[a-zA-Z0-9.]+\\.it$") &&
                DOMAINS_ACCEPTED.stream().anyMatch(email::endsWith);
        }

    private boolean validatePassword(String password, String confirmPassword) {

        return password.equals(confirmPassword) &&
                password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!.]).{8,}$");

    }
}
