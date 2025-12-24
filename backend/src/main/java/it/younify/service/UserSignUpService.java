package it.younify.service;

import it.younify.model.dto.UserSignUpRequest;
import it.younify.model.entity.User;
import it.younify.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSignUpService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String signUp(@NotNull UserSignUpRequest userSignUpRequest) {

        String email = userSignUpRequest.getEmail();
        Optional<User> opt = userRepository.findByEmail(email);

        if(opt.isPresent()) {

            return "L'email inserita risulta già registrata";
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

            if(validatePassword(password, confirmPassword)) {

                if(validateEmail(email)) {

                    String ID = "U-" + UUID.randomUUID();
                    password = passwordEncoder.encode(password);
                    User user = new User(ID, firstName, lastName, email, password, cdl, university, dateOfBirth, contactsInfo);

                    //apply 2FA with design pattern before save in DB
                    userRepository.save(user);

                    return "Benvenuto " + firstName + " nella community di YOUniFy";
                }

                return "L'email: " + email + "non appartiene a nessuna istituzione universitaria italiana";
            }

            return "La password inserita non rispetta il pattern di sicurezza richiesto";
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

        return email.matches("^[a-zA-Z0-9._%-]+@studenti\\.[a-zA-Z0-9-]+\\.it$") &&
                emailAccepted.stream().anyMatch(email::endsWith);
    }

    private boolean validatePassword(String password, String confirmPassword) {

        return password.equals(confirmPassword) &&
                password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$");

    }
}
