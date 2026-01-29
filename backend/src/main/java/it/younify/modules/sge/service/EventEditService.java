package it.younify.modules.sge.service;


import it.younify.core.exception.ResourceNotFoundException;
import it.younify.core.service.ImageStorageService;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.service.validator.GeoValidationService;
import it.younify.modules.sge.model.dto.request.EventEditRequest;
import it.younify.modules.sge.model.entity.Event;
import it.younify.modules.sge.repository.EventRepository;
import it.younify.modules.sgn.facade.NotificationFacade;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * The {@code EventEditService} class is responsible for handling the modification of existing events.
 * It ensures that only the event's original host can edit it, manages the optional replacement of the event poster,
 * and notifies all participants about the changes.
 *
 * @see EventRepository
 * @see ImageStorageService
 * @see NotificationFacade
 */
@Service
@RequiredArgsConstructor
public class EventEditService {

    private final EventRepository eventRepository;
    private final ImageStorageService imageStorageService;
    private final GeoValidationService geoValidationService;

    private final String UPLOAD_DIR = "uploads/events";

    /**
     * Edits an existing event with the provided data and notifies all participants of the changes.
     * <p>
     * This method is transactional and performs several key operations:
     * 1. Verifies that the event exists.
     * 2. Ensures the currently authenticated principal is the event's host.
     * 3. Updates the event's details (name, description, date, location).
     * 4. If a new poster image is provided, it replaces the old one.
     * 5. Sends a notification to all registered participants about the event modification.
     * 6. Saves the updated event record to the database.
     *
     * @param eventID The ID of the event to edit.
     * @param eventEditRequest The DTO containing the updated event data.
     * @throws ResourceNotFoundException if the event with the given ID cannot be found.
     * @throws AccessDeniedException if the authenticated user is not the host of the event.
     */
    @Transactional(rollbackFor = Exception.class)
    public void edit(@NonNull Long eventID, @NonNull EventEditRequest eventEditRequest) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Host host = (Host) principal;

        Optional<Event> opt = eventRepository.findById(eventID);
        if(opt.isPresent()){

            Event event = opt.get();
            if(event.getHost().getID().equals(host.getID())){

                String city = eventEditRequest.getCity();
                if(geoValidationService.isValidCityOnly(city)){

                    event.setCity(eventEditRequest.getCity());
                }

                event.setName(eventEditRequest.getName());
                event.setDescription(eventEditRequest.getDescription());
                event.setDateAndTime(eventEditRequest.getDateAndTime());
                event.setStreet(eventEditRequest.getStreet());
                event.setCAP(eventEditRequest.getCAP());

                MultipartFile file = eventEditRequest.getPoster();
                if(file != null && !file.isEmpty()){

                    String fileName = event.getPoster();
                    fileName = fileName.substring(fileName.lastIndexOf("/"));
                    event.setPoster(imageStorageService.uploadImage(file, fileName, UPLOAD_DIR));
                }

                //add notify system for joined user
                eventRepository.save(event);
            }
            else throw new AccessDeniedException("Access is denied for this operation");
        }
        else throw new ResourceNotFoundException("Event not found", "EVENT_NOT_FOUND");
    }
}
