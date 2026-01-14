package it.younify.modules.sge.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

/**
 * The {@code EventPostRequest} class is a Data Transfer Object (DTO) used for creating a new event.
 * It encapsulates all the necessary information, including event details and the promotional poster image,
 * submitted as part of a multipart form data request.
 */
@Data
public class EventPostRequest {

    /**
     * The name or title of the event.
     */
    @NotBlank
    private String name;

    /**
     * A detailed description of the event.
     */
    @NotBlank
    private String description;

    /**
     * The date and time when the event takes place.
     */
    @NotNull
    private Instant dateAndTime;

    /**
     * The street address where the event is located.
     */
    @NotBlank
    private String street;

    /**
     * The city where the event is located.
     */
    @NotBlank
    private String city;

    /**
     * The postal code (CAP) of the event's location.
     */
    @NotBlank
    private String CAP;

    /**
     * The event's promotional poster, submitted as an image file.
     */
    @NotNull
    private MultipartFile poster;
}
