package it.younify.modules.sge.service;

import it.younify.core.exception.ResourceAlreadyExistsException;
import it.younify.core.service.ImageStorageService;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sge.model.dto.request.EventPostRequest;
import it.younify.modules.sge.model.entity.Event;
import it.younify.modules.sge.repository.EventRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * The {@code EventPostService} class is responsible for handling the creation of new events.
 * It orchestrates the validation, image storage, and persistence of event data.
 *
 * @see it.younify.modules.sge.repository.EventRepository
 * @see it.younify.core.service.ImageStorageService
 */
@Service
@RequiredArgsConstructor
public class EventPostService {

    private final EventRepository eventRepository;
    private final ImageStorageService imageStorageService;
    private final String UPLOAD_DIR = "uploads/events";

    /**
     * Creates and persists a new event from the provided request data.
     * <p>
     * This method is transactional. It first checks if an identical event (same host, name, and date)
     * already exists to prevent duplicates. If not, it uploads the event poster image using the
     * {@link ImageStorageService} and then saves the new {@link Event} to the MinIO Object Storage.
     *
     * @param eventPostRequest The DTO containing all the data for the new event.
     * @throws ResourceAlreadyExistsException if an identical event from the same host already exists.
     */
    @Transactional(rollbackFor = Exception.class)
    public void post(@NotNull EventPostRequest eventPostRequest) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Host host = (Host) principal;

        Optional<Event> event = eventRepository.findExistingEvent(host, eventPostRequest.getDateAndTime(), eventPostRequest.getName());
        if(!event.isPresent()) {

            String posterPath = imageStorageService.uploadImage(eventPostRequest.getPoster(), UUID.randomUUID().toString(), UPLOAD_DIR);

            eventRepository.save(
                    Event.builder()
                            .name(eventPostRequest.getName())
                            .description(eventPostRequest.getDescription())
                            .dateAndTime(eventPostRequest.getDateAndTime())
                            .street(eventPostRequest.getStreet())
                            .city(eventPostRequest.getCity())
                            .CAP(eventPostRequest.getCAP())
                            .poster(posterPath)
                            .host(host)
                            .build()
            );
        }
        else throw new ResourceAlreadyExistsException("The host has already posted this event for this day and time", "EVENT_ALREADY_EXISTS");
    }
}
