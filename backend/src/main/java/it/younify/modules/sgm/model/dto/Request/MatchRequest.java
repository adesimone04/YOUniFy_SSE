package it.younify.modules.sgm.model.dto.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MatchRequest {
    @NotBlank
    private String receiverID;
}
