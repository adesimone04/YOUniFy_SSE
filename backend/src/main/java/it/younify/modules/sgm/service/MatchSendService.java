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

/**
 * This service handles the business logic for sending match requests between users.
 * It creates a new match record and sends a notification to the receiver.
 *
 * @see MatchRequest
 * @see Match
 * @see MatchRepository
 * @see NotificationFacade
 */
@Service
@RequiredArgsConstructor
public class MatchSendService {

    private final UserRepository userRepository;
    private final NotificationFacade notificationFacade;
    private final MatchRepository matchRepository;

    /**
     * Sends a match request from the currently authenticated user to a specified receiver.
     * This method creates a new {@link Match} entity with {@link Status#PENDING} and saves it.
     * A notification is then sent to the receiver to inform them of the new match request.
     * The operation is transactional and will roll back in case of any error.
     *
     * @param matchRequest The DTO containing the ID of the user to whom the match request is being sent.
     * @throws ResourceNotFoundException if the receiver user specified in the request does not exist.
     */
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
