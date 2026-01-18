package it.younify.modules.sgm.facade;

import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgm.model.dto.Request.MatchReactionRequest;
import it.younify.modules.sgm.model.dto.Request.MatchRequest;
import it.younify.modules.sgm.model.dto.Response.MatchAcceptedResponse;
import it.younify.modules.sgm.model.dto.Response.MatchPendingResponse;
import it.younify.modules.sgm.model.dto.Response.MatchcardResponse;
import it.younify.modules.sgm.model.entity.Match;
import it.younify.modules.sgm.repository.MatchRepository;
import it.younify.modules.sgm.service.MatchService;
import it.younify.modules.sgm.service.MatchViewService;
import it.younify.modules.sgm.service.MatchcardViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MatchFacade {
    private final MatchcardViewService matchcardViewService;
    private final MatchService matchService;
    private final MatchViewService matchViewService;
    private final MatchRepository matchRepository;

    public void send(MatchRequest matchRequest) {
        matchService.send(matchRequest);
    }

    public void react(MatchReactionRequest matchReactionRequest) {
        matchService.react(matchReactionRequest);
    }

    public void delete(Long matchId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;
        Optional<Match> opt = matchRepository.findById(matchId);
        if (opt.isPresent()) {
            Match match = opt.get();
            if(match.getSender().getID().equals(user.getID())){
                matchService.deleteMatch(match);
            }
        }
    }

    public Page<MatchcardResponse> getMatchcards(Pageable pageable){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;
        String university = user.getUniversity();
        return matchcardViewService.getMatchcards(university, pageable);
    }

    public Page<MatchcardResponse> getMatchcardsByUni(String university, Pageable pageable){
        return matchcardViewService.getMatchcards(university, pageable);
    }

    public Page<MatchcardResponse> getMatchcardsByUniAndCdl(String university, String cdl, Pageable pageable){
        return matchcardViewService.getMatchcardsByCdl(university, cdl, pageable);
    }

    public Page<MatchPendingResponse> getSentMatches(Pageable pageable){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;
        return matchViewService.getSentPendingMatches(user, pageable);
    }

    public Page<MatchPendingResponse> getReceivedMatches(Pageable pageable){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;
        return matchViewService.getReceivedPendingMatches(user, pageable);
    }

    public Page<MatchAcceptedResponse> getAcceptedMatches(Pageable pageable){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;
        return matchViewService.getSuccessMatch(user, pageable);
    }
}
