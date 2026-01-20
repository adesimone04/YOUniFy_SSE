package it.younify.modules.sge.model.entity;

import it.younify.modules.sgac.model.entity.Host;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Event} class is a JPA entity that represents an event hosted on the platform.
 * <p>
 * Each event has a name, description, location, date, and an associated {@link Host}.
 * The database enforces a unique constraint to prevent duplicate events (same name, date, and host).
 */
@Entity
@Table(
        name = "event",
        indexes = {
                @Index(name = "idx_event_date", columnList = "date_and_time")
        },
        uniqueConstraints = {
        @UniqueConstraint(
            name = "event_duplicate_check",
            columnNames = {"date_and_time", "host_id", "name"}
        )
    }
)

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    /**
     * The unique identifier for the event.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    /**
     * The name or title of the event.
     */
    @Column(nullable = false)
    private String name;

    /**
     * A detailed description of the event.
     */
    @Column(nullable = false)
    private String description;

    /**
     * The date and time when the event takes place.
     */
    @Column(nullable = false)
    private Instant dateAndTime;

    /**
     * The street address where the event is located.
     */
    @Column(nullable = false)
    private String street;

    /**
     * The city where the event is located.
     */
    @Column(nullable = false)
    private String city;

    /**
     * The postal code (CAP) of the event's location.
     */
    @Column(nullable = false, length = 5)
    private String CAP;

    /**
     * A URL or path to the event's promotional poster image.
     */
    @Column(nullable = false)
    private String poster;

    /**
     * The host organization responsible for the event.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Host host;

    /**
     * The list of user participations for this event.
     */
    @OneToMany(mappedBy = "event")
    @Builder.Default
    private List<Participation> participations = new ArrayList<>();
}
