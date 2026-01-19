package it.younify.modules.sge.service;

import it.younify.modules.sge.model.entity.Participation;
import it.younify.modules.sge.repository.ParticipationRepository;
import it.younify.modules.sgn.facade.NotificationFacade;
import it.younify.modules.sgn.model.dto.request.NotificationRequest;
import it.younify.modules.sgn.model.entity.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * The {@code EventReminderService} is responsible for sending reminder notifications to users
 * for events they are participating in. It uses a scheduled task to check for upcoming events
 * and triggers notifications accordingly.
 *
 * @see ParticipationRepository
 * @see NotificationFacade
 */
@Service
@RequiredArgsConstructor
public class EventReminderService {

    private final ParticipationRepository participationRepository;
    private final NotificationFacade notificationFacade;


    /**
     * This method is scheduled to run every 10 minutes to send reminders for upcoming events.
     * It fetches all participations for events that are scheduled to start within the next hour
     * (specifically, in a 10-minute window beginning one hour from now).
     * It processes these participations in pages to handle large volumes efficiently.
     *
     * @see Scheduled
     */
    @Scheduled(cron = "0 */10 * * * *")
    public void sendReminder(){

        Instant now = Instant.now();

        Instant windowStart = now.plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MINUTES);
        Instant windowEnd = windowStart.plus(10, ChronoUnit.MINUTES);

        int pageNumber = 0;
        int pageSize = 100;
        Page<Participation> page;

        do {

            page = participationRepository.findAllByEventDateAndTimeBetween(windowStart, windowEnd, PageRequest.of(pageNumber, pageSize));

            for (Participation p : page.getContent()) {

                sendSingleNotification(p);
            }

            pageNumber++;

        } while (page.hasNext());
    }


    /**
     * Sends a single event reminder notification to a user.
     * This helper method constructs and sends a notification for a specific participation.
     *
     * @param p The {@link Participation} for which to send a reminder.
     */
    private void sendSingleNotification(Participation p) {

        NotificationRequest notificationRequest =
                NotificationRequest.builder()
                    .receiverId(p.getUser().getID())
                    .type(Type.EVENT)
                    .content(p.getEvent().getName() + " inizia tra 1h!")
                    .build();

        notificationFacade.sendEventReminder(notificationRequest, p.getEvent().getHost());
    }
}
