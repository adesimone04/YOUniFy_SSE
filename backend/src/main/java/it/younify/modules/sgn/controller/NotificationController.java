package it.younify.modules.sgn.controller;

import it.younify.core.model.ApiResponse;
import it.younify.modules.sgn.facade.NotificationFacade;
import it.younify.modules.sgn.model.dto.request.NotificationRequest;
import it.younify.modules.sgn.model.dto.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The NotificationController class provides RESTful endpoints for managing notifications.
 * It handles HTTP requests for sending and viewing notifications, delegating the business logic to the {@link NotificationFacade}.
 * This controller is the main entry point for all notification-related API calls.
 *
 * @see NotificationFacade
 * @see ApiResponse
 */
@RestController
@RequestMapping("/api/notify")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationFacade notificationFacade;

    /**
     * Handles the HTTP GET request to view notifications for the authenticated user.
     * This endpoint retrieves a paginated list of notifications using the {@link NotificationFacade}.
     *
     * @param pageable The pagination information provided through request parameters.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} containing a {@link Page} of {@link NotificationResponse} objects.
     * @see Pageable
     * @see NotificationResponse
     */
    @GetMapping("/view")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> view(@PageableDefault(size = 10) Pageable pageable) {

        Page<NotificationResponse> page = notificationFacade.getNotify(pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<NotificationResponse>>builder()
                        .message("RETRIEVED_SUCCESSFULLY")
                        .internalCode("RETRIEVED_SUCCESSFULLY")
                        .data(page)
                        .build()
                );
    }
}
