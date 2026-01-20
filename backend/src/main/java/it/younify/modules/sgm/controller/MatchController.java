package it.younify.modules.sgm.controller;

import it.younify.core.model.ApiResponse;
import it.younify.modules.sgm.facade.MatchFacade;
import it.younify.modules.sgm.model.dto.request.MatchReactionRequest;
import it.younify.modules.sgm.model.dto.request.MatchRequest;
import it.younify.modules.sgm.model.dto.response.MatchAcceptedResponse;
import it.younify.modules.sgm.model.dto.response.MatchPendingResponse;
import it.younify.modules.sgm.model.dto.response.MatchCardResponse;
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

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
@Validated
public class MatchController {

    private final MatchFacade matchFacade;

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


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{university}")
    public ResponseEntity<ApiResponse<Page<MatchCardResponse>>> getMatchCardsByUni(@Positive @PathVariable("university") String university,
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


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{university}/{cdl}")
    public ResponseEntity<ApiResponse<Page<MatchCardResponse>>> getMatchCardsByUniAndCdl(@Positive @PathVariable("university") String university,
                                                                                         @Positive @PathVariable("cdl") String cdl,
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
