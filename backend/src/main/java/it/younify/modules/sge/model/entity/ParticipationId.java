package it.younify.modules.sge.model.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

/**
 * The {@code ParticipationId} class represents the composite primary key for the {@link Participation} entity.
 * It combines the event ID and the user ID to uniquely identify a participation record.
 */
@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ParticipationId implements Serializable {

    /**
     * The ID of the event.
     */
    private Long eventId;

    /**
     * The ID of the user.
     */
    private String userId;
}