package it.younify.modules.sgtr.controller;

import it.younify.core.model.ApiResponse;
import it.younify.modules.sgtr.model.dto.request.CommentPostRequest;
import it.younify.modules.sgtr.model.dto.request.ThreadPostRequest;
import it.younify.modules.sgtr.model.dto.response.CommentResponse;
import it.younify.modules.sgtr.model.dto.response.ThreadResponse;
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

public class ThreadController {

    private final ThreadPostService threadPostService;
    private final CommentPostService commentPostService;
    private final ThreadViewService threadViewService;
    private final ThreadDeleteService threadDeleteService;
    private final CommentDeleteService commentDeleteService;
    private final CommentViewService commentViewService;

    @PostMapping("/post")
    public ResponseEntity<ApiResponse<Void>> post(@Valid @RequestBody ThreadPostRequest threadPostRequest) {

        threadPostService.post(threadPostRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("POSTED SUCCESSFULLY")
                        .build()
        );
    }


    @PostMapping("/comment/post")
    public ResponseEntity<ApiResponse<Void>> post(@Valid @RequestBody CommentPostRequest commentPostRequest) {

        commentPostService.post(commentPostRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("POSTED SUCCESSFULLY")
                        .build()
        );
    }

    @DeleteMapping("/delete/{threadId}")
    public ResponseEntity<ApiResponse<Void>> deleteThread(@PathVariable("threadId") Long threadId) {

        threadDeleteService.delete(threadId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("DELETED SUCCESSFULLY")
                        .build()
        );
    }


    @DeleteMapping("/comment/delete/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable("commentId") Long commentId) {

        commentDeleteService.delete(commentId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("DELETED SUCCESSFULLY")
                        .build()
        );
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<Page<ThreadResponse>>> getThreadsByDate(@PageableDefault(size = 10) Pageable pageable){

        return ResponseEntity.ok(
                ApiResponse.<Page<ThreadResponse>>builder()
                        .data(threadViewService.getThreadsByDate(pageable))
                        .message("VIEW SUCCESSFULLY")
                        .build()
        );
    }

    @GetMapping("/flag/{flag}")
    public ResponseEntity<ApiResponse<Page<ThreadResponse>>> getThreadsByFlag(@PathVariable("flag") String flag,
                                                                              @PageableDefault(size = 10) Pageable pageable){

        return ResponseEntity.ok(
                ApiResponse.<Page<ThreadResponse>>builder()
                        .data(threadViewService.getThreadsByFlag(flag, pageable))
                        .message("VIEW SUCCESSFULLY")
                        .build()
        );
    }

    @GetMapping("/uni/{university}")
    public ResponseEntity<ApiResponse<Page<ThreadResponse>>> getThreadsByUniversity(@PathVariable("university") String university,
                                                                              @PageableDefault(size = 10) Pageable pageable){

        return ResponseEntity.ok(
                ApiResponse.<Page<ThreadResponse>>builder()
                        .data(threadViewService.getThreadsByUniversity(university, pageable))
                        .message("VIEW SUCCESSFULLY")
                        .build()
        );
    }

    @GetMapping("/comment/{threadId}")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getComment(@PathVariable("threadId") Long threadId,
                                                                         @PageableDefault(size = 10) Pageable pageable){

        return ResponseEntity.ok(
                ApiResponse.<Page<CommentResponse>>builder()
                        .data(commentViewService.getCommentByCFU(threadId, pageable ))
                        .message("VIEW SUCCESSFULLY")
                        .build()
        );
    }
}
