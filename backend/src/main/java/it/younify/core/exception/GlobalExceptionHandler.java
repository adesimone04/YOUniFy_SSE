package it.younify.core.exception;

import it.younify.core.model.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * The {@code GlobalExceptionHandler} class is a centralized exception handler for the application.
 * It uses {@link RestControllerAdvice} to catch exceptions thrown from any controller and
 * transform them into a standardized {@link ApiErrorResponse}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link BadRequestException} and returns a 400 Bad Request response.
     * This is typically used for client-side errors like invalid input.
     *
     * @param ex The exception that was thrown.
     * @param request The current HTTP request.
     * @return A {@link ResponseEntity} containing the standardized error response.
     */
    @ExceptionHandler(BadRequestException.class) // (400)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getInternalCode(), request);
    }

    /**
     * Handles authentication-related exceptions like {@link BadCredentialsException},
     * returning a 401 Unauthorized response.
     *
     * @param ex The exception that was thrown.
     * @param request The current HTTP request.
     * @return A {@link ResponseEntity} containing the standardized error response.
     */
    @ExceptionHandler(BadCredentialsException.class) // (401)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), "BAD_CREDENTIALS", request);
    }

    /**
     * Handles authentication-related exceptions like {@link UsernameNotFoundException},
     * returning a 401 Unauthorized response.
     *
     * @param ex The exception that was thrown.
     * @param request The current HTTP request.
     * @return A {@link ResponseEntity} containing the standardized error response.
     */
    @ExceptionHandler(UsernameNotFoundException.class) // (401)
    public ResponseEntity<ApiErrorResponse> handleUsernameNotFound(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), "ACCOUNT_NOT_FOUND", request);
    }

    /**
     * Handles {@link TokenRefreshException} and returns a 403 Forbidden response.
     * This is used when a refresh token is invalid, expired, or revoked.
     *
     * @param ex The exception that was thrown.
     * @param request The current HTTP request.
     * @return A {@link ResponseEntity} containing the standardized error response.
     */
    @ExceptionHandler(TokenRefreshException.class) // (403)
    public ResponseEntity<ApiErrorResponse> handleTokenRefreshException(TokenRefreshException ex, HttpServletRequest request) {

        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), ex.getInternalCode(), request);
    }


    /**
     * Handles {@link AccessDeniedException} when an authenticated user tries to access
     * a resource without sufficient permissions (e.g., failing a {@code @PreAuthorize} check).
     * This ensures the client receives a proper 403 Forbidden status instead of a generic error.
     *
     * @param ex The access denied exception thrown by Spring Security.
     * @param request The current HTTP request.
     * @return A {@link ResponseEntity} containing the standardized 403 error response.
     */
    @ExceptionHandler(AccessDeniedException.class) // (403)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {

        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), "ACCESS_DENIED", request);
    }

    /**
     * Handles {@link ResourceNotFoundException} and returns a 404 Not Found response.
     * This is used when a requested resource does not exist.
     *
     * @param ex The exception that was thrown.
     * @param request The current HTTP request.
     * @return A {@link ResponseEntity} containing the standardized error response.
     */
    @ExceptionHandler(ResourceNotFoundException.class) // (404)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getInternalCode(), request);
    }

    /**
     * Handles {@link ResourceAlreadyExistsException} and returns a 409 Conflict response.
     * This is used when attempting to create a resource that already exists.
     *
     * @param ex The exception that was thrown.
     * @param request The current HTTP request.
     * @return A {@link ResponseEntity} containing the standardized error response.
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class) // (409)
    public ResponseEntity<ApiErrorResponse> handleConflict(ResourceAlreadyExistsException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), ex.getInternalCode(), request);
    }

    /**
     * Handles database integrity violations, such as unique constraint checks.
     * Returns a 409 Conflict status instead of 500, with a readable message.
     *
     * @param ex The DataIntegrityViolationException thrown by Spring Data/Hibernate.
     * @param request The current HTTP request.
     * @return A {@link ResponseEntity} containing the standardized 409 error response.
     */
    @ExceptionHandler(DataIntegrityViolationException.class) // (409)
    public ResponseEntity<ApiErrorResponse> handleDatabaseViolation(DataIntegrityViolationException ex, HttpServletRequest request) {

        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), "DB_CONSTRAINS_VIOLATED", request);
    }

    /**
     * Handles all uncaught {@link Exception} instances as a last resort.
     * This method acts as a generic fallback for any unexpected server-side errors,
     * preventing stack traces from being exposed to the client. It returns a standardized
     * 500 Internal Server Error response.
     *
     * @param ex The exception that was thrown.
     * @param request The current HTTP request.
     * @return A {@link ResponseEntity} containing the standardized 500 error response.
     */
    @ExceptionHandler(Exception.class) // (500)
    public ResponseEntity<ApiErrorResponse> handleInternalServerError(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(),"INTERNAL_SERVER_ERROR", request);
    }


    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, String internalCode, HttpServletRequest request) {

        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .internalCode(internalCode)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, status);
    }
}
