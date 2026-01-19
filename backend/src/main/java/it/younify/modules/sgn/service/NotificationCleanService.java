package it.younify.modules.sgn.service;

import it.younify.modules.sgn.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * This service is responsible for periodically cleaning up old notifications from the database.
 * It uses a scheduled task to automatically delete notifications that are older than a specified period.
 *
 * @see NotificationRepository
 */
@Service
public class NotificationCleanService {

    private final NotificationRepository notificationRepository;

    public NotificationCleanService(NotificationRepository notificationRepository) {

        this.notificationRepository = notificationRepository;
    }

    /**
     * This method is scheduled to run daily at midnight.
     * It deletes all notifications from the database that are older than 30 days.
     * The operation is transactional and will roll back in case of any error.
     *
     * @see Scheduled
     * @see Transactional
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanNotifications() {

        Instant daysAgo = Instant.now().minus(30, ChronoUnit.DAYS);
        notificationRepository.deleteAllByDateBefore(daysAgo);
    }
}