package it.younify.modules.sgm.model.dto.Response;

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
public class MatchcardResponse {
    @NotBlank
    private String userID;

    @NotBlank
    private String userPhoto;

    @NotBlank
    private String userName;

    @NotBlank
    private String userUniversity;

    @NotBlank
    private String userCdl;

    @NotNull
    private int userAge;
}
