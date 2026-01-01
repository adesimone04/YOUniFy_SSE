package it.younify.core.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * The {@code ApiErrorResponse} class represents a standardized error response for the API.
 * <p>
 * This class is used to provide detailed error information to the client when an exception occurs.
 * It includes a timestamp, HTTP status code, error type, a descriptive message, and the path of the request.
 */
@Data
@Builder
public class ApiErrorResponse {

    /**
     * The timestamp of when the error occurred.
     */
    private LocalDateTime timestamp;

    /**
     * The HTTP status code of the error.
     */
    private int status;

    /**
     * The type of the error (e.g., "Not Found", "Bad Request").
     */
    private String error;

    /**
     * A descriptive message explaining the error.
     */
    private String message;

    /**
     * The path of the request that resulted in the error.
     */
    private String path;
}
