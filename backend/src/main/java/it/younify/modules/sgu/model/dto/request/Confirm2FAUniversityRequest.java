package it.younify.modules.sgu.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
/**
 * Represents the request body for confirming a university change using a two-factor authentication (2FA) code.
 * This DTO is used to securely transfer the 2FA code and the new university details from the client to the server.
 */
@Data
public class Confirm2FAUniversityRequest {

    /**
     * The unique identifier of the account attempting to authenticate.
     * This field is mandatory.
     */
    @NotBlank
    private String ID;

    /**
     * The 2FA code entered by the user.
     * This field is mandatory.
     */
    @NotBlank
    @Size(min = 6, max = 6)
    private String codeInserted;

    /**
     * The user's email address.
     * This field is mandatory.
     */
    @NotBlank
    @Size(max = 40)
    private String email;

    /**
     * The user's university.
     * This field is mandatory.
     */
    @NotBlank
    @Size(max = 50)
    private String university;
}
