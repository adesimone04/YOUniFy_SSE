package it.younify.modules.sgtr.controller;

import it.younify.core.model.ApiResponse;
import it.younify.modules.sgtr.model.dto.request.ThreadPostRequest;
import it.younify.modules.sgtr.model.dto.request.ThreadVoteRequest;
import it.younify.modules.sgtr.model.dto.response.ThreadResponse;
import it.younify.modules.sgtr.model.entity.FlagCategory;
import it.younify.modules.sgtr.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/thread")
@RequiredArgsConstructor

public class ThreadController {

    private final ThreadPostService threadPostService;
    private final ThreadViewService threadViewService;
    private final ThreadDeleteService threadDeleteService;
    private final ThreadVoteService threadVoteService;

    private final CensorshipService censorshipService;


    @PostMapping("/post")
    public ResponseEntity<ApiResponse<Void>> post(@Valid @RequestBody ThreadPostRequest threadPostRequest) {

        threadPostService.post(threadPostRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("POSTED_SUCCESSFULLY")
                        .build()
        );
    }


    @DeleteMapping("/delete/{threadId}")
    public ResponseEntity<ApiResponse<Void>> deleteThread(@PathVariable("threadId") Long threadId) {

        threadDeleteService.delete(threadId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("DELETED_SUCCESSFULLY")
                        .build()
        );
    }


    @PostMapping("/react")
    public ResponseEntity<ApiResponse<Void>> reactCFU(@RequestBody ThreadVoteRequest threadVoteRequest) {

        threadVoteService.vote(threadVoteRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("REACT_SUCCESSFULLY")
                        .build()
        );
    }


    @GetMapping()
    public ResponseEntity<ApiResponse<Page<ThreadResponse>>> getThreadsByDate(@PageableDefault(size = 10) Pageable pageable){

        return ResponseEntity.ok(
                ApiResponse.<Page<ThreadResponse>>builder()
                        .message("VIEW_SUCCESSFULLY")
                        .data(threadViewService.getThreadsByDate(pageable))
                        .build()
        );
    }


    @GetMapping("/utility/flag")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getFlags(){

        return ResponseEntity.ok(
                ApiResponse.<List<Map<String, String>>>builder()
                        .message("RETRIEVE_SUCCESSFULY")
                        .data(
                                Arrays.stream(FlagCategory.values())
                                .map(flag -> Map.of(
                                        "value", flag.name(),
                                        "label", flag.getLabel()
                                ))
                                .collect(Collectors.toList())
                        )
                        .build()
        );
    }


    @GetMapping("/flag/{flag}")
    public ResponseEntity<ApiResponse<Page<ThreadResponse>>> getThreadsByFlag(@PathVariable("flag") String flag,
                                                                              @PageableDefault(size = 10) Pageable pageable) {
        //NON FUNZIONA
        FlagCategory flagCategory = FlagCategory.valueOf(flag);
        return ResponseEntity.ok(
                ApiResponse.<Page<ThreadResponse>>builder()
                        .message("VIEW_SUCCESSFULLY")
                        .data(threadViewService.getThreadsByFlag(flagCategory, pageable))
                        .build()
        );
    }


    @GetMapping("/uni/{university}")
    public ResponseEntity<ApiResponse<Page<ThreadResponse>>> getThreadsByUniversity(@PathVariable("university") String university,
                                                                              @PageableDefault(size = 10) Pageable pageable){
        return ResponseEntity.ok(
                ApiResponse.<Page<ThreadResponse>>builder()
                        .message("VIEW_SUCCESSFULLY")
                        .data(threadViewService.getThreadsByUniversity(university, pageable))
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/utility/refresh-censorship")
    public ResponseEntity<ApiResponse<Void>> refreshCensorshipList(){

        censorshipService.init();

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("UPDATE_SUCCESSFULLY")
                        .build()
        );
    }
}
