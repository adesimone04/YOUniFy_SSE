package it.younify.modules.sgn.model.dto.request;

import it.younify.modules.sgn.model.entity.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Represents the request body for creating a new notification.
 * This class is used to transfer notification data from the client to the server.
 */
@Data
public class NotificationRequest {

    /**
     * The unique identifier of the user who will receive the notification.
     */
    @NotBlank
    private String receiverId;

    /**
     * The type of the notification, which categorizes the notification's purpose.
     * @see it.younify.modules.sgn.model.entity.Type
     */
    @NotNull
    private Type type;

    /**
     * The main message or content of the notification.
     */
    @NotBlank
    private String content;
}
