package it.younify.core.exception;

import it.younify.core.model.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    /**
     * Handles authentication-related exceptions like {@link BadCredentialsException} and
     * {@link UsernameNotFoundException}, returning a 401 Unauthorized response.
     *
     * @param ex The exception that was thrown.
     * @param request The current HTTP request.
     * @return A {@link ResponseEntity} containing the standardized error response.
     */
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class}) // (401)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Email o password non validi", request);
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

        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
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
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
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
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }


    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, HttpServletRequest request) {

        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, status);
    }
}
