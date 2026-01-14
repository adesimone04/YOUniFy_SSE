package it.younify.modules.sge.facade;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
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

/**
 * The {@code EventFacade} class acts as a high-level entry point for all event-related operations.
 * It simplifies the interaction with the underlying services by orchestrating them to perform
 * complex business logic such as creating, editing, deleting, and viewing events.
 */
@Component
@RequiredArgsConstructor
public class EventFacade {

    private final EventPostService eventPostService;
    private final EventDeleteService eventDeleteService;
    private final EventEditService eventEditService;
    private final EventViewService eventViewService;
    private final EventJoinService eventJoinService;
    private final EventUnjoinService eventUnjoinService;
    private final EventRepository eventRepository;

    /**
     * Creates a new event.
     *
     * @param eventPostRequest The request containing the event's data.
     */
    public void createEvent(EventPostRequest eventPostRequest){

        eventPostService.post(eventPostRequest);
    }

    /**
     * Deletes an event by its ID.
     *
     * @param eventId The ID of the event to delete.
     */
    public void deleteEvent(Long eventId){

        eventDeleteService.delete(eventId);
    }

    /**
     * Edits an existing event.
     *
     * @param eventId The ID of the event to edit.
     * @param eventEditRequest The request containing the updated event data.
     */
    public void editEvent(Long eventId, EventEditRequest eventEditRequest){

        eventEditService.edit(eventId, eventEditRequest);
    }

    /**
     * Allows the authenticated user to join an event.
     *
     * @param eventId The ID of the event to join.
     */
    public void joinEvent(Long eventId){

        eventJoinService.joinEvent(eventId);
    }

    /**
     * Allows the authenticated user to unjoin an event.
     *
     * @param eventId The ID of the event to unjoin.
     */
    public void unjoinEvent(Long eventId){

        eventUnjoinService.unjoinEvent(eventId);
    }

    /**
     * Retrieves a paginated list of all events, sorted by date.
     *
     * @param pageable The pagination information.
     * @return A paginated list of events.
     */
    public Page<EventResponse> getEventsByDate(Pageable pageable){

        return eventViewService.getEventsByDate(pageable);
    }

    /**
     * Retrieves a paginated list of events for the currently authenticated host.
     *
     * @param pageable The pagination information.
     * @return A paginated list of the host's events.
     */
    public Page<EventResponse> getEventsByHost(Pageable pageable){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return eventViewService.getEventsByHost((Host) principal, pageable);
    }

    /**
     * Retrieves a paginated list of events filtered by a specific city.
     *
     * @param city The city to filter events by.
     * @param pageable The pagination information.
     * @return A paginated list of events in the specified city.
     */
    public Page<EventResponse> getEventsByCity(String city, Pageable pageable){

        return eventViewService.getEventsByCity(city, pageable);
    }

    /**
     * Retrieves a paginated list of events that the currently authenticated user has joined.
     *
     * @param pageable The pagination information.
     * @return A paginated list of joined events.
     */
    public Page<EventResponse> getEventsJoined(Pageable pageable){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return eventViewService.getEventsByJoined((User) principal, pageable);
    }

    /**
     * Retrieves a list of all unique cities where events are being held.
     *
     * @return A list of distinct city names.
     */
    public List<String> getCities(){

        return eventRepository.findDistinctCities();
    }
}
