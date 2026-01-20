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

@Component
@RequiredArgsConstructor
public class MatchFacade {

    private final MatchSendService matchSendService;
    private final MatchViewService matchViewService;
    private final MatchReactService matchReactService;
    private final MatchDeleteService matchDeleteService;
    private final MatchCardViewService matchCardViewService;

    private final UniversityValidationService universityValidationService;
    private final CdlValidationService cdlValidationService;

    private final MatchRepository matchRepository;


    public void send(MatchRequest matchRequest) {

        matchSendService.send(matchRequest);
    }

    public void react(MatchReactionRequest matchReactionRequest) {

        matchReactService.react(matchReactionRequest);
    }

    public void delete(Long matchId) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;

        Optional<Match> opt = matchRepository.findById(matchId);
        if (opt.isPresent()) {

            Match match = opt.get();
            if(match.getSender().getID().equals(user.getID())){

                matchDeleteService.deleteMatch(match);
            }
        }
    }

    public Page<MatchCardResponse> getMatchCards(Pageable pageable){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;

        return matchCardViewService.getMatchCards(user.getUniversity(), pageable);
    }


    public Page<MatchCardResponse> getMatchCardsByUni(String university, Pageable pageable){

        if(universityValidationService.isValidUniversity(university)){

            return matchCardViewService.getMatchCards(university, pageable);
        }
        else throw new BadRequestException("The university is not valid", "UNIVERSITY_NOT_VALID");
    }


    public Page<MatchCardResponse> getMatchCardsByUniAndCdl(String university, String cdl, Pageable pageable){

        if(cdlValidationService.isValidCdl(cdl) && universityValidationService.isValidUniversity(university)){

            return matchCardViewService.getMatchCardsByUniAndCdl(university, cdl, pageable);
        }
        else throw new BadRequestException("The university or cdl is not valid", "UNIVERSITY_OR_CDL_NOT_VALID");
    }


    public Page<MatchPendingResponse> getSentMatches(Pageable pageable){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return matchViewService.getSentPendingMatches((User) principal, pageable);
    }


    public Page<MatchPendingResponse> getReceivedMatches(Pageable pageable){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return matchViewService.getReceivedPendingMatches((User) principal, pageable);
    }

    public Page<MatchAcceptedResponse> getAcceptedMatches(Pageable pageable){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return matchViewService.getSuccessMatch((User) principal, pageable);
    }
}
