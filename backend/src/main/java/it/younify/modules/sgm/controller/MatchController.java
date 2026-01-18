package it.younify.modules.sgm.controller;


import it.younify.core.model.ApiResponse;
import it.younify.modules.sgm.facade.MatchFacade;
import it.younify.modules.sgm.model.dto.Request.MatchReactionRequest;
import it.younify.modules.sgm.model.dto.Request.MatchRequest;
import it.younify.modules.sgm.model.dto.Response.MatchAcceptedResponse;
import it.younify.modules.sgm.model.dto.Response.MatchPendingResponse;
import it.younify.modules.sgm.model.dto.Response.MatchcardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class MatchController {
    private final MatchFacade matchFacade;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> send(@RequestBody MatchRequest matchRequest) {
        matchFacade.send(matchRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("MATCH_SENT_SUCCESSFULLY")
                        .internalCode("MATCH_SENT_SUCCESSFULLY")
                        .build()
        );
    }

    @PostMapping("/react")
    public ResponseEntity<ApiResponse<Void>> send(@RequestBody MatchReactionRequest matchReactionRequest) {
        matchFacade.react(matchReactionRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("MATCH_REACTED_SUCCESSFULLY")
                        .internalCode("MATCH_REACTED_SUCCESSFULLY")
                        .build()
        );
    }

    @DeleteMapping("/notanymore/{matchId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("matchId") Long matchId ) {
        matchFacade.delete(matchId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("MATCH_DELETED_SUCCESSFULLY")
                        .internalCode("MATCH_DELETED_SUCCESSFULLY")
                        .build()
        );
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<Page<MatchcardResponse>>> getMatchcards(@PageableDefault(size = 10) Pageable pageable){
        Page<MatchcardResponse>  page = matchFacade.getMatchcards(pageable);
        return ResponseEntity.ok(
                ApiResponse.<Page<MatchcardResponse>>builder()
                        .message("RETRIEVED_SUCCESSFULLY")
                        .internalCode("RETRIEVED_SUCCESSFULLY")
                        .data(page)
                        .build()
        );
    }

    @GetMapping("/{university}")
    public ResponseEntity<ApiResponse<Page<MatchcardResponse>>> getMatchcardsByUni(@PathVariable("university") String university,
                                                                                   @PageableDefault(size = 10) Pageable pageable) {
        Page<MatchcardResponse>  page = matchFacade.getMatchcardsByUni(university, pageable);
        return ResponseEntity.ok(
                ApiResponse.<Page<MatchcardResponse>>builder()
                        .message("RETRIEVED_SUCCESSFULLY")
                        .internalCode("RETRIEVED_SUCCESSFULLY")
                        .data(page)
                        .build()
        );
    }

    @GetMapping("/{university}/{cdl}")
    public ResponseEntity<ApiResponse<Page<MatchcardResponse>>> getMatchcardsByUni(@PathVariable("university") String university,
                                                                                   @PathVariable("cdl") String cdl,
                                                                                   @PageableDefault(size = 10) Pageable pageable) {
        Page<MatchcardResponse>  page = matchFacade.getMatchcardsByUniAndCdl(university, cdl, pageable);
        return ResponseEntity.ok(
                ApiResponse.<Page<MatchcardResponse>>builder()
                        .message("RETRIEVED_SUCCESSFULLY")
                        .internalCode("RETRIEVED_SUCCESSFULLY")
                        .data(page)
                        .build()
        );
    }

    @GetMapping("/sended")
    public ResponseEntity<ApiResponse<Page<MatchPendingResponse>>> getSentPendingMatched(@PageableDefault(size = 10) Pageable pageable) {
        Page<MatchPendingResponse>  page = matchFacade.getSentMatches(pageable);
        return ResponseEntity.ok(
                ApiResponse.<Page<MatchPendingResponse>>builder()
                        .message("RETRIEVED_SUCCESSFULLY")
                        .internalCode("RETRIEVED_SUCCESSFULLY")
                        .data(page)
                        .build()
        );
    }

    @GetMapping("/received")
    public ResponseEntity<ApiResponse<Page<MatchPendingResponse>>> getReceivedPendingMatches(@PageableDefault(size = 10) Pageable pageable) {
        Page<MatchPendingResponse>  page = matchFacade.getReceivedMatches(pageable);
        return ResponseEntity.ok(
                ApiResponse.<Page<MatchPendingResponse>>builder()
                        .message("RETRIEVED_SUCCESSFULLY")
                        .internalCode("RETRIEVED_SUCCESSFULLY")
                        .data(page)
                        .build()
        );
    }

    @GetMapping("/accepted")
    public ResponseEntity<ApiResponse<Page<MatchAcceptedResponse>>> getAcceptedMatches(@PageableDefault(size = 10) Pageable pageable) {
        Page<MatchAcceptedResponse>  page = matchFacade.getAcceptedMatches(pageable);
        return ResponseEntity.ok(
                ApiResponse.<Page<MatchAcceptedResponse>>builder()
                        .message("RETRIEVED_SUCCESSFULLY")
                        .internalCode("RETRIEVED_SUCCESSFULLY")
                        .data(page)
                        .build()
        );
    }
}
