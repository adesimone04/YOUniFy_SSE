package it.younify.modules.sgm.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MatchRequest {

    @NotBlank
    private String receiverID;
}
