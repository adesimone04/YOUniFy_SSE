package it.younify.modules.sge.service;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sge.model.entity.Participation;
import it.younify.modules.sge.repository.ParticipationRepository;
import it.younify.modules.sgn.facade.NotificationFacade;
import it.younify.modules.sgn.model.dto.request.NotificationRequest;
import it.younify.modules.sgn.model.entity.Type;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventReminderService {

    private final ParticipationRepository participationRepository;
    private final NotificationFacade notificationFacade;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void sendReminder(){
        Instant now = Instant.now();
        Instant windowStart = now.plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MINUTES);
        Instant windowEnd = windowStart.plus(1, ChronoUnit.MINUTES);

        List<Participation> participationsList = participationRepository.findAllByEventDateAndTimeBetween(windowStart, windowEnd);
        if(participationsList.isEmpty()){
            return;
        }

        participationsList.parallelStream().forEach(p -> {
            NotificationRequest notificationRequest = NotificationRequest.builder()
                    .receiverId(p.getUser().getID())
                    .type(Type.EVENT)
                    .content(p.getEvent().getName() + " inizia tra 1h!")
                    .build();
            notificationFacade.sendEventReminder(notificationRequest, p.getEvent().getHost());
        });
    }
}
