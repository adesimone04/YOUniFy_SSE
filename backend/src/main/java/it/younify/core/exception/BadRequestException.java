package it.younify.core.exception;

import lombok.Getter;

/**
 * The {@code BadRequestException} is a runtime exception that is thrown to indicate
 * that a client's request is invalid or malformed.
 * <p>
 * This exception is used to signal a wide range of client-side errors, such as invalid input,
 * failed validation, or incorrect request formatting.
 */
@Getter
public class BadRequestException extends RuntimeException {

    private String internalCode;

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, String internalCode){
        super(message);
        this.internalCode = internalCode;
    }
}
