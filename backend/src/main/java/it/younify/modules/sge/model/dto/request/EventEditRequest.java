package it.younify.modules.sge.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.time.Instant;

@Data
public class EventEditRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Instant dateAndTime;

    @NotBlank
    private String street;

    @NotBlank
    private String city;

    @NotBlank
    private String CAP;

    private MultipartFile poster;
}