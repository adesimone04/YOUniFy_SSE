package it.younify.modules.sgn.service;

import it.younify.core.exception.ResourceNotFoundException;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.HostRepository;
import it.younify.modules.sgac.repository.UserRepository;
import it.younify.modules.sgn.model.dto.request.NotificationRequest;
import it.younify.modules.sgn.model.entity.Notification;
import it.younify.modules.sgn.repository.NotificationRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class NotificationSendService {

    private final NotificationRepository notificationRepository;
    private final HostRepository hostRepository;
    private final UserRepository userRepository;

    @Transactional(rollbackFor = Exception.class)
    public void send(@NotNull NotificationRequest notificationRequest) {

        String receiverId = notificationRequest.getReceiverId();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User senderUser = null;
        Host senderHost = null;

        if (principal instanceof User) {
            senderUser = (User) principal;
        } else if (principal instanceof Host) {
            senderHost = (Host) principal;
        } else {
            throw new ResourceNotFoundException("Sender type unknown", "SENDER_ERROR");
        }

        User receiverUser = null;
        Host receiverHost = null;

        if (receiverId.startsWith("U")) {
            receiverUser = userRepository.findById(receiverId)
                    .orElseThrow(() -> new ResourceNotFoundException("User receiver not found", "USER_NOT_FOUND"));
        } else if (receiverId.startsWith("H")) {
            receiverHost = hostRepository.findById(receiverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Host receiver not found", "HOST_NOT_FOUND"));
        } else {
            throw new ResourceNotFoundException("Invalid Receiver ID format", "INVALID_ID");
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
}