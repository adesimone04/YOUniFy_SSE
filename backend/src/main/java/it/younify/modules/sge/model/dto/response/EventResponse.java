package it.younify.modules.sge.model.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class EventResponse {

    private Long ID;

    private String name;

    private String description;

    private Instant dateAndTime;

    private String street;

    private String city;

    private String CAP;

    private String posterUrl;

    private String hostName;

    private List<JoinedUserResponse> joinedUsers;
}