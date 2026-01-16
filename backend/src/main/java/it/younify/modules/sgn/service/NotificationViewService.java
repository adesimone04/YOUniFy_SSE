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

@Service
@RequiredArgsConstructor
public class NotificationViewService {

    private final NotificationRepository notificationRepository;
    private final ImageStorageService imageStorageService;

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotificationsHost(Pageable pageable, Host host) {

        return notificationRepository.findAllByReceiverHost(host, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotificationsUser(Pageable pageable, User user) {

        return notificationRepository.findAllByReceiverUser(user, pageable).map(this::toResponse);
    }

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
