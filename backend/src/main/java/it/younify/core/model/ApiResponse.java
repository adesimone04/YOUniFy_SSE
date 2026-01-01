package it.younify.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * The {@code ApiResponse} class is a generic wrapper for API responses.
 * It provides a standardized structure for returning data from API endpoints.
 * <p>
 * This class includes a status, a message, the actual data payload, and a timestamp.
 *
 * @param <T> The type of the data payload.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    /**
     * The status of the API response, defaulting to "200".
     */
    @Builder.Default
    private String status = "200";

    /**
     * A message providing additional information about the response.
     */
    private String message;

    /**
     * The data payload of the response.
     */
    private T data;

    /**
     * The timestamp of when the response was generated.
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
