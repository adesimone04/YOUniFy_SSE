package it.younify.modules.sgm.service;

import it.younify.modules.sgm.model.entity.Match;
import it.younify.modules.sgm.repository.MatchRepository;
import it.younify.modules.sgn.facade.NotificationFacade;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service handles the business logic for deleting a match and its associated notifications.
 *
 * @see Match
 * @see MatchRepository
 * @see NotificationFacade
 */
@Service
@RequiredArgsConstructor
public class MatchDeleteService {

    private final NotificationFacade notificationFacade;
    private final MatchRepository matchRepository;

    /**
     * Deletes a specified match and any related notifications.
     * This method first calls the {@link NotificationFacade} to remove any notifications
     * associated with the match, then deletes the match record itself.
     * The operation is transactional and will roll back in case of any error.
     *
     * @param match The {@link Match} entity to be deleted.
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteMatch(@NonNull Match match){

        notificationFacade.deleteMatchNotify(match.getReceiver());
        matchRepository.deleteById(match.getID());
    }
}
