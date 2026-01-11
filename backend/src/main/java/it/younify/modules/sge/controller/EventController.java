package it.younify.modules.sge.controller;

import it.younify.core.model.ApiResponse;
import it.younify.modules.sge.facade.EventFacade;
import it.younify.modules.sge.model.dto.request.EventEditRequest;
import it.younify.modules.sge.model.dto.request.EventPostRequest;
import it.younify.modules.sge.model.dto.response.EventResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


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

    @PreAuthorize("hasRole('HOST')")
    @GetMapping("/host/{hostId}")
    public ResponseEntity<ApiResponse<Page<EventResponse>>> getEventsByHost(@PathVariable("hostId") String hostId,
                                                                            @PageableDefault(size = 10) Pageable pageable){

            Page<EventResponse> page = eventFacade.getEventsByHost(hostId, pageable);

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
