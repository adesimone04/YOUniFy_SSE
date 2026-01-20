package it.younify.modules.sgm.service;

import it.younify.core.exception.ResourceNotFoundException;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.UserRepository;
import it.younify.modules.sgm.model.dto.request.MatchRequest;
import it.younify.modules.sgm.model.entity.Match;
import it.younify.modules.sgm.model.entity.Status;
import it.younify.modules.sgm.repository.MatchRepository;
import it.younify.modules.sgn.facade.NotificationFacade;
import it.younify.modules.sgn.model.dto.request.NotificationRequest;
import it.younify.modules.sgn.model.entity.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchSendService {

    private final UserRepository userRepository;
    private final NotificationFacade notificationFacade;
    private final MatchRepository matchRepository;

    @Transactional(rollbackFor = Exception.class)
    public void send(MatchRequest matchRequest) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<User> opt = userRepository.findById(matchRequest.getReceiverID());
        if (opt.isPresent()) {

            User receiver = opt.get();

            Match match = Match.builder()
                    .sender((User) principal)
                    .receiver(opt.get())
                    .status(Status.PENDING)
                    .build();

            matchRepository.save(match);

            NotificationRequest notificationRequest =
                    NotificationRequest.builder()
                            .receiverId(receiver.getID())
                            .type(Type.MATCH)
                            .content("Hai ricevuto una richiesta di match da " +
                                    ((User) principal).getFirstName() + " " + ((User) principal).getLastName())
                            .build();

            notificationFacade.send(notificationRequest);

        } else throw new ResourceNotFoundException("User not found", "USER_NOT_FOUND");
    }
}
