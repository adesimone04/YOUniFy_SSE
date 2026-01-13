package it.younify.modules.sge.controller;

import it.younify.core.model.ApiResponse;
import it.younify.modules.sge.facade.EventFacade;
import it.younify.modules.sge.model.dto.request.EventEditRequest;
import it.younify.modules.sge.model.dto.request.EventPostRequest;
import it.younify.modules.sge.model.dto.response.EventResponse;
import it.younify.modules.sge.repository.EventRepository;
import it.younify.modules.sgtr.model.entity.FlagCategory;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api/event")
@RequiredArgsConstructor
public class EventController {

    private final EventFacade eventFacade;

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

    @PreAuthorize("hasAnyRole('ADMIN', 'HOST')")
    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long eventId){

        eventFacade.deleteEvent(eventId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("DELETED_SUCCESSFULLY")
                        .build()
        );
    }

    @PreAuthorize("hasRole('HOST')")
    @PostMapping(value = "/edit/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> editEvent(@PathVariable Long eventId, @Valid @ModelAttribute EventEditRequest eventEditRequest){

        eventFacade.editEvent(eventId, eventEditRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("EDITED_SUCCESSFULLY")
                        .build()
        );
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/join/{eventId}")
    public ResponseEntity<ApiResponse<Void>> joinEvent(@PathVariable Long eventId){

        eventFacade.joinEvent(eventId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("JOINED_SUCCESSFULLY")
                        .build()
        );
    }

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

    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<Page<EventResponse>>> getEventsByCity(@PathVariable("city") String city, @PageableDefault(size = 10) Pageable pageable){

        Page<EventResponse> page = eventFacade.getEventsByCity(city, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<EventResponse>>builder()
                        .message("VIEW_SUCCESSFULLY")
                        .data(page)
                        .build()
        );
    }

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

    @PreAuthorize("hasRole('HOST')")
    @GetMapping("/myevents")
    public ResponseEntity<ApiResponse<Page<EventResponse>>> getEventsByHost(@PageableDefault(size = 10) Pageable pageable){

            Page<EventResponse> page = eventFacade.getEventsByHost(pageable);

            return ResponseEntity.ok(
                    ApiResponse.<Page<EventResponse>>builder()
                            .message("VIEW_SUCCESSFULLY")
                            .data(page)
                            .build()
            );
    }

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
