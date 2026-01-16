package it.younify.modules.sgn.service;

import it.younify.modules.sgn.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class NotificationCleanService {

    private final NotificationRepository notificationRepository;

    public NotificationCleanService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanNotifications() {
        Instant daysAgo = Instant.now().minus(30, ChronoUnit.DAYS);

        notificationRepository.deleteAllByDateBefore(daysAgo);
    }
}