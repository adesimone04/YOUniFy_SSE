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

@Service
@RequiredArgsConstructor
public class EventEditService {

    private final EventRepository eventRepository;
    private final ImageStorageService imageStorageService;
    private final String UPLOAD_DIR = "uploads/events";

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
        else throw new ResourceNotFoundException("Event not found");
    }
}

