package it.younify.modules.sgac.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * The {@code AuthenticationResponse} class is a Data Transfer Object (DTO) that holds
 * the results of an authentication attempt.
 * <p>
 * This DTO is returned upon successful login or token refresh, containing the necessary tokens
 * and account information for the client to maintain a session.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    /**
     * The JWT access token used for authenticating subsequent requests.
     */
    private String accessToken;

    /**
     * The refresh token used to obtain a new access token when the current one expires.
     */
    private String refreshToken;

    /**
     * The unique identifier of the authenticated account.
     */
    private String accountID;

    /**
     * The status of the authentication attempt (e.g., "LOGIN SUCCESSFUL", "2FA_SENT").
     */
    private String status;

    /**
     * The expiry date of 2FA code returned in case of status "2FA_SENT".
     */
    private Instant expiryDate;
}
