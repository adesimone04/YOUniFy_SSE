package it.younify.modules.sgm.model.dto.response;


import it.younify.modules.sgm.model.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchAcceptedResponse {

    private Long matchID;

    private String matcherName;

    private String matcherPhoto;

    private int matcherAge;

    private String matcherUni;

    private String matcherDegree;

    private String matcherContact;

    private Status status;
}
