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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * The {@code EventViewService} class provides methods for querying and retrieving event data in a client-friendly format.
 * It fetches events based on different criteria and transforms them into {@link EventResponse} DTOs,
 * adapting the response based on the authenticated principal ({@link User} or {@link Host}).
 *
 * @see EventRepository
 * @see ImageStorageService
 * @see EventResponse
 */
@Service
@RequiredArgsConstructor
public class EventViewService {

    private final ImageStorageService imageStorageService;
    private final EventRepository eventRepository;

    /**
     * Retrieves a paginated list of all events, sorted by their date and time in ascending order.
     * This method determines if the authenticated principal is a {@link User} or {@link Host}
     * to correctly build the {@link EventResponse}, specifically to check if the user has joined the event.
     *
     * @param pageable The pagination information.
     * @return A paginated list of {@link EventResponse} objects.
     */
    @Transactional(readOnly = true)
    public Page<EventResponse> getEventsByDate(@NonNull Pageable pageable) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User) {

            return eventRepository.findAllByOrderByDateAndTimeAsc(pageable)
                    .map(event -> toResponse(((User) principal).getID(), event));
        }
        else return eventRepository.findAllByOrderByDateAndTimeAsc(pageable)
                .map(event -> toResponse(((Host) principal).getID(), event));
    }

    /**
     * Retrieves a paginated list of events created by a specific {@link Host}, sorted by date and time in ascending order.
     * This method is typically used by a host to view their own events.
     *
     * @param host The host whose events are to be retrieved.
     * @param pageable The pagination information.
     * @return A paginated list of {@link EventResponse} objects.
     */
    @Transactional(readOnly = true)
    public Page<EventResponse> getEventsByHost(@NonNull Host host, @NonNull Pageable pageable){

        return eventRepository.findAllByHostOrderByDateAndTimeAsc(host, pageable).map(this::toMyResponse);
    }

    /**
     * Retrieves a paginated list of events located in a specific city, sorted by date and time in ascending order.
     * This method determines if the authenticated principal is a {@link User} or {@link Host}
     * to correctly build the {@link EventResponse}, specifically to check if the user has joined the event.
     *
     * @param city The city to filter events by.
     * @param pageable The pagination information.
     * @return A paginated list of {@link EventResponse} objects.
     */
    @Transactional(readOnly = true)
    public Page<EventResponse> getEventsByCity(@NonNull String city, @NonNull Pageable pageable){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User) {

            return eventRepository.findAllByCityOrderByDateAndTimeAsc(city, pageable)
                    .map(event -> toResponse(((User) principal).getID(), event));
        }
        else return eventRepository.findAllByCityOrderByDateAndTimeAsc(city, pageable)
                .map(event -> toResponse(((Host) principal).getID(), event));
    }

    /**
     * Retrieves a paginated list of events that a specific {@link User} has joined.
     * This method is typically used by a user to view their personal event schedule.
     *
     * @param user The user whose joined events are to be retrieved.
     * @param pageable The pagination information.
     * @return A paginated list of {@link EventResponse} objects.
     */
    @Transactional(readOnly = true)
    public Page<EventResponse> getEventsByJoined(@NonNull User user, @NonNull Pageable pageable){

        return eventRepository.findDistinctByParticipationsUser(user, pageable).map(this::toMyResponse);
    }


    /**
     * Converts an {@link Event} entity into a client-friendly {@link EventResponse} DTO,
     * including whether a specific user has joined the event and a list of other participants.
     * This method is used when the "joined" status for a specific user needs to be highlighted.
     *
     * @param ID The ID of the user for whom to check the "joined" status.
     * @param event The event entity to convert.
     * @return The corresponding {@link EventResponse} DTO.
     */
    private EventResponse toResponse(String ID, Event event){

        boolean isJoined = event.getParticipations().stream()
                .anyMatch(participation -> participation.getUser().getID().equals(ID));

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
                    .joined(isJoined)
                    .joinedUsers(joinedUsers)
                    .build();
    }


    /**
     * Converts an {@link Event} entity into a client-friendly {@link EventResponse} DTO,
     * including a list of participants but without checking the "joined" status for a specific user.
     * This method is typically used when the context already implies the user's participation
     * or when the "joined" status is not relevant for the current view (e.g., a host viewing their own event).
     *
     * @param event The event entity to convert.
     * @return The corresponding {@link EventResponse} DTO.
     */
    private EventResponse toMyResponse(Event event){

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
