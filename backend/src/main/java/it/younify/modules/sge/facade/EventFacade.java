package it.younify.modules.sge.facade;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.HostRepository;
import it.younify.modules.sge.model.dto.request.EventEditRequest;
import it.younify.modules.sge.model.dto.request.EventPostRequest;
import it.younify.modules.sge.model.dto.response.EventResponse;
import it.younify.modules.sge.repository.EventRepository;
import it.younify.modules.sge.service.EventDeleteService;
import it.younify.modules.sge.service.EventEditService;
import it.younify.modules.sge.service.EventPostService;
import it.younify.modules.sge.service.EventViewService;
import it.younify.modules.sgtr.service.ThreadDeleteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventFacade {
    private final EventPostService eventPostService;
    private final EventDeleteService eventDeleteService;
    private final EventEditService eventEditService;
    private final EventViewService eventViewService;
    private final HostRepository hostRepository;

    public void createEvent(EventPostRequest eventPostRequest) throws IOException {
        eventPostService.post(eventPostRequest);
    }

    public void deleteEvent(Long eventId){
        eventDeleteService.delete(eventId);
    }

    public void editEvent(Long eventId, EventEditRequest eventEditRequest) throws IOException {
        eventEditService.edit(eventId, eventEditRequest);
    }

    public Page<EventResponse> getEventsByDate(Pageable pageable){
        return eventViewService.getEventsbyDate(pageable);
    }

    public Page<EventResponse> getEventsByHost(String hostId, Pageable pageable){

        Optional<Host> opt = hostRepository.findById(hostId);
        if(opt.isPresent()) {
            Host host = opt.get();
            return eventViewService.getEventsbyHost(host, pageable);
        }
        return null;
    }

    public Page<EventResponse> getEventsByCity(String city, Pageable pageable){
        return eventViewService.getEventsbyCity(city, pageable);
    }

    public Page<EventResponse> getEventsJoined(Pageable pageable){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;
        return eventViewService.getEventsbyJoined(user, pageable);
    }

}
