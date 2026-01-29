package it.younify.modules.sge.model.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.List;

/**
 * The {@code EventResponse} class is a Data Transfer Object (DTO) that represents an event to be sent to the client.
 * It provides a client-friendly representation of an {@link it.younify.modules.sge.model.entity.Event} entity,
 * including details about the host and a list of users who have joined.
 */
@Data
@Builder
public class EventResponse {

    /**
     * The unique identifier for the event.
     */
    private Long ID;

    /**
     * The name or title of the event.
     */
    private String name;

    /**
     * A detailed description of the event.
     */
    private String description;

    /**
     * The date and time when the event takes place.
     */
    private Instant dateAndTime;

    /**
     * The street address where the event is located.
     */
    private String street;

    /**
     * The city where the event is located.
     */
    private String city;

    /**
     * The postal code (CAP) of the event's location.
     */
    private String CAP;

    /**
     * The URL to the event's promotional poster image.
     */
    private String posterUrl;

    /**
     * The name of the host organization responsible for the event.
     */
    private String hostName;

    /**
     * Indicates whether the currently authenticated user has joined this event.
     * This field is typically populated based on the user's participation status.
     */
    private boolean joined;

    /**
     * A list of users who have joined the event.
     */
    private List<JoinedUserResponse> joinedUsers;
}