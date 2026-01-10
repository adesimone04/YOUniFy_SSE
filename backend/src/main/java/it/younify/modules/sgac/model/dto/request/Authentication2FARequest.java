package it.younify.modules.sgac.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The {@code Authentication2FARequest} class is a Data Transfer Object (DTO) used for
 * submitting a two-factor authentication (2FA) code.
 * <p>
 * This DTO is sent by the client when the user needs to provide a 2FA code to complete
 * the authentication process.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Authentication2FARequest {

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
}
