package it.younify.modules.sge.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.time.Instant;

/**
 * The {@code EventEditRequest} class is a Data Transfer Object (DTO) used for editing an existing event.
 * It allows for updating event details. The promotional poster is optional; if a new file is not provided,
 * the existing poster will be retained.
 */
@Data
public class EventEditRequest {

    /**
     * The updated name or title of the event.
     */
    @NotBlank
    @Size(max = 100)
    private String name;

    /**
     * The updated detailed description of the event.
     */
    @NotBlank
    @Size(max = 200)
    private String description;

    /**
     * The updated date and time when the event takes place.
     */
    @NotNull
    @Future
    private Instant dateAndTime;

    /**
     * The updated street address where the event is located.
     */
    @NotBlank
    @Size(max = 60)
    private String street;

    /**
     * The updated city where the event is located.
     */
    @NotBlank
    @Size(max = 30)
    private String city;

    /**
     * The updated postal code (CAP) of the event's location.
     */
    @NotBlank
    @Pattern(regexp = "\\d{5}")
    private String CAP;

    /**
     * The new promotional poster for the event.
     * This is optional; if left null, the current poster will not be changed.
     */
    private MultipartFile poster;
}