package it.younify.modules.sgac.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * The {@code RefreshTokenRequest} class is a Data Transfer Object (DTO) used for
 * requesting a new access token.
 * <p>
 * This DTO contains the refresh token that authorizes the issuance of a new access token.
 */
@Data
public class RefreshTokenRequest {

    /**
     * The refresh token.
     * This field is mandatory.
     */
    @NotBlank
    String refreshToken;
}
