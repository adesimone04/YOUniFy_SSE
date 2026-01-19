package it.younify.modules.sge.service;

import it.younify.core.exception.ResourceNotFoundException;
import it.younify.core.service.ImageStorageService;
import it.younify.core.utils.SecurityUtils;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sge.model.entity.Event;
import it.younify.modules.sge.repository.EventRepository;
import it.younify.modules.sgn.facade.NotificationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.NonNull;

import java.util.Optional;

/**
 * The {@code EventDeleteService} class is responsible for handling the deletion of events.
 * It ensures that only the event's original host or an admin can delete it, cleans up associated resources,
 * and notifies all participants about the cancellation.
 *
 * @see EventRepository
 * @see ImageStorageService
 * @see NotificationFacade
 */
@Service
@RequiredArgsConstructor
public class EventDeleteService {

    private final EventRepository eventRepository;
    private final ImageStorageService imageStorageService;

    /**
     * Deletes an event, its associated poster image, and notifies all participants.
     * <p>
     * This method is transactional and performs several key operations:
     * 1. Verifies that the event exists.
     * 2. Ensures the currently authenticated principal is either the event's host or an admin.
     * 3. Deletes the event's poster from the image storage.
     * 4. Sends a notification to all registered participants about the event cancellation.
     * 5. Removes the event record from the database, which also cascades to delete all associated participations.
     *
     * @param eventId The ID of the event to delete.
     * @throws ResourceNotFoundException if the event with the given ID cannot be found.
     * @throws AccessDeniedException if the authenticated user is not the host of the event or an admin.
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(@NonNull Long eventId) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Host host = (Host) principal;

        Optional<Event> optT = eventRepository.findById(eventId);
        if(optT.isPresent()) {

            Event event = optT.get();
            if(event.getHost().getID().equals(host.getID()) || SecurityUtils.isAdmin()){

                if (event.getPoster() != null) {

                    imageStorageService.deleteImage(event.getPoster());
                }

                //add notify system for joined user
                eventRepository.delete(event);
            }
            else throw new AccessDeniedException("Access is denied for this operation");
        }
        else throw new ResourceNotFoundException("Event not found", "EVENT_NOT_FOUND");
    }
}
