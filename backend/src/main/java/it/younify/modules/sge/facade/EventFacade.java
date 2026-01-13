package it.younify.modules.sge.facade;

import it.younify.core.exception.ResourceNotFoundException;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.HostRepository;
import it.younify.modules.sgac.repository.UserRepository;
import it.younify.modules.sge.model.dto.request.EventEditRequest;
import it.younify.modules.sge.model.dto.request.EventPostRequest;
import it.younify.modules.sge.model.dto.response.EventResponse;
import it.younify.modules.sge.repository.EventRepository;
import it.younify.modules.sge.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventFacade {

    private final EventPostService eventPostService;
    private final EventDeleteService eventDeleteService;
    private final EventEditService eventEditService;
    private final EventViewService eventViewService;
    private final EventJoinService eventJoinService;
    private final HostRepository hostRepository;
    private final EventRepository eventRepository;

    public void createEvent(EventPostRequest eventPostRequest){

        eventPostService.post(eventPostRequest);
    }

    public void deleteEvent(Long eventId){

        eventDeleteService.delete(eventId);
    }

    public void editEvent(Long eventId, EventEditRequest eventEditRequest){

        eventEditService.edit(eventId, eventEditRequest);
    }

    public void joinEvent(Long EventId){
        eventJoinService.joinEvent(EventId);
    }

    public Page<EventResponse> getEventsByDate(Pageable pageable){

        return eventViewService.getEventsByDate(pageable);
    }

    public Page<EventResponse> getEventsByHost(Pageable pageable){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            return eventViewService.getEventsByHost((Host) principal, pageable);

    }

    public Page<EventResponse> getEventsByCity(String city, Pageable pageable){

        return eventViewService.getEventsByCity(city, pageable);
    }

    public Page<EventResponse> getEventsJoined(Pageable pageable){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return eventViewService.getEventsByJoined((User) principal, pageable);
    }

    public List<String> getCities(){
        return eventRepository.findDistinctCities();
    }
}
