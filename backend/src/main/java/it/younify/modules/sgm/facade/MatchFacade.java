package it.younify.modules.sgm.facade;

import it.younify.core.exception.BadRequestException;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.service.validator.CdlValidationService;
import it.younify.modules.sgac.service.validator.UniversityValidationService;
import it.younify.modules.sgm.model.dto.request.MatchReactionRequest;
import it.younify.modules.sgm.model.dto.request.MatchRequest;
import it.younify.modules.sgm.model.dto.response.MatchAcceptedResponse;
import it.younify.modules.sgm.model.dto.response.MatchPendingResponse;
import it.younify.modules.sgm.model.dto.response.MatchCardResponse;
import it.younify.modules.sgm.model.entity.Match;
import it.younify.modules.sgm.repository.MatchRepository;
import it.younify.modules.sgm.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * The {@code MatchFacade} class provides a simplified interface to the matching module's functionality.
 * It orchestrates various services to handle match-related operations, including sending, reacting to,
 * deleting, and viewing matches, as well as retrieving match cards and total matched counts.
 *
 * @see MatchSendService
 * @see MatchViewService
 * @see MatchReactService
 * @see MatchDeleteService
 * @see MatchCardViewService
 * @see TotalMatchedService
 */
@Component
@RequiredArgsConstructor
public class MatchFacade {

    private final MatchSendService matchSendService;
    private final MatchViewService matchViewService;
    private final MatchReactService matchReactService;
    private final MatchDeleteService matchDeleteService;
    private final MatchCardViewService matchCardViewService;

    private final TotalMatchedService totalMatchedService;

    private final UniversityValidationService universityValidationService;
    private final CdlValidationService cdlValidationService;

    private final MatchRepository matchRepository;

    /**
     * Sends a new match request from the authenticated user to a specified receiver.
     *
     * @param matchRequest The request object containing the ID of the receiver.
     */
    public void send(MatchRequest matchRequest) {

        matchSendService.send(matchRequest);
    }

    /**
     * Processes a reaction (accept or reject) to a match request.
     *
     * @param matchReactionRequest The request object containing the match ID and the desired status.
     */
    public void react(MatchReactionRequest matchReactionRequest) {

        matchReactService.react(matchReactionRequest);
    }

    /**
     * Deletes a match initiated by the currently authenticated user.
     * This method ensures that only the sender of the match can delete it.
     *
     * @param matchId The ID of the match to delete.
     * @throws it.younify.core.exception.ResourceNotFoundException if the match does not exist.
     * @throws BadRequestException if the authenticated user is not the sender of the match.
     */
    public void delete(Long matchId) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;

        Optional<Match> opt = matchRepository.findById(matchId);
        if (opt.isPresent()) {

            Match match = opt.get();
            if(match.getSender().getID().equals(user.getID())){

                matchDeleteService.deleteMatch(match);
            }
            else throw new BadRequestException("You are not the sender of this match", "WRONG_SENDER");
        }
        else throw new BadRequestException("Match not found", "MATCH_NOT_FOUND");
    }

    /**
     * Retrieves a paginated list of match cards for the authenticated user's university.
     * These cards represent potential matches for the user.
     *
     * @param pageable The pagination information.
     * @return A paginated list of {@link MatchCardResponse} objects.
     */
    public Page<MatchCardResponse> getMatchCards(Pageable pageable){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;

        return matchCardViewService.getMatchCards(user.getUniversity(), pageable);
    }

    /**
     * Retrieves a paginated list of match cards filtered by a specific university.
     *
     * @param university The university to filter by.
     * @param pageable The pagination information.
     * @return A paginated list of {@link MatchCardResponse} objects.
     * @throws BadRequestException if the provided university is not valid.
     */
    public Page<MatchCardResponse> getMatchCardsByUni(String university, Pageable pageable){

        if(universityValidationService.isValidUniversity(university)){

            return matchCardViewService.getMatchCards(university, pageable);
        }
        else throw new BadRequestException("The university is not valid", "UNIVERSITY_NOT_VALID");
    }

    /**
     * Retrieves a paginated list of match cards filtered by a specific university and course of study.
     *
     * @param university The university to filter by.
     * @param cdl The course of study to filter by.
     * @param pageable The pagination information.
     * @return A paginated list of {@link MatchCardResponse} objects.
     * @throws BadRequestException if the provided university or course of study is not valid.
     */
    public Page<MatchCardResponse> getMatchCardsByUniAndCdl(String university, String cdl, Pageable pageable){

        if(cdlValidationService.isValidCdl(cdl) && universityValidationService.isValidUniversity(university)){

            return matchCardViewService.getMatchCardsByUniAndCdl(university, cdl, pageable);
        }
        else throw new BadRequestException("The university or cdl is not valid", "UNIVERSITY_OR_CDL_NOT_VALID");
    }

    /**
     * Retrieves a paginated list of match requests sent by the authenticated user that are still pending.
     *
     * @param pageable The pagination information.
     * @return A paginated list of {@link MatchPendingResponse} objects.
     */
    public Page<MatchPendingResponse> getSentMatches(Pageable pageable){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return matchViewService.getSentPendingMatches((User) principal, pageable);
    }


    /**
     * Retrieves a paginated list of match requests received by the authenticated user that are still pending.
     *
     * @param pageable The pagination information.
     * @return A paginated list of {@link MatchPendingResponse} objects.
     */
    public Page<MatchPendingResponse> getReceivedMatches(Pageable pageable){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return matchViewService.getReceivedPendingMatches((User) principal, pageable);
    }

    /**
     * Retrieves a paginated list of accepted matches for the authenticated user.
     *
     * @param pageable The pagination information.
     * @return A paginated list of {@link MatchAcceptedResponse} objects.
     */
    public Page<MatchAcceptedResponse> getAcceptedMatches(Pageable pageable){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return matchViewService.getSuccessMatch((User) principal, pageable);
    }

    /**
     * Retrieves the total number of accepted matches for the authenticated user.
     *
     * @return The total count of accepted matches.
     */
    public Integer getTotalMatched(){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return totalMatchedService.getTotalMatched((User) principal);
    }
}
