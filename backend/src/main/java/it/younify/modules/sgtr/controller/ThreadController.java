package it.younify.modules.sgtr.controller;

import it.younify.core.model.ApiResponse;
import it.younify.modules.sgtr.model.dto.request.CommentPostRequest;
import it.younify.modules.sgtr.model.dto.request.ThreadPostRequest;
import it.younify.modules.sgtr.service.CommentPostService;
import it.younify.modules.sgtr.service.ThreadPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/thread")
@RequiredArgsConstructor

public class ThreadController {

    private final ThreadPostService threadPostService;
    private final CommentPostService commentPostService;

    @PostMapping("/post")
    public ResponseEntity<ApiResponse<Void>> post(@Valid @RequestBody ThreadPostRequest threadPostRequest) {

        threadPostService.post(threadPostRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("POSTED SUCCESSFULLY")
                        .build()
        );
    }


    @PostMapping("comment/post")
    public ResponseEntity<ApiResponse<Void>> post(@Valid @RequestBody CommentPostRequest commentPostRequest) {

        commentPostService.post(commentPostRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("POSTED SUCCESSFULLY")
                        .build()
        );
    }






}
