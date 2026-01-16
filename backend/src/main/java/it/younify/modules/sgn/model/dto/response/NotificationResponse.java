package it.younify.modules.sgn.model.dto.response;

import it.younify.modules.sgn.model.entity.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Represents the response body for a notification.
 * This class is used to transfer notification data from the server to the client.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    /**
     * The unique identifier of the notification.
     */
    private Long ID;

    /**
     * The name of the user who sent the notification.
     */
    private String senderName;

    /**
     * The name of the user who will receive the notification.
     */
    private String receiverName;

    /**
     * The type of the notification, which categorizes the notification's purpose.
     * @see it.younify.modules.sgn.model.entity.Type
     */
    private Type type;

    /**
     * The main message or content of the notification.
     */
    private String content;

    /**
     * The timestamp indicating when the notification was created.
     */
    private Instant date;
}
