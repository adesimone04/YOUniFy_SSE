package it.younify.modules.sgtr.facade;

import it.younify.modules.sgtr.model.dto.request.CommentPostRequest;
import it.younify.modules.sgtr.model.dto.request.CommentVoteRequest;
import it.younify.modules.sgtr.model.dto.response.CommentResponse;
import it.younify.modules.sgtr.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentFacade {

    private final CommentPostService commentPostService;
    private final CommentDeleteService commentDeleteService;
    private final CommentViewService commentViewService;
    private final CommentVoteService commentVoteService;

    private final CensorshipService censorshipService;


    public void createComment(CommentPostRequest commentPostRequest) {

        String content = commentPostRequest.getContent();

        if(censorshipService.containsSensitiveContent(content)){

            commentPostRequest.setContent(censorshipService.censorText(content));
        }

        commentPostService.post(commentPostRequest);
    }


    public void deleteComment(Long commentId) {

        commentDeleteService.delete(commentId);
    }


    public void reactionComment(CommentVoteRequest commentVoteRequest) {

        commentVoteService.vote(commentVoteRequest);
    }


    public Page<CommentResponse> commentView(Long threadId, Pageable pageable){

        return commentViewService.getCommentByCFU(threadId, pageable);
    }
}
