package it.younify.modules.sgn.service;

import it.younify.core.service.ImageStorageService;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgn.model.dto.response.NotificationResponse;
import it.younify.modules.sgn.model.entity.Notification;
import it.younify.modules.sgn.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service handles the business logic for viewing notifications.
 * It is responsible for fetching notifications from the database and converting them into a client-friendly format.
 * The service provides methods to retrieve notifications for both {@link User} and {@link Host} entities.
 *
 * @see Notification
 * @see NotificationRepository
 * @see NotificationResponse
 */
@Service
@RequiredArgsConstructor
public class NotificationViewService {

    private final NotificationRepository notificationRepository;
    private final ImageStorageService imageStorageService;

    /**
     * Retrieves a paginated list of notifications for a specific {@link Host}.
     * This method fetches notifications where the given host is the receiver and maps them to {@link NotificationResponse} objects.
     * The operation is performed in a read-only transaction.
     *
     * @param pageable The pagination information.
     * @param host The host for whom to retrieve notifications.
     * @return A {@link Page} of {@link NotificationResponse} objects.
     * @see Host
     * @see Page
     * @see NotificationResponse
     */
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotificationsHost(Pageable pageable, Host host) {

        return notificationRepository.findAllByReceiverHost(host, pageable).map(this::toResponse);
    }

    /**
     * Retrieves a paginated list of notifications for a specific {@link User}.
     * This method fetches notifications where the given user is the receiver and maps them to {@link NotificationResponse} objects.
     * The operation is performed in a read-only transaction.
     *
     * @param pageable The pagination information.
     * @param user The user for whom to retrieve notifications.
     * @return A {@link Page} of {@link NotificationResponse} objects.
     * @see User
     * @see Page
     * @see NotificationResponse
     */
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotificationsUser(Pageable pageable, User user) {

        return notificationRepository.findAllByReceiverUser(user, pageable).map(this::toResponse);
    }

    /**
     * Converts a {@link Notification} entity to a {@link NotificationResponse} DTO.
     * This method resolves the sender's name and profile photo URL, regardless of whether the sender is a {@link User} or a {@link Host}.
     * Note: The sender's photo URL is mapped to the {@code receiverName} field in the {@link NotificationResponse}.
     *
     * @param notification The notification entity to convert.
     * @return The corresponding {@link NotificationResponse} DTO.
     * @see Notification
     * @see NotificationResponse
     */
    private NotificationResponse toResponse(Notification notification) {

        String senderName = "";
        String senderPhoto = "";

        if(notification.getSenderHost() != null) {

            senderName = notification.getSenderHost().getHostName();
            senderPhoto = imageStorageService.getPresignedUrl(notification.getSenderHost().getProfileImage());
        }
        else{

            senderName = notification.getSenderUser().getFirstName() + " " +  notification.getSenderUser().getLastName();
            senderPhoto = imageStorageService.getPresignedUrl(notification.getSenderUser().getProfileImage());
        }

        return new NotificationResponse(
                notification.getID(),
                senderName,
                senderPhoto,
                notification.getType(),
                notification.getContent(),
                notification.getDate()
        );
    }
}
