package it.younify.modules.sgac.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;


/**
 * The {@code UserSignUpRequest} class is a Data Transfer Object (DTO) used for
 * capturing user information during the sign-up process.
 * <p>
 * This DTO contains all the necessary fields for creating a new user account.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpRequest {

    /**
     * The user's first name.
     * This field is mandatory.
     */
    @NotBlank
    @Size(min = 3, max = 30)
    private String firstName;

    /**
     * The user's last name.
     * This field is mandatory.
     */
    @NotBlank
    @Size(min = 3, max = 30)
    private String lastName;

    /**
     * The user's email address.
     * This field is mandatory.
     */
    @NotBlank
    @Size(max = 40)
    private String email;

    /**
     * The user's chosen password.
     * This field is mandatory.
     */
    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    /**
     * The confirmation of the user's password.
     * This should match the {@code password} field.
     * This field is mandatory.
     */
    @NotBlank
    @Size(min = 8, max = 100)
    private String confirmPassword;

    /**
     * The user's course of study (Corso di Laurea).
     * This field is mandatory.
     */
    @NotBlank
    @Size(max = 100)
    private String cdl;

    /**
     * The user's university.
     * This field is mandatory.
     */
    @NotBlank
    @Size(max = 50)
    private String university;

    /**
     * The user's date of birth.
     */
    @NotNull
    private LocalDate dateOfBirth;

    /**
     * The user's contact information.
     * This field is mandatory.
     */
    @NotBlank
    @Size(max = 50)
    private String contactsInfo;
}
