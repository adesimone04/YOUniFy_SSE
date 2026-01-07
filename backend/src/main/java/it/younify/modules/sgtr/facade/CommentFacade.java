package it.younify.modules.sgtr.facade;

import it.younify.modules.sgtr.model.dto.request.CommentPostRequest;
import it.younify.modules.sgtr.model.dto.request.CommentVoteRequest;
import it.younify.modules.sgtr.model.dto.response.CommentResponse;
import it.younify.modules.sgtr.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * The {@code CommentFacade} class serves as a high-level entry point for all comment-related operations.
 * It orchestrates multiple services to handle the business logic for creating, deleting,
 * voting on, and viewing comments.
 *
 * @see it.younify.modules.sgtr.service.CommentPostService
 * @see it.younify.modules.sgtr.service.CommentDeleteService
 * @see it.younify.modules.sgtr.service.CommentViewService
 * @see it.younify.modules.sgtr.service.CommentVoteService
 * @see it.younify.modules.sgtr.service.CensorshipService
 */
@Component
@RequiredArgsConstructor
public class CommentFacade {

    private final CommentPostService commentPostService;
    private final CommentDeleteService commentDeleteService;
    private final CommentViewService commentViewService;
    private final CommentVoteService commentVoteService;

    private final CensorshipService censorshipService;


    /**
     * Creates a new comment, first checking for and censoring any sensitive content.
     *
     * @param commentPostRequest The request containing the comment's data.
     */
    public void createComment(CommentPostRequest commentPostRequest) {

        String content = commentPostRequest.getContent();

        if(censorshipService.containsSensitiveContent(content)){

            commentPostRequest.setContent(censorshipService.censorText(content));
        }

        commentPostService.post(commentPostRequest);
    }


    /**
     * Deletes a comment by its ID.
     *
     * @param commentId The ID of the comment to delete.
     */
    public void deleteComment(Long commentId) {

        commentDeleteService.delete(commentId);
    }


    /**
     * Casts a vote on a comment.
     *
     * @param commentVoteRequest The request containing the vote information.
     */
    public void reactionComment(CommentVoteRequest commentVoteRequest) {

        commentVoteService.vote(commentVoteRequest);
    }


    /**
     * Retrieves a paginated list of comments for a given thread, sorted by vote count (CFU).
     *
     * @param threadId The ID of the thread to retrieve comments for.
     * @param pageable The pagination information.
     * @return A paginated list of comments.
     */
    public Page<CommentResponse> commentView(Long threadId, Pageable pageable){

        return commentViewService.getCommentByCFU(threadId, pageable);
    }
}
