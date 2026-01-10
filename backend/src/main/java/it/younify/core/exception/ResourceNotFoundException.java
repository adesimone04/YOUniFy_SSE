package it.younify.core.exception;

import lombok.Getter;

/**
 * The {@code ResourceNotFoundException} is a runtime exception that is thrown when a requested
 * resource cannot be found.
 * <p>
 * This exception is typically used when a database query or an external service call fails to
 * retrieve an expected resource, such as a user, host, or token.
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {

    private String internalCode;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, String internalCode) {
        super(message);
        this.internalCode = internalCode;
    }
}
