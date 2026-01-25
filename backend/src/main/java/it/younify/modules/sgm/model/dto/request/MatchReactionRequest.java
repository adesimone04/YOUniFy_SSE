package it.younify.modules.sgm.model.dto.request;

import it.younify.modules.sgm.model.entity.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Represents the request body for reacting to a match request (e.g., accepting or rejecting).
 * This DTO is used to transfer the match ID and the desired status update from the client to the server.
 */
@Data
public class MatchReactionRequest {

    /**
     * The unique identifier of the match to which the reaction applies.
     */
    @NotNull
    private long matchId;

    /**
     * The new status for the match (e.g., {@link Status#ACCEPTED}, {@link Status#REJECTED}).
     * @see Status
     */
    @NotNull
    private Status status;
}
