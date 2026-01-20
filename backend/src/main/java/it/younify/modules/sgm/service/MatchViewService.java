package it.younify.modules.sgm.service;

import it.younify.core.service.ImageStorageService;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.UserRepository;
import it.younify.modules.sgm.model.dto.response.MatchAcceptedResponse;
import it.younify.modules.sgm.model.dto.response.MatchPendingResponse;
import it.younify.modules.sgm.model.entity.Match;
import it.younify.modules.sgm.model.entity.Status;
import it.younify.modules.sgm.repository.MatchRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

/**
 * This service handles the business logic for viewing different types of matches for a user.
 * It provides methods to retrieve sent, received, and accepted matches.
 *
 * @see MatchRepository
 * @see ImageStorageService
 * @see UserRepository
 */
@Service
@RequiredArgsConstructor
public class MatchViewService {

    private final MatchRepository matchRepository;
    private final ImageStorageService imageStorageService;
    private final UserRepository userRepository;

    /**
     * Retrieves a paginated list of pending match requests sent by the specified user.
     *
     * @param user The user who sent the match requests.
     * @param pageable The pagination information.
     * @return A paginated list of {@link MatchPendingResponse} objects.
     */
    @Transactional(readOnly = true)
    public Page<MatchPendingResponse> getSentPendingMatches(@NonNull User user, @NonNull Pageable pageable){

        return matchRepository.findAllBySenderAndStatus(user, Status.PENDING, pageable).map(this::toResponse);
    }

    /**
     * Retrieves a paginated list of pending match requests received by the specified user.
     *
     * @param user The user who received the match requests.
     * @param pageable The pagination information.
     * @return A paginated list of {@link MatchPendingResponse} objects.
     */
    @Transactional(readOnly = true)
    public Page<MatchPendingResponse> getReceivedPendingMatches(@NonNull User user, @NonNull Pageable pageable){

        return matchRepository.findAllByReceiverAndStatus(user, Status.PENDING, pageable).map(this::toResponse);
    }

    /**
     * Retrieves a paginated list of accepted matches for the specified user, where the user can be either the sender or the receiver.
     *
     * @param user The user for whom to retrieve accepted matches.
     * @param pageable The pagination information.
     * @return A paginated list of {@link MatchAcceptedResponse} objects.
     */
    @Transactional(readOnly = true)
    public Page<MatchAcceptedResponse> getSuccessMatch(@NonNull User user, @NonNull Pageable pageable){

        return matchRepository.findAllByReceiverAndStatusOrSenderAndStatus(user, user, Status.ACCEPTED, pageable).map(this::toResponseSuccess);
    }


    private MatchPendingResponse toResponse(Match match) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User me = (User) principal;

        User sender = match.getSender();

        if(me.getID().equals(sender.getID())){

            User receiver = userRepository.findById(match.getReceiver().getID()).get();

            return new MatchPendingResponse(

                    match.getID(),
                    receiver.getFirstName() + " " + receiver.getLastName(),
                    imageStorageService.getPresignedUrl(receiver.getProfileImage()),
                    Period.between(receiver.getDateOfBirth(), LocalDate.now()).getYears(),
                    receiver.getUniversity(),
                    receiver.getCdl(),
                    match.getStatus()
            );
        }

        return new MatchPendingResponse(

                match.getID(),
                sender.getFirstName() + " " + sender.getLastName(),
                imageStorageService.getPresignedUrl(sender.getProfileImage()),
                Period.between(sender.getDateOfBirth(), LocalDate.now()).getYears(),
                sender.getUniversity(),
                sender.getCdl(),
                Status.WAITING
        );
    }

    private MatchAcceptedResponse toResponseSuccess(Match match) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User me =  (User) principal;

        User sender = match.getSender();

        if(me.getID().equals(sender.getID())){

            User receiver = userRepository.findById(match.getReceiver().getID()).get();

            return new MatchAcceptedResponse(
                    match.getID(),
                    receiver.getFirstName() + " " + receiver.getLastName(),
                    imageStorageService.getPresignedUrl(receiver.getProfileImage()),
                    Period.between(receiver.getDateOfBirth(), LocalDate.now()).getYears(),
                    receiver.getUniversity(),
                    receiver.getCdl(),
                    receiver.getContactsInfo(),
                    match.getStatus()
            );
        }

        return new MatchAcceptedResponse(
                match.getID(),
                sender.getFirstName() + " " + sender.getLastName(),
                imageStorageService.getPresignedUrl(sender.getProfileImage()),
                Period.between(sender.getDateOfBirth(), LocalDate.now()).getYears(),
                sender.getUniversity(),
                sender.getCdl(),
                sender.getContactsInfo(),
                match.getStatus()
        );
    }
}
