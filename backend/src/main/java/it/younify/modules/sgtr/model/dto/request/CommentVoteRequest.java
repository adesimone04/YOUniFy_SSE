package it.younify.modules.sgtr.model.dto.request;

import it.younify.modules.sgtr.model.entity.VoteType;
import lombok.Data;

@Data
public class CommentVoteRequest {

    private Long commentId;

    private VoteType voteType;
}
