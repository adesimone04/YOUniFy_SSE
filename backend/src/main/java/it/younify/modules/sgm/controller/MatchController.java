package it.younify.modules.sgm.controller;

import it.younify.core.model.ApiResponse;
import it.younify.modules.sgm.facade.MatchFacade;
import it.younify.modules.sgm.model.dto.request.MatchReactionRequest;
import it.younify.modules.sgm.model.dto.request.MatchRequest;
import it.younify.modules.sgm.model.dto.response.MatchAcceptedResponse;
import it.younify.modules.sgm.model.dto.response.MatchPendingResponse;
import it.younify.modules.sgm.model.dto.response.MatchCardResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * The {@code MatchController} class provides RESTful endpoints for managing user matches.
 * It handles HTTP requests for sending, reacting to, deleting, and viewing matches,
 * as well as retrieving match cards and statistics. This controller delegates the business logic
 * to the {@link MatchFacade}.
 *
 * @see MatchFacade
 * @see ApiResponse
 */
@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
@Validated
public class MatchController {

    private final MatchFacade matchFacade;

    /**
     * Handles the HTTP POST request to send a match request.
     * This endpoint is restricted to users with the 'USER' role.
     *
     * @param matchRequest The request body containing the receiver's ID.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} indicating the result of the operation.
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> send(@Valid @RequestBody MatchRequest matchRequest) {

        matchFacade.send(matchRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("MATCH_SENT_SUCCESSFULLY")
                        .internalCode("MATCH_SENT_SUCCESSFULLY")
                        .build()
        );
    }


    /**
     * Handles the HTTP POST request to react to a match request.
     * This endpoint is restricted to users with the 'USER' role.
     *
     * @param matchReactionRequest The request body containing the match ID and the reaction status.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} indicating the result of the operation.
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/react")
    public ResponseEntity<ApiResponse<Void>> react(@Valid @RequestBody MatchReactionRequest matchReactionRequest) {

        matchFacade.react(matchReactionRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("MATCH_REACTED_SUCCESSFULLY")
                        .internalCode("MATCH_REACTED_SUCCESSFULLY")
                        .build()
        );
    }


    /**
     * Handles the HTTP DELETE request to delete a match.
     * This endpoint is restricted to users with the 'USER' role.
     *
     * @param matchId The ID of the match to delete.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} indicating the result of the operation.
     */
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/notAnymore/{matchId}")
    public ResponseEntity<ApiResponse<Void>> delete(@Positive @PathVariable("matchId") Long matchId) {

        matchFacade.delete(matchId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("MATCH_DELETED_SUCCESSFULLY")
                        .internalCode("MATCH_DELETED_SUCCESSFULLY")
                        .build()
        );
    }


    /**
     * Handles the HTTP GET request to retrieve a paginated list of match cards.
     * This endpoint is restricted to users with the 'USER' role.
     *
     * @param pageable The pagination information.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} containing a paginated list of {@link MatchCardResponse} objects.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping()
    public ResponseEntity<ApiResponse<Page<MatchCardResponse>>> getMatchCards(@PageableDefault(size = 10) Pageable pageable){

        Page<MatchCardResponse> page = matchFacade.getMatchCards(pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<MatchCardResponse>>builder()
                        .message("VIEW_SUCCESSFULLY")
                        .internalCode("VIEW_SUCCESSFULLY")
                        .data(page)
                        .build()
        );
    }


    /**
     * Handles the HTTP GET request to retrieve a paginated list of match cards filtered by university.
     * This endpoint is restricted to users with the 'USER' role.
     *
     * @param university The university to filter by.
     * @param pageable The pagination information.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} containing a paginated list of {@link MatchCardResponse} objects.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{university}")
    public ResponseEntity<ApiResponse<Page<MatchCardResponse>>> getMatchCardsByUni(@NotBlank @PathVariable("university") String university,
                                                                                   @PageableDefault(size = 10) Pageable pageable) {

        Page<MatchCardResponse> page = matchFacade.getMatchCardsByUni(university, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<MatchCardResponse>>builder()
                        .message("VIEW_SUCCESSFULLY")
                        .internalCode("VIEW_SUCCESSFULLY")
                        .data(page)
                        .build()
        );
    }


    /**
     * Handles the HTTP GET request to retrieve a paginated list of match cards filtered by university and course of study.
     * This endpoint is restricted to users with the 'USER' role.
     *
     * @param university The university to filter by.
     * @param cdl The course of study to filter by.
     * @param pageable The pagination information.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} containing a paginated list of {@link MatchCardResponse} objects.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{university}/{cdl}")
    public ResponseEntity<ApiResponse<Page<MatchCardResponse>>> getMatchCardsByUniAndCdl(@NotBlank @PathVariable("university") String university,
                                                                                         @NotBlank @PathVariable("cdl") String cdl,
                                                                                         @PageableDefault(size = 10) Pageable pageable) {

        Page<MatchCardResponse> page = matchFacade.getMatchCardsByUniAndCdl(university, cdl, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<MatchCardResponse>>builder()
                        .message("VIEW_SUCCESSFULLY")
                        .internalCode("VIEW_SUCCESSFULLY")
                        .data(page)
                        .build()
        );
    }


    /**
     * Handles the HTTP GET request to retrieve a paginated list of sent pending matches.
     * This endpoint is restricted to users with the 'USER' role.
     *
     * @param pageable The pagination information.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} containing a paginated list of {@link MatchPendingResponse} objects.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/sent")
    public ResponseEntity<ApiResponse<Page<MatchPendingResponse>>> getSentPendingMatched(@PageableDefault(size = 10) Pageable pageable) {

        Page<MatchPendingResponse> page = matchFacade.getSentMatches(pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<MatchPendingResponse>>builder()
                        .message("RETRIEVED_SUCCESSFULLY")
                        .internalCode("RETRIEVED_SUCCESSFULLY")
                        .data(page)
                        .build()
        );
    }


    /**
     * Handles the HTTP GET request to retrieve a paginated list of received pending matches.
     * This endpoint is restricted to users with the 'USER' role.
     *
     * @param pageable The pagination information.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} containing a paginated list of {@link MatchPendingResponse} objects.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/received")
    public ResponseEntity<ApiResponse<Page<MatchPendingResponse>>> getReceivedPendingMatches(@PageableDefault(size = 10) Pageable pageable) {

        Page<MatchPendingResponse> page = matchFacade.getReceivedMatches(pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<MatchPendingResponse>>builder()
                        .message("RETRIEVED_SUCCESSFULLY")
                        .internalCode("RETRIEVED_SUCCESSFULLY")
                        .data(page)
                        .build()
        );
    }


    /**
     * Handles the HTTP GET request to retrieve a paginated list of accepted matches.
     * This endpoint is restricted to users with the 'USER' role.
     *
     * @param pageable The pagination information.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} containing a paginated list of {@link MatchAcceptedResponse} objects.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/accepted")
    public ResponseEntity<ApiResponse<Page<MatchAcceptedResponse>>> getAcceptedMatches(@PageableDefault(size = 10) Pageable pageable) {

        Page<MatchAcceptedResponse> page = matchFacade.getAcceptedMatches(pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<MatchAcceptedResponse>>builder()
                        .message("RETRIEVED_SUCCESSFULLY")
                        .internalCode("RETRIEVED_SUCCESSFULLY")
                        .data(page)
                        .build()
        );
    }


    /**
     * Handles the HTTP GET request to retrieve the total number of accepted matches for the authenticated user.
     * This endpoint is restricted to users with the 'USER' role.
     *
     * @return A {@link ResponseEntity} with an {@link ApiResponse} containing the total number of accepted matches.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/totalMatched")
    public ResponseEntity<ApiResponse<Integer>> getTotalMatched() {

        Integer totalMatched = matchFacade.getTotalMatched();

        return ResponseEntity.ok(
                ApiResponse.<Integer>builder()
                        .message("RETRIEVED_SUCCESSFULLY")
                        .internalCode("RETRIEVED_SUCCESSFULLY")
                        .data(totalMatched)
                        .build()
        );
    }
}
