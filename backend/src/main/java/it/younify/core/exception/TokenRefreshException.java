package it.younify.core.exception;

import lombok.Getter;

/**
 * The {@code TokenRefreshException} is a runtime exception that is thrown when an error occurs
 * during the token refresh process.
 * <p>
 * This exception is typically thrown when a refresh token is not found, has been revoked, or is expired.
 *
 * @see it.younify.modules.sgac.service.RefreshTokenService
 */
@Getter
public class TokenRefreshException extends RuntimeException {

    private String internalCode;

    public TokenRefreshException(String message) {
        super(message);
    }

    public TokenRefreshException(String message, String internalCode) {
        super(message);
        this.internalCode = internalCode;
    }
}
