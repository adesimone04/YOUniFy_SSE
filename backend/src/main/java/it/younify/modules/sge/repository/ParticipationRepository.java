package it.younify.modules.sge.repository;

import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sge.model.entity.Event;
import it.younify.modules.sge.model.entity.Participation;
import it.younify.modules.sge.model.entity.ParticipationId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * The {@code ParticipationRepository} interface is a Spring Data JPA repository for {@link Participation} entities.
 * <p>
 * It manages the many-to-many relationship between users and events, using a composite key {@link ParticipationId}.
 * This repository provides standard CRUD operations for tracking event attendance.
 */
public interface ParticipationRepository extends JpaRepository<Participation, ParticipationId> {

    Optional<Participation> findByUserAndEvent(User user, Event event);
}
