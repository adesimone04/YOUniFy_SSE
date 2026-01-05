package it.younify.modules.sgtr.controller;

import it.younify.core.model.ApiResponse;
import it.younify.modules.sgtr.model.dto.request.ThreadVoteRequest;
import it.younify.modules.sgtr.service.ThreadVoteService;
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
public class ReactionController {

    private final ThreadVoteService threadVoteService;

    @PostMapping("/react")
    public ResponseEntity<ApiResponse<Void>> reactCFU(@RequestBody ThreadVoteRequest threadVoteRequest) {

        threadVoteService.vote(threadVoteRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("REACT_SUCCESSFULLY")
                        .build()
        );
    }
}