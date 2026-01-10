package it.younify.core.exception;

import lombok.Getter;

/**
 * The {@code ResourceAlreadyExistsException} is a runtime exception that is thrown when an attempt
 * is made to create a resource that already exists.
 * <p>
 * This exception is commonly used in sign-up processes to indicate that an account with the
 * given email or other unique identifier already exists.
 *
 * @see it.younify.modules.sgac.service.UserSignUpService
 * @see it.younify.modules.sgac.service.HostSignUpService
 */
@Getter
public class ResourceAlreadyExistsException extends RuntimeException {

    private String internalCode;

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    public ResourceAlreadyExistsException(String message, String internalCode){
        super(message);
        this.internalCode = internalCode;
    }
}
