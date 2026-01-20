package it.younify.modules.sgm.model.dto.request;

import it.younify.modules.sgm.model.entity.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MatchReactionRequest {

    @NotNull
    private long matchId;

    @NotNull
    private Status status;
}
