package it.younify.modules.sgn.facade;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgn.model.dto.request.NotificationRequest;
import it.younify.modules.sgn.model.dto.response.NotificationResponse;
import it.younify.modules.sgn.service.NotificationSendService;
import it.younify.modules.sgn.service.NotificationViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationFacade {

    private final NotificationViewService notificationViewService;
    private final NotificationSendService notificationSendService;

    public void send(NotificationRequest notificationRequest) {

        notificationSendService.send(notificationRequest);
    }

    public Page<NotificationResponse> getNotify(Pageable pageable) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal instanceof User) {

            return notificationViewService.getNotificationsUser(pageable, (User) principal);
        }

        return notificationViewService.getNotificationsHost(pageable, (Host) principal);
    }
}
