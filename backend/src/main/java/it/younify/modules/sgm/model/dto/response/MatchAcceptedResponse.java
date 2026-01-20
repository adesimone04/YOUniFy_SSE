package it.younify.modules.sgm.model.dto.response;


import it.younify.modules.sgm.model.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an accepted match, providing details of the matched user.
 * This DTO is used to transfer the details of an accepted match to the client for display.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchAcceptedResponse {

    /**
     * The unique identifier of the match.
     */
    private Long matchID;

    /**
     * The name of the matched user.
     */
    private String matcherName;

    /**
     * The URL of the matched user's profile photo.
     */
    private String matcherPhoto;

    /**
     * The age of the matched user.
     */
    private int matcherAge;

    /**
     * The university the matched user attends.
     */
    private String matcherUni;

    /**
     * The matched user's course of study (Corso di Laurea).
     */
    private String matcherDegree;

    /**
     * The contact information of the matched user, revealed upon acceptance.
     */
    private String matcherContact;

    /**
     * The current status of the match.
     * @see Status
     */
    private Status status;
}
