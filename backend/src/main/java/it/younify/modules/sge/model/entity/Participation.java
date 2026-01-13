package it.younify.modules.sge.model.entity;

import it.younify.modules.sgac.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "participation")

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Participation {

    @EmbeddedId
    @Builder.Default
    private ParticipationId id = new ParticipationId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;
}
