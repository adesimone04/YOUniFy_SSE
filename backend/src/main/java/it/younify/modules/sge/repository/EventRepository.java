package it.younify.modules.sge.repository;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sge.model.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * The {@code EventRepository} interface is a Spring Data JPA repository for {@link Event} entities.
 * It provides methods for querying events based on various criteria such as date, host, city, and user participation.
 */
public interface EventRepository extends JpaRepository<Event, Long> {
    
    /**
     * Retrieves a paginated list of all events, ordered by their proximity to the current date and time.
     * Upcoming events are listed first, sorted in ascending order of date and time.
     * Past events are listed after upcoming events, sorted in descending order of date and time.
     *
     * @param pageable The pagination information.
     * @return A paginated list of events.
     */
    @Query("SELECT e FROM Event e ORDER BY " +
            "CASE WHEN e.dateAndTime >= CURRENT_TIMESTAMP THEN 0 ELSE 1 END, " +
            "CASE WHEN e.dateAndTime >= CURRENT_TIMESTAMP THEN e.dateAndTime END ASC, " +
            "CASE WHEN e.dateAndTime < CURRENT_TIMESTAMP THEN e.dateAndTime END DESC")
    Page<Event> findAllOrderedByProximity(Pageable pageable);

    /**
     * Retrieves a paginated list of events hosted by a specific host, ordered by their proximity to the current date and time.
     * Upcoming events are listed first, sorted in ascending order of date and time.
     * Past events are listed after upcoming events, sorted in descending order of date and time.
     *
     * @param host The host whose events are to be retrieved.
     * @param pageable The pagination information.
     * @return A paginated list of the host's events.
     */
    @Query("SELECT e FROM Event e WHERE e.host = :host ORDER BY " +
            "CASE WHEN e.dateAndTime >= CURRENT_TIMESTAMP THEN 0 ELSE 1 END, " +
            "CASE WHEN e.dateAndTime >= CURRENT_TIMESTAMP THEN e.dateAndTime END ASC, " +
            "CASE WHEN e.dateAndTime < CURRENT_TIMESTAMP THEN e.dateAndTime END DESC")
    Page<Event> findAllByHostOrderedByProximity(@Param("host") Host host, Pageable pageable);

    /**
     * Retrieves a paginated list of events taking place in a specific city, ordered by their proximity to the current date and time.
     * Upcoming events are listed first, sorted in ascending order of date and time.
     * Past events are listed after upcoming events, sorted in descending order of date and time.
     *
     * @param city The name of the city.
     * @param pageable The pagination information.
     * @return A paginated list of events in the specified city.
     */
    @Query("SELECT e FROM Event e WHERE e.city = :city ORDER BY " +
            "CASE WHEN e.dateAndTime >= CURRENT_TIMESTAMP THEN 0 ELSE 1 END, " +
            "CASE WHEN e.dateAndTime >= CURRENT_TIMESTAMP THEN e.dateAndTime END ASC, " +
            "CASE WHEN e.dateAndTime < CURRENT_TIMESTAMP THEN e.dateAndTime END DESC")
    Page<Event> findAllByCityOrderedByProximity(@Param("city") String city, Pageable pageable);

    /**
     * Retrieves a paginated list of distinct events that a specific user is participating in, ordered by their proximity to the current date and time.
     * Upcoming events are listed first, sorted in ascending order of date and time.
     * Past events are listed after upcoming events, sorted in descending order of date and time.
     *
     * @param user The user whose participating events are to be retrieved.
     * @param pageable The pagination information.
     * @return A paginated list of events the user has joined.
     */
    @Query("SELECT DISTINCT e FROM Event e JOIN e.participations p WHERE p.user = :user ORDER BY " +
            "CASE WHEN e.dateAndTime >= CURRENT_TIMESTAMP THEN 0 ELSE 1 END, " +
            "CASE WHEN e.dateAndTime >= CURRENT_TIMESTAMP THEN e.dateAndTime END ASC, " +
            "CASE WHEN e.dateAndTime < CURRENT_TIMESTAMP THEN e.dateAndTime END DESC")
    Page<Event> findDistinctByParticipationsUserOrderedByProximity(@Param("user") User user, Pageable pageable);

    /**
     * Checks for the existence of an event with the same host, date, and name.
     * This query is used to prevent the creation of duplicate events.
     *
     * @param host The host of the event.
     * @param date The date and time of the event.
     * @param name The name of the event.
     * @return An {@link Optional} containing the event if a duplicate is found.
     */
    @Query("SELECT e FROM Event e WHERE e.host = :host AND e.dateAndTime = :date AND e.name = :name")
    Optional<Event> findExistingEvent(
            @Param("host") Host host,
            @Param("date") Instant date,
            @Param("name") String name
    );

    /**
     * Retrieves a list of all unique cities where events are being held.
     * This is useful for populating filter dropdowns or search suggestions.
     *
     * @return A list of distinct city names, sorted alphabetically.
     */
    @Query("SELECT DISTINCT e.city FROM Event e WHERE e.city IS NOT NULL ORDER BY e.city ASC")
    List<String> findDistinctCities();
}
