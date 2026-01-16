package it.younify.modules.sgn.model.entity;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Represents a notification entity in the database.
 * This class maps to the `notification` table and stores information about notifications sent within the system.
 */
@Entity
@Table(name = "notification")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    /**
     * The unique identifier for the notification.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    /**
     * The host who sent the notification. This is optional and may be null.
     * @see it.younify.modules.sgac.model.entity.Host
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_host_id")
    private Host senderHost;

    /**
     * The user who sent the notification. This is optional and may be null.
     * @see it.younify.modules.sgac.model.entity.User
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id")
    private User senderUser;

    /**
     * The host who will receive the notification. This is optional and may be null.
     * @see it.younify.modules.sgac.model.entity.Host
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_host_id")
    private Host receiverHost;

    /**
     * The user who will receive the notification. This is optional and may be null.
     * @see it.younify.modules.sgac.model.entity.User
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_id")
    private User receiverUser;

    /**
     * The timestamp indicating when the notification was created.
     */
    @Column(nullable = false)
    private Instant date;

    /**
     * The type of the notification, which categorizes the notification's purpose.
     * @see it.younify.modules.sgn.model.entity.Type
     */
    @Column(nullable = false, length = 18)
    private Type type;

    /**
     * The main message or content of the notification.
     */
    @Column(nullable = false, length = 100)
    private String content;
}
