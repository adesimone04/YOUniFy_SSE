package it.younify.modules.sge.service;

import it.younify.core.exception.BadRequestException;
import it.younify.core.exception.ResourceNotFoundException;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.UserRepository;
import it.younify.modules.sge.model.entity.Event;
import it.younify.modules.sge.model.entity.Participation;
import it.younify.modules.sge.repository.EventRepository;
import it.younify.modules.sge.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class EventUnjoinService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRepository participationRepository;

    @Transactional(rollbackFor = Exception.class)
    public void unjoinEvent(Long eventId) {

        Optional<Event> optE = eventRepository.findById(eventId);
        if (optE.isPresent()) {

            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = (User) principal;

            Event event = optE.get();

            Optional<Participation> optP = participationRepository.findByUserAndEvent(user, event);
            if (optP.isPresent()) {

                if(event.getDateAndTime().isAfter(Instant.now())){

                    participationRepository.delete(optP.get());
                }
                else throw new BadRequestException("The user can't unjoin the event after the event's start time.", "CANT_UNJOIN_EVENT");
            }
            else throw new BadRequestException("The user is not already participating in this event.", "ALREADY_NOT_JOINED");

        }else throw new ResourceNotFoundException("Event not found", "EVENT_NOT_FOUND");
    }
}
