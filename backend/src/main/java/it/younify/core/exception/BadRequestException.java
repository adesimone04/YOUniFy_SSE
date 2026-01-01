package it.younify.core.exception;

/**
 * The {@code BadRequestException} is a runtime exception that is thrown to indicate
 * that a client's request is invalid or malformed.
 * <p>
 * This exception is used to signal a wide range of client-side errors, such as invalid input,
 * failed validation, or incorrect request formatting.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
