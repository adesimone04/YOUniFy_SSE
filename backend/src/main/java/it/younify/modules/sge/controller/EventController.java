package it.younify.modules.sge.controller;

import it.younify.core.model.ApiResponse;
import it.younify.modules.sge.facade.EventFacade;
import it.younify.modules.sge.model.dto.request.EventEditRequest;
import it.younify.modules.sge.model.dto.request.EventPostRequest;
import it.younify.modules.sge.model.dto.response.EventResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * The {@code EventController} class handles all HTTP requests related to events.
 * It uses the {@link EventFacade} to perform business logic and provides role-based access control for its endpoints.
 */
@RestController
@RequestMapping("api/event")
@RequiredArgsConstructor
@Validated
public class EventController {

    private final EventFacade eventFacade;

    /**
     * Creates a new event. This endpoint is restricted to hosts.
     *
     * @param eventPostRequest The multipart request containing the event data and poster.
     * @return A response indicating the event was posted successfully.
     */
    @PreAuthorize("hasRole('HOST')")
    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> post(@Valid @ModelAttribute EventPostRequest eventPostRequest){

        eventFacade.createEvent(eventPostRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("POSTED_SUCCESSFULLY")
                        .build()
        );
    }

    /**
     * Deletes an event. This endpoint is restricted to the event's host and administrators.
     *
     * @param eventId The ID of the event to delete.
     * @return A response indicating the event was deleted successfully.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HOST')")
    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@Positive @PathVariable Long eventId){

        eventFacade.deleteEvent(eventId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("DELETED_SUCCESSFULLY")
                        .build()
        );
    }

    /**
     * Edits an existing event. This endpoint is restricted to the event's host.
     *
     * @param eventId The ID of the event to edit.
     * @param eventEditRequest The multipart request containing the updated event data.
     * @return A response indicating the event was edited successfully.
     */
    @PreAuthorize("hasRole('HOST')")
    @PostMapping(value = "/edit/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> editEvent(@Positive @PathVariable Long eventId, @Valid @ModelAttribute EventEditRequest eventEditRequest){

        eventFacade.editEvent(eventId, eventEditRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("EDITED_SUCCESSFULLY")
                        .build()
        );
    }

    /**
     * Allows a user to join an event. This endpoint is restricted to users.
     *
     * @param eventId The ID of the event to join.
     * @return A response indicating the user joined successfully.
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/join/{eventId}")
    public ResponseEntity<ApiResponse<Void>> joinEvent(@Positive @PathVariable Long eventId){

        eventFacade.joinEvent(eventId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("JOINED_SUCCESSFULLY")
                        .build()
        );
    }


    /**
     * Allows a user to unjoin an event. This endpoint is restricted to users.
     *
     * @param eventId The ID of the event to unjoin.
     * @return A response indicating the user unjoined successfully.
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/unjoin/{eventId}")
    public ResponseEntity<ApiResponse<Void>> unjoinEvent(@Positive @PathVariable Long eventId){

        eventFacade.unjoinEvent(eventId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("UNJOINED_SUCCESSFULLY")
                        .build()
        );
    }

    /**
     * Retrieves a paginated list of all events, sorted by date.
     *
     * @param pageable The pagination information.
     * @return A paginated list of events.
     */
    @GetMapping()
    public ResponseEntity<ApiResponse<Page<EventResponse>>> getEventsByDate(@PageableDefault(size = 10) Pageable pageable){

        Page<EventResponse> page = eventFacade.getEventsByDate(pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<EventResponse>>builder()
                        .message("VIEW_SUCCESSFULLY")
                        .data(page)
                        .build()
        );
    }

    /**
     * Retrieves a paginated list of events filtered by city.
     *
     * @param city The city to filter by.
     * @param pageable The pagination information.
     * @return A paginated list of events in the specified city.
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<Page<EventResponse>>> getEventsByCity(@Positive @PathVariable("city") String city, @PageableDefault(size = 10) Pageable pageable){

        Page<EventResponse> page = eventFacade.getEventsByCity(city, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<EventResponse>>builder()
                        .message("VIEW_SUCCESSFULLY")
                        .data(page)
                        .build()
        );
    }

    /**
     * Retrieves a list of all unique cities where events are being held.
     *
     * @return A list of cities, formatted for use in a client-side select/combo box.
     */
    @GetMapping("/utility/cities")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getCities(){

        return ResponseEntity.ok(
                ApiResponse.<List<Map<String, String>>>builder()
                        .message("RETRIEVE_SUCCESSFULLY")
                        .data(
                                eventFacade.getCities().stream().map(city -> Map.of(
                                        "value", city,
                                        "label", city
                                ))
                                        .collect(Collectors.toList())
                        )
                        .build()
        );
    }

    /**
     * Retrieves a paginated list of events for the currently authenticated host.
     *
     * @param pageable The pagination information.
     * @return A paginated list of the host's events.
     */
    @PreAuthorize("hasRole('HOST')")
    @GetMapping("/myEvents")
    public ResponseEntity<ApiResponse<Page<EventResponse>>> getEventsByHost(@PageableDefault(size = 10) Pageable pageable){

            Page<EventResponse> page = eventFacade.getEventsByHost(pageable);

            return ResponseEntity.ok(
                    ApiResponse.<Page<EventResponse>>builder()
                            .message("VIEW_SUCCESSFULLY")
                            .data(page)
                            .build()
            );
    }

    /**
     * Retrieves a paginated list of events that the currently authenticated user has joined.
     *
     * @param pageable The pagination information.
     * @return A paginated list of joined events.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/joined")
    public ResponseEntity<ApiResponse<Page<EventResponse>>> getEventsJoined(@PageableDefault(size = 10) Pageable pageable){

        Page<EventResponse> page = eventFacade.getEventsJoined(pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<EventResponse>>builder()
                        .message("VIEW_SUCCESFULLY")
                        .data(page)
                        .build()
        );
    }
}
