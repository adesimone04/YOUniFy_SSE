package it.younify.modules.sge.model.entity;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "partecipation")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Partecipation {

    /**
     * Chiave composta istanziata.
     */
    @EmbeddedId
    private PartecipationId id = new PartecipationId();

    /**
     * EVENTO
     * @MapsId("eventId") dice a JPA: "Usa il valore di eventId dentro la classe PartecipationId
     * per mappare questa relazione". Non serve @JoinColumn, lo calcola lui
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;
}
