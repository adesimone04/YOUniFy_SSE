package it.younify.modules.sge.service;


import it.younify.core.exception.ResourceNotFoundException;
import it.younify.core.service.ImageStorageService;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sge.model.dto.request.EventEditRequest;
import it.younify.modules.sge.model.entity.Event;
import it.younify.modules.sge.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventEditService {
    private final EventRepository eventRepository;
    private final ImageStorageService imageStorageService;
    private final String UPLOAD_DIR = "uploads/events";

    @Transactional(rollbackFor = Exception.class)
    public void edit(Long eventID, EventEditRequest eventEditRequest) throws IOException {
        Optional<Event> opt = eventRepository.findById(eventID);
        if (!opt.isPresent()){
            throw new ResourceNotFoundException("Event not found");
        }

        Event event = opt.get();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Host host = (Host) principal;
        if(!event.getHost().getID().equals(host.getID())){
            throw new ResourceNotFoundException("Host not found");
        }

        if (eventEditRequest.getName() != null && !eventEditRequest.getName().isBlank()) {
            event.setName(eventEditRequest.getName());
        }

        if (eventEditRequest.getDescription() != null && !eventEditRequest.getDescription().isBlank()) {
            event.setDescription(eventEditRequest.getDescription());
        }

        if (eventEditRequest.getDateandtime() != null) {
            event.setDateandtime(eventEditRequest.getDateandtime());
        }

        if (eventEditRequest.getStreet() != null && !eventEditRequest.getStreet().isBlank()) {
            event.setStreet(eventEditRequest.getStreet());
        }

        if (eventEditRequest.getCity() != null && !eventEditRequest.getCity().isBlank()) {
            event.setCity(eventEditRequest.getCity());
        }

        if (eventEditRequest.getCAP() != null) {
            event.setCAP(eventEditRequest.getCAP());
        }

        if (eventEditRequest.getPoster() != null && !event.getPoster().isEmpty()) {
            String newPosterPath = imageStorageService.uploadImage(eventEditRequest.getPoster(), "events",  UPLOAD_DIR);
            event.setPoster(newPosterPath);
        }

        eventRepository.save(event);
    }
}

