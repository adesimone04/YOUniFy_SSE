package it.younify.modules.sgu.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Represents the request body for changing a user's password.
 * This DTO is used to securely transfer the new password and its confirmation from the client to the server.
 */
@Data
public class ChangePassRequest {

    /**
     * The new password chosen by the user.
     * It must be at least 8 characters long.
     */
    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    /**
     * A confirmation of the new password to prevent typos.
     * This field must exactly match the {@code password} field.
     */
    @NotBlank
    @Size(min = 8, max = 100)
    private String confirmPassword;
}
