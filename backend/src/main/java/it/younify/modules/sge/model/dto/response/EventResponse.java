package it.younify.modules.sge.model.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class EventResponse {
    private Long id;
    private String name;
    private String description;
    private Instant dateandtime;
    private String street;
    private String city;
    private Integer CAP;

    private String posterUrl;

    private String hostName;
}