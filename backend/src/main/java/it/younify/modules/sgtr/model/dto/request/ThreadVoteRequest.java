package it.younify.modules.sgtr.model.dto.request;

import it.younify.modules.sgtr.model.entity.VoteType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ThreadVoteRequest {

    private Long threadId;

    private VoteType voteType;
}
