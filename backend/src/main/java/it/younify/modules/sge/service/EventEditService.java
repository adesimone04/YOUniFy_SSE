package it.younify.modules.sge.service;


import it.younify.core.exception.ResourceNotFoundException;
import it.younify.core.service.ImageStorageService;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sge.model.dto.request.EventEditRequest;
import it.younify.modules.sge.model.entity.Event;
import it.younify.modules.sge.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * The {@code EventEditService} class is responsible for handling the modification of existing events.
 * It ensures that only the event's original host can edit it and manages the optional replacement of the event poster.
 *
 * @see it.younify.modules.sge.repository.EventRepository
 * @see it.younify.core.service.ImageStorageService
 */
@Service
@RequiredArgsConstructor
public class EventEditService {

    private final EventRepository eventRepository;
    private final ImageStorageService imageStorageService;
    private final String UPLOAD_DIR = "uploads/events";

    /**
     * Edits an existing event with the provided data.
     * <p>
     * This method is transactional. It first verifies that the event exists and that the currently
     * authenticated principal is the host who created the event. If authorized, it updates the event's
     * details. If a new poster image is provided, it replaces the old one using the same file name to
     * avoid storing orphaned images.
     *
     * @param eventID The ID of the event to edit.
     * @param eventEditRequest The DTO containing the updated event data.
     * @throws ResourceNotFoundException if the event with the given ID cannot be found.
     * @throws AccessDeniedException if the authenticated user is not the host of the event.
     */
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long eventID, EventEditRequest eventEditRequest) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Host host = (Host) principal;

        Optional<Event> opt = eventRepository.findById(eventID);
        if(opt.isPresent()){

            Event event = opt.get();
            if(event.getHost().getID().equals(host.getID())){

                event.setName(eventEditRequest.getName());
                event.setDescription(eventEditRequest.getDescription());
                event.setDateAndTime(eventEditRequest.getDateAndTime());
                event.setStreet(eventEditRequest.getStreet());
                event.setCity(eventEditRequest.getCity());
                event.setCAP(eventEditRequest.getCAP());

                MultipartFile file = eventEditRequest.getPoster();
                if(file != null && !file.isEmpty()){

                    String fileName = event.getPoster();
                    fileName = fileName.substring(fileName.lastIndexOf("/"));
                    event.setPoster(imageStorageService.uploadImage(file, fileName, UPLOAD_DIR));
                }

                // add notification system (SGN) for joined user when information are changed
                eventRepository.save(event);
            }
            else throw new AccessDeniedException("Access is denied for this operation");
        }
        else throw new ResourceNotFoundException("Event not found", "EVENT_NOT_FOUND");
    }
}
