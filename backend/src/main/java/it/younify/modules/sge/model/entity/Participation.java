package it.younify.modules.sge.model.entity;

import it.younify.modules.sgac.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * The {@code Participation} class is a JPA entity that represents a user's participation in an event.
 * <p>
 * It serves as a join entity for the many-to-many relationship between {@link User} and {@link Event},
 * using a composite primary key {@link ParticipationId}.
 */
@Entity
@Table(name = "participation")

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Participation {

    /**
     * The composite primary key for the participation record.
     */
    @EmbeddedId
    @Builder.Default
    private ParticipationId id = new ParticipationId();

    /**
     * The event being attended.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @MapsId("eventId")
    private Event event;

    /**
     * The user attending the event.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @MapsId("userId")
    private User user;
}
