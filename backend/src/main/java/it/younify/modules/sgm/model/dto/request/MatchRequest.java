package it.younify.modules.sgm.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Represents the request body for creating a new match request.
 * This DTO is used to transfer the ID of the intended recipient from the client to the server.
 */
@Data
public class MatchRequest {

    /**
     * The unique identifier of the user who will receive the match request.
     */
    @NotBlank
    private String receiverID;
}
