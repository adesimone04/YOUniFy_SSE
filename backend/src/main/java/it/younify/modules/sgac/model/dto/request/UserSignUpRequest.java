package it.younify.modules.sgac.model.dto.request;

import lombok.Data;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;


/**
 * The {@code UserSignUpRequest} class is a Data Transfer Object (DTO) used for
 * capturing user information during the sign-up process.
 * <p>
 * This DTO contains all the necessary fields for creating a new user account.
 */
@Data
public class UserSignUpRequest {

    /**
     * The user's first name.
     * This field is mandatory.
     */
    @NotBlank
    private String firstName;

    /**
     * The user's last name.
     * This field is mandatory.
     */
    @NotBlank
    private String lastName;

    /**
     * The user's email address.
     * This field is mandatory.
     */
    @NotBlank
    private String email;

    /**
     * The user's chosen password.
     * This field is mandatory.
     */
    @NotBlank
    private String password;

    /**
     * The confirmation of the user's password.
     * This should match the {@code password} field.
     * This field is mandatory.
     */
    @NotBlank
    private String confirmPassword;

    /**
     * The user's course of study (Corso di Laurea).
     * This field is mandatory.
     */
    @NotBlank
    private String cdl;

    /**
     * The user's university.
     * This field is mandatory.
     */
    @NotBlank
    private String university;

    /**
     * The user's date of birth.
     */
    private LocalDate dateOfBirth;

    /**
     * The user's contact information.
     * This field is mandatory.
     */
    @NotBlank
    private String contactsInfo;
}
