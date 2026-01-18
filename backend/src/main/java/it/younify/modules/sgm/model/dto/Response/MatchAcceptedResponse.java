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
public class MatchAcceptedResponse {
    @NotBlank
    private Long matchID;

    @NotBlank
    private String matcherName;

    @NotBlank
    private String matcherPhoto;

    @NotNull
    private int matcherAge;

    @NotBlank
    private String matcherUni;

    @NotBlank
    private String matcherDegree;

    @NotBlank
    private String matcherContact;

    @NotNull
    private Status status;
}
