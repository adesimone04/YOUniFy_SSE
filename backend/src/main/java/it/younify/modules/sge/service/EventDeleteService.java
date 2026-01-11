package it.younify.modules.sge.service;

import it.younify.core.exception.ResourceNotFoundException;
import it.younify.core.service.ImageStorageService;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sge.model.entity.Event;
import it.younify.modules.sge.repository.EventRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventDeleteService {

    private final EventRepository eventRepository;
    private final ImageStorageService imageStorageService;

    @Transactional(rollbackFor = Exception.class)
    public void delete(@NotNull Long eventId) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Host host = (Host) principal;

        Optional<Event> optT = eventRepository.findById(eventId);
        if(optT.isPresent()) {

            Event event = optT.get();
            if(event.getHost().getID().equals(host.getID())) {

                if (event.getPoster() != null) {
                    imageStorageService.deleteImage(event.getPoster());
                }
                eventRepository.delete(event);
            }
        }
        else throw new ResourceNotFoundException("Event not found", "EVENT_NOT_FOUND");
    }
}
