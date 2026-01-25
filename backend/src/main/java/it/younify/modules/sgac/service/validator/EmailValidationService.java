package it.younify.modules.sgac.service.validator;

import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * The {@code EmailValidationService} is responsible for validating user email addresses.
 * It specifically checks if an email belongs to an accepted list of Italian university domains,
 * ensuring that only students from supported institutions can register.
 */
@Service
public class EmailValidationService {

    /**
     * A set of accepted email domains for Italian universities.
     * Only emails with a domain present in this set will be considered valid.
     */
    private static final Set<String> DOMAINS_ACCEPTED = Set.of(

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
     * Validates an email address to ensure it belongs to an accepted Italian university domain.
     * The validation checks two conditions:
     * 1. The email format is valid and ends with a {@code .it} top-level domain.
     * 2. The domain part of the email is present in the {@code DOMAINS_ACCEPTED} set.
     * The domain check is case-insensitive.
     *
     * @param email The email address to validate.
     * @return {@code true} if the email is a valid university email, {@code false} otherwise.
     */
    public boolean validateEmail(String email) {

        return email.matches("^[a-zA-Z0-9._%-]+@[a-zA-Z0-9.]+\\.it$") &&
                DOMAINS_ACCEPTED.contains(email.substring(email.indexOf("@") + 1).toLowerCase());
    }
}
