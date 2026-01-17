package it.younify.modules.sgn.facade;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgn.model.dto.request.NotificationRequest;
import it.younify.modules.sgn.model.dto.response.NotificationResponse;
import it.younify.modules.sgn.service.NotificationDeleteService;
import it.younify.modules.sgn.service.NotificationSendService;
import it.younify.modules.sgn.service.NotificationViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * The NotificationFacade class provides a simplified interface to the notification module's functionality.
 * It orchestrates the {@link NotificationSendService} and {@link NotificationViewService} to handle sending and retrieving notifications.
 * This facade is the primary entry point for all notification-related operations from the controller layer.
 *
 * @see NotificationSendService
 * @see NotificationViewService
 */
@Component
@RequiredArgsConstructor
public class NotificationFacade {

    private final NotificationViewService notificationViewService;
    private final NotificationSendService notificationSendService;
    private final NotificationDeleteService notificationDeleteService;

    /**
     * Sends a notification based on the provided request.
     * This method delegates the sending logic to the {@link NotificationSendService}.
     *
     * @param notificationRequest The request object containing the details of the notification to be sent.
     * @see NotificationRequest
     */
    public void send(NotificationRequest notificationRequest) {

        notificationSendService.send(notificationRequest);
    }

    /**
     * Retrieves a paginated list of notifications for the currently authenticated user or host.
     * This method determines the type of the principal (User or Host) and delegates the retrieval logic
     * to the appropriate method in the {@link NotificationViewService}.
     *
     * @param pageable The pagination information.
     * @return A {@link Page} of {@link NotificationResponse} objects for the authenticated principal.
     * @see Page
     * @see NotificationResponse
     * @see User
     * @see Host
     */
    public Page<NotificationResponse> getNotify(Pageable pageable) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal instanceof User) {

            return notificationViewService.getNotificationsUser(pageable, (User) principal);
        }

        return notificationViewService.getNotificationsHost(pageable, (Host) principal);
    }

    /**
     * Deletes all "MATCH" type notifications sent from the currently authenticated user to a specified receiver.
     * This method delegates the deletion logic to the {@link NotificationDeleteService}.
     *
     * @param receiverUser The user who received the match notifications to be deleted.
     * @see NotificationDeleteService#deleteMatchNotify(User)
     */
    public void deleteMatchNotify(User receiverUser){

        notificationDeleteService.deleteMatchNotify(receiverUser);
    }
}
