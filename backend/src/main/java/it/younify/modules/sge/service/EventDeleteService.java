package it.younify.modules.sge.service;

import it.younify.core.exception.ResourceNotFoundException;
import it.younify.core.service.ImageStorageService;
import it.younify.core.utils.SecurityUtils;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sge.model.entity.Event;
import it.younify.modules.sge.repository.EventRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * The {@code EventDeleteService} class is responsible for handling the deletion of events.
 * It ensures that only the event's original host can delete it and cleans up associated resources, such as the event poster.
 *
 * @see it.younify.modules.sge.repository.EventRepository
 * @see it.younify.core.service.ImageStorageService
 */
@Service
@RequiredArgsConstructor
public class EventDeleteService {

    private final EventRepository eventRepository;
    private final ImageStorageService imageStorageService;

    /**
     * Deletes an event and its associated poster image.
     * <p>
     * This method is transactional. It first verifies that the event exists and that the currently
     * authenticated principal is the host who created the event. If authorized, it deletes the
     * event's poster from storage and then removes the event record from the database.
     *
     * @param eventId The ID of the event to delete.
     * @throws ResourceNotFoundException if the event with the given ID cannot be found.
     * @throws AccessDeniedException if the authenticated user is not the host of the event.
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(@NotNull Long eventId) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Host host = (Host) principal;

        Optional<Event> optT = eventRepository.findById(eventId);
        if(optT.isPresent()) {

            Event event = optT.get();
            if(event.getHost().getID().equals(host.getID()) || SecurityUtils.isAdmin()){

                if (event.getPoster() != null) {
                    imageStorageService.deleteImage(event.getPoster());
                }
                eventRepository.delete(event);
            }
            else throw new AccessDeniedException("Access is denied for this operation");
        }
        else throw new ResourceNotFoundException("Event not found", "EVENT_NOT_FOUND");
    }
}
