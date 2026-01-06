package it.younify.modules.sgtr.controller;

import it.younify.core.model.ApiResponse;
import it.younify.modules.sgtr.model.dto.request.CommentPostRequest;
import it.younify.modules.sgtr.model.dto.request.CommentVoteRequest;
import it.younify.modules.sgtr.model.dto.response.CommentResponse;
import it.younify.modules.sgtr.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/thread")
@RequiredArgsConstructor
public class CommentController {

    private final CommentPostService commentPostService;
    private final CommentDeleteService commentDeleteService;
    private final CommentViewService commentViewService;
    private final CommentVoteService commentVoteService;


    @PostMapping("/comment/post")
    public ResponseEntity<ApiResponse<Void>> post(@Valid @RequestBody CommentPostRequest commentPostRequest) {

        commentPostService.post(commentPostRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("POSTED_SUCCESSFULLY")
                        .build()
        );
    }


    @DeleteMapping("/comment/delete/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable("commentId") Long commentId) {

        commentDeleteService.delete(commentId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("DELETED_SUCCESSFULLY")
                        .build()
        );
    }


    @PostMapping("/comment/react")
    public ResponseEntity<ApiResponse<Void>> reactCFU(@RequestBody CommentVoteRequest commentVoteRequest) {

        commentVoteService.vote(commentVoteRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("REACT_SUCCESSFULLY")
                        .build()
        );
    }

    @GetMapping("/comment/{threadId}")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getComment(@PathVariable("threadId") Long threadId,
                                                                         @PageableDefault(size = 10) Pageable pageable){

        return ResponseEntity.ok(
                ApiResponse.<Page<CommentResponse>>builder()
                        .data(commentViewService.getCommentByCFU(threadId, pageable))
                        .message("VIEW_SUCCESSFULLY")
                        .build()
        );
    }
}