package it.younify.modules.sge.model.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@Data
public class EventPostRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Instant dateandtime;

    @NotBlank
    private String street;

    @NotBlank
    private String city;

    @NotNull
    private int CAP;

    @NotNull(message = "L'immagine del poster è obbligatoria")
    private MultipartFile poster;
}
