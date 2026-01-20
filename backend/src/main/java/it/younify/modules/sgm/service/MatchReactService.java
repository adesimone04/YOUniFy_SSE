package it.younify.modules.sgm.service;

import it.younify.core.exception.ResourceNotFoundException;
import it.younify.modules.sgm.model.dto.request.MatchReactionRequest;
import it.younify.modules.sgm.model.entity.Match;
import it.younify.modules.sgm.model.entity.Status;
import it.younify.modules.sgm.repository.MatchRepository;
import it.younify.modules.sgn.facade.NotificationFacade;
import it.younify.modules.sgn.model.dto.request.NotificationRequest;
import it.younify.modules.sgn.model.entity.Type;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchReactService {

    private final NotificationFacade notificationFacade;
    private final MatchRepository matchRepository;

    @Transactional(rollbackFor = Exception.class)
    public void react(@NonNull MatchReactionRequest matchReactionRequest){

        Optional<Match> opt = matchRepository.findById(matchReactionRequest.getMatchId());
        if(opt.isPresent()){

            Match match = opt.get();

            if(matchReactionRequest.getStatus().equals(Status.ACCEPTED)){

                match.setStatus(Status.ACCEPTED);

                NotificationRequest notificationRequest =
                        NotificationRequest.builder()
                                .receiverId(match.getSender().getID())
                                .type(Type.MATCH)
                                .content(match.getReceiver().getFirstName() +
                                        " " + match.getReceiver().getLastName() + " ha accettato la tua richiesta di match!")
                                .build();

                notificationFacade.send(notificationRequest);
                matchRepository.save(match);
            }
            else matchRepository.deleteById(matchReactionRequest.getMatchId());
        }
        else throw new ResourceNotFoundException("Match not found", "MATCH_NOT_FOUND");
    }
}
