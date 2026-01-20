package it.younify.modules.sgm.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the data for a user's card in the matching feature.
 * This DTO is used to transfer a summary of a user's profile to the client for display in the matching interface.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchCardResponse {

    /**
     * The unique identifier of the user.
     */
    private String userID;

    /**
     * The URL of the user's profile photo.
     */
    private String userPhoto;

    /**
     * The full name of the user.
     */
    private String userName;

    /**
     * The university the user attends.
     */
    private String userUniversity;

    /**
     * The user's course of study (Corso di Laurea).
     */
    private String userCdl;

    /**
     * The age of the user.
     */
    private int userAge;
}
