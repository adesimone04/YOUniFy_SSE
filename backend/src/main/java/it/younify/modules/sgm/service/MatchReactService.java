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

/**
 * This service handles the business logic for reacting to a match request, either by accepting or rejecting it.
 *
 * @see MatchReactionRequest
 * @see MatchRepository
 * @see NotificationFacade
 */
@Service
@RequiredArgsConstructor
public class MatchReactService {

    private final NotificationFacade notificationFacade;
    private final MatchRepository matchRepository;

    /**
     * Processes a reaction to a match request.
     * If the reaction is an acceptance, the match status is updated to {@link Status#ACCEPTED},
     * and a notification is sent to the original sender.
     * If the reaction is a rejection, the match record is deleted from the database.
     * The operation is transactional and will roll back in case of any error.
     *
     * @param matchReactionRequest The DTO containing the match ID and the reaction status.
     * @throws ResourceNotFoundException if the match specified in the request does not exist.
     */
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
