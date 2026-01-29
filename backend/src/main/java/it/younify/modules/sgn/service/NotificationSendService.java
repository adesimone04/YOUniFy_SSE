package it.younify.modules.sgn.service;

import it.younify.core.exception.ResourceNotFoundException;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.HostRepository;
import it.younify.modules.sgac.repository.UserRepository;
import it.younify.modules.sgn.model.dto.request.NotificationRequest;
import it.younify.modules.sgn.model.entity.Notification;
import it.younify.modules.sgn.repository.NotificationRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * This service handles the business logic for sending notifications.
 * It is responsible for creating and persisting {@link Notification} entities based on incoming requests.
 * The service determines the sender from the security context and identifies the receiver based on the provided ID.
 *
 * @see Notification
 * @see NotificationRepository
 */
@Service
@RequiredArgsConstructor
public class NotificationSendService {

    private final HostRepository hostRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    /**
     * Creates and saves a new notification.
     * This method identifies the sender from the {@link SecurityContextHolder} and the receiver from the {@code receiverId}
     * in the {@link NotificationRequest}. It supports sending from and to both {@link User} and {@link Host} principals.
     * The operation is transactional and will roll back in case of any error.
     *
     * @param notificationRequest The DTO containing the details for the notification to be sent.
     * @throws AccessDeniedException if the authenticated principal is not a {@link User} or a {@link Host}.
     * @throws ResourceNotFoundException if the specified receiver is not found, or if the receiver ID has an invalid format.
     * @see NotificationRequest
     * @see User
     * @see Host
     */
    @Transactional(rollbackFor = Exception.class)
    public void send(@NonNull NotificationRequest notificationRequest) {

        String receiverId = notificationRequest.getReceiverId();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User senderUser = null;
        Host senderHost = null;

        if (principal instanceof User) {

            senderUser = (User) principal;
        }
        else {

            if (principal instanceof Host) {

                senderHost = (Host) principal;
            }
            else throw new AccessDeniedException("Access is denied for this operation");
        }

        User receiverUser = null;
        Host receiverHost = null;

        if (receiverId.startsWith("U")) {

            receiverUser = userRepository.findById(receiverId)
                    .orElseThrow(() -> new ResourceNotFoundException("User receiver not found", "USER_NOT_FOUND"));
        }
        else{

            if (receiverId.startsWith("H")) {

                receiverHost = hostRepository.findById(receiverId)
                        .orElseThrow(() -> new ResourceNotFoundException("Host receiver not found", "HOST_NOT_FOUND"));
            }
            else throw new ResourceNotFoundException("ID account not exist", "ACCOUNT_NOT_FOUND");
        }


        Notification notification = Notification.builder()
                .senderUser(senderUser)
                .senderHost(senderHost)
                .receiverUser(receiverUser)
                .receiverHost(receiverHost)
                .type(notificationRequest.getType())
                .content(notificationRequest.getContent())
                .date(Instant.now())
                .build();

        notificationRepository.save(notification);
    }


    public void sendReminderEvent(@NonNull NotificationRequest notificationRequest, Host senderHost) {

        String receiverId = notificationRequest.getReceiverId();

        if (receiverId.startsWith("U")) {

            User receiverUser = userRepository.findById(receiverId)
                    .orElseThrow(() -> new ResourceNotFoundException("User receiver not found", "USER_NOT_FOUND"));

            Notification notification = Notification.builder()
                    .senderHost(senderHost)
                    .receiverUser(receiverUser)
                    .type(notificationRequest.getType())
                    .content(notificationRequest.getContent())
                    .date(Instant.now())
                    .build();

            notificationRepository.save(notification);
        }
        else throw new ResourceNotFoundException("ID account not exist", "ACCOUNT_NOT_FOUND");
    }
}