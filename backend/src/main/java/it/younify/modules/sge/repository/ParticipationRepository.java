package it.younify.modules.sge.repository;

import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sge.model.entity.Event;
import it.younify.modules.sge.model.entity.Participation;
import it.younify.modules.sge.model.entity.ParticipationId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * The {@code ParticipationRepository} interface is a Spring Data JPA repository for {@link Participation} entities.
 * It manages the many-to-many relationship between users and events, using a composite key {@link ParticipationId}.
 * This repository provides standard CRUD operations and custom queries for tracking event attendance.
 *
 * @see Participation
 * @see ParticipationId
 * @see JpaRepository
 */
public interface ParticipationRepository extends JpaRepository<Participation, ParticipationId> {

    /**
     * Finds a specific participation record by user and event.
     * This method is useful for checking if a user is already registered for an event.
     *
     * @param user The user who might be participating.
     * @param event The event in question.
     * @return An {@link Optional} containing the {@link Participation} if it exists, otherwise empty.
     */
    Optional<Participation> findByUserAndEvent(User user, Event event);


    /**
     * Retrieves a paginated list of all participation records for events occurring within a specific time range.
     * This query is useful for finding all event participations scheduled between two points in time,
     * for example, to send reminders or perform cleanup tasks.
     * The use of {@code JOIN FETCH} ensures that the related {@code User}, {@code Event}, and {@code Host} entities are loaded in a single query,
     * avoiding N+1 performance issues.
     *
     * @param start The start of the time range.
     * @param end The end of the time range.
     * @param pageable The pagination information.
     * @return A {@link Page} of {@link Participation} records for events within the specified range.
     */
    @Query(value = "SELECT p FROM Participation p " +
                  "JOIN FETCH p.user " +
                  "JOIN FETCH p.event e " +
                  "JOIN FETCH e.host " +
                  "WHERE e.dateAndTime BETWEEN :start AND :end",
          countQuery = "SELECT count(p) FROM Participation p WHERE p.event.dateAndTime BETWEEN :start AND :end")
    Page<Participation> findAllByEventDateAndTimeBetween(@Param("start") Instant start, @Param("end") Instant end, Pageable pageable);
}
