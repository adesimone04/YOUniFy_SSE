package it.younify.core.exception;

/**
 * The {@code TokenRefreshException} is a runtime exception that is thrown when an error occurs
 * during the token refresh process.
 * <p>
 * This exception is typically thrown when a refresh token is not found, has been revoked, or is expired.
 *
 * @see it.younify.modules.sgac.service.RefreshTokenService
 */
public class TokenRefreshException extends RuntimeException {

    public TokenRefreshException(String message) {
        super(message);
    }
}
