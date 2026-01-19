package it.younify.modules.sgtr.controller;

import it.younify.core.model.ApiResponse;
import it.younify.modules.sgtr.facade.ThreadFacade;
import it.younify.modules.sgtr.model.dto.request.ThreadPostRequest;
import it.younify.modules.sgtr.model.dto.request.ThreadVoteRequest;
import it.younify.modules.sgtr.model.dto.response.ThreadResponse;
import it.younify.modules.sgtr.model.entity.FlagCategory;
import it.younify.modules.sgtr.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * The {@code ThreadController} class handles all HTTP requests related to threads.
 * This includes creating, deleting, voting, and retrieving threads in various ways.
 * It primarily interacts with the {@link ThreadFacade} to orchestrate these operations.
 *
 * @see it.younify.modules.sgtr.facade.ThreadFacade
 */
@RestController
@RequestMapping("/api/thread")
@RequiredArgsConstructor
@Validated
public class ThreadController {

    private final ThreadFacade threadFacade;

    /**
     * Creates a new thread.
     * The content of the thread is validated and checked for inappropriate content before being saved.
     *
     * @param threadPostRequest The request body containing the thread's content and his flag.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PostMapping("/post")
    public ResponseEntity<ApiResponse<Void>> post(@Valid @RequestBody ThreadPostRequest threadPostRequest) {

        threadFacade.createThread(threadPostRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("POSTED_SUCCESSFULLY")
                        .internalCode("POSTED_SUCCESSFULLY")
                        .build()
        );
    }


    /**
     * Deletes a thread by its ID.
     * This operation is restricted and can only be performed by the thread's author or an admin.
     *
     * @param threadId The unique identifier of the thread to be deleted.
     * @return A {@link ResponseEntity} with a success message.
     * @throws org.springframework.security.access.AccessDeniedException if the current user is not authorized to delete the thread.
     * @throws it.younify.core.exception.ResourceNotFoundException if the thread does not exist.
     */
    @DeleteMapping("/delete/{threadId}")
    public ResponseEntity<ApiResponse<Void>> deleteThread(@Positive @PathVariable("threadId") Long threadId) {

        threadFacade.deleteThread(threadId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("DELETED_SUCCESSFULLY")
                        .internalCode("DELETED_SUCCESSFULLY")
                        .build()
        );
    }


    /**
     * Handles a user's vote (upvote or downvote) on a specific thread.
     * <p>
     * This endpoint delegates the core voting logic to the {@link it.younify.modules.sgtr.facade.ThreadFacade},
     * which manages updating the thread's score (CFU) and preventing duplicate votes.
     * The outcome is a simple success message if the operation is completed without errors.
     *
     * @param threadVoteRequest The request body containing the ID of the thread and the type of vote being cast.
     * @return A {@link ResponseEntity} containing a generic {@link ApiResponse} to confirm the action was successful.
     * @throws it.younify.core.exception.ResourceNotFoundException if the thread specified in the request does not exist.
     * @throws it.younify.core.exception.BadRequestException if the user attempts to vote on their own thread or if the vote request is otherwise invalid.
     * @see it.younify.modules.sgtr.model.dto.request.ThreadVoteRequest
     */
    @PostMapping("/react")
    public ResponseEntity<ApiResponse<Void>> reactCFU(@Valid @RequestBody ThreadVoteRequest threadVoteRequest) {

        threadFacade.voteThread(threadVoteRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("REACT_SUCCESSFULLY")
                        .internalCode("REACT_SUCCESSFULLY")
                        .build()
        );
    }


    /**
     * Retrieves a paginated list of all threads, sorted by creation date in descending order.
     *
     * @param pageable The pagination information.
     * @return A {@link ResponseEntity} containing a paginated list of {@link ThreadResponse}.
     */
    @GetMapping()
    public ResponseEntity<ApiResponse<Page<ThreadResponse>>> getThreadsByDate(@PageableDefault(size = 10) Pageable pageable){

        Page<ThreadResponse> page = threadFacade.getThreadsByDate(pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<ThreadResponse>>builder()
                        .message("VIEW_SUCCESSFULLY")
                        .internalCode("VIEW_SUCCESSFULLY")
                        .data(page)
                        .build()
        );
    }


    /**
     * Retrieves the list of all available flag categories.
     * This is a utility endpoint used by the frontend to populate filter options.
     *
     * @return A {@link ResponseEntity} containing a list of flag categories with their values and labels.
     */
    @GetMapping("/utility/flag")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getFlags(){

        return ResponseEntity.ok(
                ApiResponse.<List<Map<String, String>>>builder()
                        .message("RETRIEVE_SUCCESSFULLY")
                        .internalCode("RETRIEVE_SUCCESSFULLY")
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


    /**
     * Retrieves a paginated list of threads filtered by a specific flag category.
     *
     * @param flag The flag category to filter by.
     * @param pageable The pagination information.
     * @return A {@link ResponseEntity} containing a paginated list of {@link ThreadResponse}.
     */
    @GetMapping("/flag/{flag}")
    public ResponseEntity<ApiResponse<Page<ThreadResponse>>> getThreadsByFlag(@Positive @PathVariable("flag") String flag,
                                                                              @PageableDefault(size = 10) Pageable pageable) {

        Page<ThreadResponse> page = threadFacade.getThreadsByFlag(flag, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<ThreadResponse>>builder()
                        .message("VIEW_SUCCESSFULLY")
                        .internalCode("VIEW_SUCCESSFULLY")
                        .data(page)
                        .build()
        );
    }


    /**
     * Retrieves a paginated list of threads filtered by a specific university.
     *
     * @param university The university to filter by.
     * @param pageable The pagination information.
     * @return A {@link ResponseEntity} containing a paginated list of {@link ThreadResponse}.
     */
    @GetMapping("/uni/{university}")
    public ResponseEntity<ApiResponse<Page<ThreadResponse>>> getThreadsByUniversity(@Positive @PathVariable("university") String university,
                                                                              @PageableDefault(size = 10) Pageable pageable){

        Page<ThreadResponse> page = threadFacade.getThreadsByUniversity(university, pageable);
        return ResponseEntity.ok(
                ApiResponse.<Page<ThreadResponse>>builder()
                        .message("VIEW_SUCCESSFULLY")
                        .internalCode("VIEW_SUCCESSFULLY")
                        .data(page)
                        .build()
        );
    }


    /**
     * Retrieves a paginated list of threads posted by the currently authenticated user.
     * This endpoint provides a summary of the user's own contributions.
     *
     * @param pageable The pagination information, with a default size of 10.
     * @return A {@link ResponseEntity} containing a paginated list of {@link ThreadResponse} objects posted by the user.
     * @see ThreadFacade#getThreadPosted(Pageable)
     * @see Pageable
     * @see ThreadResponse
     */
    @GetMapping("/summary/posted")
    public ResponseEntity<ApiResponse<Page<ThreadResponse>>> getThreadsPosted(@PageableDefault(size = 10) Pageable pageable){

        Page<ThreadResponse> page = threadFacade.getThreadPosted(pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<ThreadResponse>>builder()
                        .data(page)
                        .message("RETRIEVE_SUCCESSFULLY")
                        .internalCode("RETRIEVE_SUCCESSFULLY")
                        .build()
        );
    }


    /**
     * Retrieves the total of CFUs made by the user/host threads and comments
     *
     * @return A {@link ResponseEntity} containing the total CFU.
     */
    @GetMapping("/summary/cfu")
    public ResponseEntity<ApiResponse<Integer>> getCFUSummary(){

        return ResponseEntity.ok(
                ApiResponse.<Integer>builder()
                        .data(threadFacade.getCFUTotal())
                        .message("RETRIEVE_SUCCESSFULLY")
                        .internalCode("RETRIEVE_SUCCESSFULLY")
                        .build()
                );
    }


    /**
     * Refreshes the censorship list from the source.
     * This is an administrative endpoint and requires ADMIN authority.
     *
     * @return A {@link ResponseEntity} with a success message.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/utility/refresh-censorship")
    public ResponseEntity<ApiResponse<Void>> refreshCensorshipList(){

        threadFacade.refreshCensorship();

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("UPDATE_SUCCESSFULLY")
                        .internalCode("UPDATE_SUCCESSFULLY")
                        .build()
        );
    }
}
