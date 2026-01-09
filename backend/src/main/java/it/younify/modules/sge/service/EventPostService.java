package it.younify.modules.sge.service;

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


import java.io.IOException;
@Service
@RequiredArgsConstructor
public class EventPostService {

    private final EventRepository eventRepository;
    private final ImageStorageService imageStorageService;
    private final String UPLOAD_DIR = "uploads/events";


    @Transactional(rollbackFor = Exception.class)
    public void post(@NotNull EventPostRequest eventPostRequest) throws IOException {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Host host = (Host) principal;

        String posterPath = imageStorageService.uploadImage(eventPostRequest.getPoster(), eventPostRequest.getName(), UPLOAD_DIR);

        eventRepository.save(
                Event.builder()
                        .name(eventPostRequest.getName())
                        .description(eventPostRequest.getDescription())
                        .dateandtime(eventPostRequest.getDateandtime())
                        .street(eventPostRequest.getStreet())
                        .city(eventPostRequest.getCity())
                        .CAP(eventPostRequest.getCAP())
                        .poster(posterPath)
                        .host(host)
                        .build()
        );
    }
}
