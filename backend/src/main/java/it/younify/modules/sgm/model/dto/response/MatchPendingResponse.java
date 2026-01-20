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
public class MatchPendingResponse {

    private Long matchID;

    private String senderName;

    private String senderPhoto;

    private int senderAge;

    private String senderUni;

    private String senderDegree;

    private Status status;
}
