package it.younify.modules.sge.model.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.time.Instant;

@Data
public class EventEditRequest {

    private String name;

    private String description;

    private Instant dateandtime;

    private String street;

    private String city;

    private Integer CAP;

    private MultipartFile poster;
}