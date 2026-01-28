package it.younify.modules.sge.service;

import it.younify.core.service.ImageStorageService;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sge.model.dto.response.EventResponse;
import it.younify.modules.sge.model.dto.response.JoinedUserResponse;
import it.younify.modules.sge.model.entity.Event;
import it.younify.modules.sge.repository.EventRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * The {@code EventViewService} class provides methods for querying and retrieving event data in a client-friendly format.
 * It fetches events based on different criteria and transforms them into {@link EventResponse} DTOs.
 *
 * @see it.younify.modules.sge.repository.EventRepository
 * @see it.younify.core.service.ImageStorageService
 */
@Service
@RequiredArgsConstructor
public class EventViewService {

    private final ImageStorageService imageStorageService;
    private final EventRepository eventRepository;

    /**
     * Retrieves a paginated list of all events, sorted by date.
     *
     * @param pageable The pagination information.
     * @return A paginated list of {@link EventResponse} objects.
     */
    @Transactional(readOnly = true)
    public Page<EventResponse> getEventsByDate(@NonNull Pageable pageable){

        return eventRepository.findAllByOrderByDateAndTimeAsc(pageable).map(this::toResponse);
    }

    /**
     * Retrieves a paginated list of events created by a specific host.
     *
     * @param host The host whose events are to be retrieved.
     * @param pageable The pagination information.
     * @return A paginated list of {@link EventResponse} objects.
     */
    @Transactional(readOnly = true)
    public Page<EventResponse> getEventsByHost(@NonNull Host host, @NonNull Pageable pageable){

        return eventRepository.findAllByHostOrderByDateAndTimeAsc(host, pageable).map(this::toResponse);
    }

    /**
     * Retrieves a paginated list of events located in a specific city.
     *
     * @param city The city to filter events by.
     * @param pageable The pagination information.
     * @return A paginated list of {@link EventResponse} objects.
     */
    @Transactional(readOnly = true)
    public Page<EventResponse> getEventsByCity(@NonNull String city, @NonNull Pageable pageable){

        return eventRepository.findAllByCityOrderByDateAndTimeAsc(city, pageable).map(this::toResponse);
    }

    /**
     * Retrieves a paginated list of events that a specific user has joined.
     *
     * @param user The user whose joined events are to be retrieved.
     * @param pageable The pagination information.
     * @return A paginated list of {@link EventResponse} objects.
     */
    @Transactional(readOnly = true)
    public Page<EventResponse> getEventsByJoined(@NonNull User user, @NonNull Pageable pageable){

        return eventRepository.findDistinctByParticipationsUser(user, pageable).map(this::toResponse);
    }


    /**
     * Converts an {@link Event} entity into a client-friendly {@link EventResponse} DTO.
     * <p>
     * This method maps the event's data and enriches it with additional information, such as
     * generating pre-signed URLs for poster and profile images and compiling a list of participants.
     *
     * @param event The event entity to convert.
     * @return The corresponding {@link EventResponse} DTO.
     */
    private EventResponse toResponse(Event event){

        List<JoinedUserResponse> joinedUsers = event.getParticipations().stream()
                        .map(participation -> {

                        User user = participation.getUser();
                        return JoinedUserResponse.builder()
                                .fullName(user.getFirstName() + " " + user.getLastName())
                                .profileImageUrl(imageStorageService.getPresignedUrl(user.getProfileImage()))
                                .build();

                    })
                    .toList();

        return EventResponse.builder()
                    .ID(event.getID())
                    .name(event.getName())
                    .description(event.getDescription())
                    .dateAndTime(event.getDateAndTime())
                    .street(event.getStreet())
                    .city(event.getCity())
                    .CAP(event.getCAP())
                    .posterUrl(imageStorageService.getPresignedUrl(event.getPoster()))
                    .hostName(event.getHost().getHostName())
                    .joinedUsers(joinedUsers)
                    .build();
    }
}
