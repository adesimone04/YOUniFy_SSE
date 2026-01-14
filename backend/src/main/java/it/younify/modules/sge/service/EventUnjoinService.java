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


/**
 * The {@code EventUnjoinService} class handles the logic for a user to leave an event they have previously joined.
 * It includes business rules, such as preventing users from leaving an event after it has already started.
 *
 * @see it.younify.modules.sge.repository.ParticipationRepository
 * @see it.younify.modules.sge.repository.EventRepository
 */
@Service
@RequiredArgsConstructor
public class EventUnjoinService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRepository participationRepository;

    /**
     * Allows the currently authenticated user to unjoin a specified event.
     * <p>
     * This method is transactional. It first verifies that the user is actually participating in the event.
     * Crucially, it also checks if the event's start time is still in the future. A user cannot
     * unjoin an event after it has already started.
     *
     * @param eventId The ID of the event to unjoin.
     * @throws ResourceNotFoundException if the event with the given ID cannot be found.
     * @throws BadRequestException if the user is not participating in the event or if the event has already started.
     */
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
