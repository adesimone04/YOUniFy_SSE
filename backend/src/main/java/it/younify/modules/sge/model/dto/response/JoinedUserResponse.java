package it.younify.modules.sge.model.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * The {@code JoinedUserResponse} class is a Data Transfer Object (DTO) that provides a summary
 * of a user who has joined an event.
 * <p>
 * This DTO is used to display a list of participants for an event, showing only public-facing
 * information like their name and profile picture.
 */
@Data
@Builder
public class JoinedUserResponse {

    /**
     * The full name of the user.
     */
    private String fullName;

    /**
     * The URL to the user's profile image.
     */
    private String profileImageUrl;
}
