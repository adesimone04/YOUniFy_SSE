package it.younify.modules.sgm.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchCardResponse {

    private String userID;

    private String userPhoto;

    private String userName;

    private String userUniversity;

    private String userCdl;

    private int userAge;
}
