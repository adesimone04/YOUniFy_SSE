package it.younify.modules.sgm.model.dto.Response;

import it.younify.modules.sgm.model.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchPendingResponse
{
    @NotBlank
    private Long matchID;

    @NotBlank
    private String senderName;

    @NotBlank
    private String senderPhoto;

    @NotNull
    private int senderAge;

    @NotBlank
    private String senderUni;

    @NotBlank
    private String senderDegree;

    @NotNull
    private Status status;
}
