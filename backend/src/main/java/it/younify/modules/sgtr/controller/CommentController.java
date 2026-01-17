package it.younify.modules.sgtr.controller;

import it.younify.core.model.ApiResponse;
import it.younify.modules.sgtr.facade.CommentFacade;
import it.younify.modules.sgtr.model.dto.request.CommentPostRequest;
import it.younify.modules.sgtr.model.dto.request.CommentVoteRequest;
import it.younify.modules.sgtr.model.dto.response.CommentResponse;
import it.younify.modules.sgtr.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * The {@code CommentController} class handles all HTTP requests related to comments on threads.
 * This includes creating, deleting, voting, and retrieving comments.
 * It uses the {@link CommentFacade} to coordinate these actions.
 *
 * @see it.younify.modules.sgtr.facade.CommentFacade
 */
@RestController
@RequestMapping("/api/thread")
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentFacade commentFacade;

    /**
     * Creates a new comment on a thread.
     * The comment's content is validated for appropriateness before being saved.
     *
     * @param commentPostRequest The request body containing the comment's content and the parent thread's ID.
     * @return A {@link ResponseEntity} with a success message.
     * @throws it.younify.core.exception.ResourceNotFoundException if the parent thread does not exist.
     */
    @PostMapping("/comment/post")
    public ResponseEntity<ApiResponse<Void>> post(@Valid @RequestBody CommentPostRequest commentPostRequest) {

        commentFacade.createComment(commentPostRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("POSTED_SUCCESSFULLY")
                        .internalCode("POSTED_SUCCESSFULLY")
                        .build()
        );
    }


    /**
     * Deletes a comment by its ID.
     * This operation is restricted and can only be performed by the comment's author or an admin.
     *
     * @param commentId The unique identifier of the comment to be deleted.
     * @return A {@link ResponseEntity} with a success message.
     * @throws org.springframework.security.access.AccessDeniedException if the current user is not authorized to delete the comment.
     * @throws it.younify.core.exception.ResourceNotFoundException if the comment does not exist.
     */
    @DeleteMapping("/comment/delete/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@Positive @PathVariable("commentId") Long commentId) {

        commentFacade.deleteComment(commentId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("DELETED_SUCCESSFULLY")
                        .internalCode("DELETED_SUCCESSFULLY")
                        .build()
        );
    }

    /**
     * Casts a vote (upvote or downvote) on a comment.
     * A user cannot vote on their own comment.
     *
     * @param commentVoteRequest The request body containing the comment ID and the vote type.
     * @return A {@link ResponseEntity} with a success message.
     * @throws it.younify.core.exception.ResourceNotFoundException if the comment does not exist.
     */
    @PostMapping("/comment/react")
    public ResponseEntity<ApiResponse<Void>> reactCFU(@RequestBody CommentVoteRequest commentVoteRequest) {

        commentFacade.reactionComment(commentVoteRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("REACT_SUCCESSFULLY")
                        .internalCode("REACT_SUCCESSFULLY")
                        .build()
        );
    }


    /**
     * Retrieves a paginated list of comments for a specific thread.
     *
     * @param threadId The ID of the thread whose comments are to be retrieved.
     * @param pageable The pagination information.
     * @return A {@link ResponseEntity} containing a paginated list of {@link CommentResponse}.
     */
    @GetMapping("/comment/{threadId}")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getComment(@Positive @PathVariable("threadId") Long threadId,
                                                                         @PageableDefault(size = 10) Pageable pageable){

        Page<CommentResponse> page = commentFacade.commentView(threadId, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<CommentResponse>>builder()
                        .data(page)
                        .message("VIEW_SUCCESSFULLY")
                        .internalCode("VIEW_SUCCESSFULLY")
                        .build()
        );
    }
}