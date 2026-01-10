package it.younify.modules.sge.model.entity;

import it.younify.modules.sgac.model.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "participation")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Participation {

    @EmbeddedId
    private ParticipationId id = new ParticipationId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;
}
