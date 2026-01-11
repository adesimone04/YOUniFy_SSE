package it.younify.modules.sge.facade;

import it.younify.core.exception.ResourceNotFoundException;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.HostRepository;
import it.younify.modules.sge.model.dto.request.EventEditRequest;
import it.younify.modules.sge.model.dto.request.EventPostRequest;
import it.younify.modules.sge.model.dto.response.EventResponse;
import it.younify.modules.sge.service.EventDeleteService;
import it.younify.modules.sge.service.EventEditService;
import it.younify.modules.sge.service.EventPostService;
import it.younify.modules.sge.service.EventViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventFacade {

    private final EventPostService eventPostService;
    private final EventDeleteService eventDeleteService;
    private final EventEditService eventEditService;
    private final EventViewService eventViewService;
    private final HostRepository hostRepository;

    public void createEvent(EventPostRequest eventPostRequest){

        eventPostService.post(eventPostRequest);
    }

    public void deleteEvent(Long eventId){

        eventDeleteService.delete(eventId);
    }

    public void editEvent(Long eventId, EventEditRequest eventEditRequest){

        eventEditService.edit(eventId, eventEditRequest);
    }

    public Page<EventResponse> getEventsByDate(Pageable pageable){

        return eventViewService.getEventsByDate(pageable);
    }

    public Page<EventResponse> getEventsByHost(String hostId, Pageable pageable){

        Optional<Host> opt = hostRepository.findById(hostId);
        if(opt.isPresent()){

            return eventViewService.getEventsByHost(opt.get(), pageable);
        }

        throw new ResourceNotFoundException("Host not found");
    }

    public Page<EventResponse> getEventsByCity(String city, Pageable pageable){

        return eventViewService.getEventsByCity(city, pageable);
    }

    public Page<EventResponse> getEventsJoined(Pageable pageable){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return eventViewService.getEventsByJoined((User) principal, pageable);
    }
}
