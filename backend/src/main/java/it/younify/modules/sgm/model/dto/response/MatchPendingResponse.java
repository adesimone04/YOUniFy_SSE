package it.younify.modules.sgm.model.dto.response;

import it.younify.modules.sgm.model.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a pending match request from another user.
 * This DTO is used to transfer the details of a match request to the client for display.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchPendingResponse {

    /**
     * The unique identifier of the match.
     */
    private Long matchID;

    /**
     * The name of the user who sent the match request.
     */
    private String senderName;

    /**
     * The URL of the sender's profile photo.
     */
    private String senderPhoto;

    /**
     * The age of the sender.
     */
    private int senderAge;

    /**
     * The university the sender attends.
     */
    private String senderUni;

    /**
     * The sender's course of study (Corso di Laurea).
     */
    private String senderCdl;

    /**
     * The current status of the match request.
     * @see Status
     */
    private Status status;
}
