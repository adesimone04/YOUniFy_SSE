package it.younify.modules.sgn.controller;

import it.younify.core.model.ApiResponse;
import it.younify.modules.sgn.facade.NotificationFacade;
import it.younify.modules.sgn.model.dto.request.NotificationRequest;
import it.younify.modules.sgn.model.dto.response.NotificationResponse;
import it.younify.modules.sgn.service.NotificationSendService;
import it.younify.modules.sgn.service.NotificationViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/notify")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationSendService notificationSendService;
    private final NotificationViewService notificationViewService;
    private final NotificationFacade notificationFacade;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> send(@RequestBody NotificationRequest notificationRequest) {
        notificationFacade.send(notificationRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("NOTIFIED_SUCCESSFULLY")
                        .build()
        );
    }

    @GetMapping("/view")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> view(Pageable pageable) {
        Page<NotificationResponse> page = notificationFacade.getNotify(pageable);
        return ResponseEntity.ok(
                ApiResponse.<Page<NotificationResponse>>builder()
                        .message("RETRIEVED_SUCCESSFULLY")
                        .data(page)
                        .build()
                );
    }
}
