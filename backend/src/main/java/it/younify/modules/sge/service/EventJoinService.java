package it.younify.modules.sge.service;

import it.younify.core.exception.BadRequestException;
import it.younify.core.exception.ResourceNotFoundException;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sge.model.entity.Event;
import it.younify.modules.sge.repository.EventRepository;
import it.younify.modules.sge.repository.ParticipationRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import it.younify.modules.sge.model.entity.Participation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * The {@code EventJoinService} class is responsible for handling a user's request to join an event.
 * It creates a participation record linking the user to the event.
 *
 * @see it.younify.modules.sge.repository.EventRepository
 * @see it.younify.modules.sge.repository.ParticipationRepository
 */
@Service
@RequiredArgsConstructor
public class EventJoinService {

    private final EventRepository eventRepository;
    private final ParticipationRepository participationRepository;

    /**
     * Allows the currently authenticated user to join a specified event.
     * <p>
     * This method is transactional. It first ensures the event exists. If it does, it creates a
     * new {@link Participation} record to link the authenticated {@link User} with the {@link Event}.
     * The underlying database constraints will prevent a user from joining the same event twice.
     *
     * @param eventId The ID of the event to join.
     * @throws BadRequestException if the user already joined the event.
     * @throws ResourceNotFoundException if the event with the given ID cannot be found.
     */
    @Transactional(rollbackFor = Exception.class)
    public void joinEvent(@NonNull Long eventId) {

        Optional<Event> optE = eventRepository.findById(eventId);
        if (optE.isPresent()) {

            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = (User) principal;

            Event event = optE.get();

            Optional<Participation> optP = participationRepository.findByUserAndEvent(user, event);
            if (!optP.isPresent()) {

                if(event.getDateAndTime().isAfter(Instant.now())) {

                    participationRepository.save(
                            Participation.builder()
                                    .event(event)
                                    .user(user)
                                    .build()
                    );
                }
                else throw new BadRequestException("User can't join the event after the event's start time", "CANT_JOIN_EVENT");
            }
            else throw new BadRequestException("User already joined the event", "ALREADY_JOINED");

        }else throw new ResourceNotFoundException("Event not found", "EVENT_NOT_FOUND");
    }
}
