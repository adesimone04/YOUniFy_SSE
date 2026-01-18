package it.younify.modules.sgm.service;

import it.younify.core.exception.ResourceNotFoundException;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.UserRepository;
import it.younify.modules.sgm.model.dto.Request.MatchReactionRequest;
import it.younify.modules.sgm.model.dto.Request.MatchRequest;
import it.younify.modules.sgm.model.entity.Match;
import it.younify.modules.sgm.model.entity.Status;
import it.younify.modules.sgm.repository.MatchRepository;
import it.younify.modules.sgn.facade.NotificationFacade;
import it.younify.modules.sgn.model.dto.request.NotificationRequest;
import it.younify.modules.sgn.model.dto.response.NotificationResponse;
import it.younify.modules.sgn.model.entity.Type;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final UserRepository userRepository;
    private final NotificationFacade notificationFacade;
    private final MatchRepository matchRepository;

    @Transactional
    public void send(MatchRequest matchRequest) {
        Optional<User> opt = userRepository.findById(matchRequest.getReceiverID());
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (opt.isPresent()) {
            User receiver = opt.get();
            Match match = Match.builder()
                    .sender((User) principal)
                    .receiver(receiver)
                    .status(Status.PENDING)
                    .build();
            matchRepository.save(match);

            NotificationRequest notificationRequest = NotificationRequest.builder()
                    .receiverId(receiver.getID())
                    .type(Type.MATCH)
                    .content("hai ricevuto una richiesta di match da " + ((User) principal).getFirstName() + " " + ((User) principal).getLastName())
                    .build();
            notificationFacade.send(notificationRequest);
        }else throw new ResourceNotFoundException("User not found", "USER_NOT_FOUND");
    }

    @Transactional
    public void react(MatchReactionRequest matchReactionRequest){
        Optional<Match> opt = matchRepository.findById(matchReactionRequest.getMatchId());
        if(opt.isPresent()){
            Match match = opt.get();
            if(matchReactionRequest.getStatus().equals(Status.ACCEPTED)){
                match.setStatus(Status.ACCEPTED);
                NotificationRequest notificationRequest = NotificationRequest.builder()
                        .receiverId(match.getSender().getID())
                        .type(Type.MATCH)
                        .content(match.getReceiver().getFirstName() + " " + match.getReceiver().getLastName() +" ha accettato la tua richiesta di match!")
                        .build();
                notificationFacade.send(notificationRequest);
                matchRepository.save(match);
            }else{
                matchRepository.deleteById(matchReactionRequest.getMatchId());
            }
        }else throw new ResourceNotFoundException("Match not found", "MATCH_NOT_FOUND");
    }

    @Transactional
    public void deleteMatch(Match match){
        notificationFacade.deleteMatchNotify(match.getReceiver());
        matchRepository.deleteById(match.getID());
    }
}
