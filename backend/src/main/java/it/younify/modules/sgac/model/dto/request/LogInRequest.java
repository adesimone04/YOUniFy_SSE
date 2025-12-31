package it.younify.modules.sgac.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * The {@code LogInRequest} class is a Data Transfer Object (DTO) used for
 * submitting user credentials during a login attempt.
 * <p>
 * This DTO captures the email and password provided by the user.
 */
@Data
public class LogInRequest {

    /**
     * The user's email/PEC address.
     * This field is mandatory.
     */
    @NotBlank
    String email;

    /**
     * The user's password.
     * This field is mandatory.
     */
    @NotBlank
    String password;
}
