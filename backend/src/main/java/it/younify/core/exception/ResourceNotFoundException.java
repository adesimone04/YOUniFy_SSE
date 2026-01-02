package it.younify.core.exception;

/**
 * The {@code ResourceNotFoundException} is a runtime exception that is thrown when a requested
 * resource cannot be found.
 * <p>
 * This exception is typically used when a database query or an external service call fails to
 * retrieve an expected resource, such as a user, host, or token.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
