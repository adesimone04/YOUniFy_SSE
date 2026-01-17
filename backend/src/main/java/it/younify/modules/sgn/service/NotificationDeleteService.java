package it.younify.modules.sgn.service;


import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgn.model.entity.Type;
import it.younify.modules.sgn.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service handles the business logic for deleting notifications.
 * It provides methods to remove notifications based on specific criteria, such as un-matching.
 *
 * @see it.younify.modules.sgn.model.entity.Notification
 * @see NotificationRepository
 */
@Service
@AllArgsConstructor
public class NotificationDeleteService {

    private final NotificationRepository notificationRepository;

    /**
     * Deletes all "MATCH" type notifications sent from the currently authenticated user to a specified receiver.
     * This method is typically called when a user un-matches another user, ensuring that all related notifications are removed.
     * The operation is transactional and will roll back in case of any error.
     *
     * @param receiverUser The user who received the match notifications to be deleted.
     * @see User
     * @see Type#MATCH
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteMatchNotify(User receiverUser){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        notificationRepository.deleteAllBySenderUserAndReceiverUserAndType((User) principal, receiverUser, Type.MATCH);
    }
}
