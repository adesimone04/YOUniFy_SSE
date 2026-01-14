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
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * The {@code EventRepository} interface is a Spring Data JPA repository for {@link Event} entities.
 * It provides methods for querying events based on various criteria such as date, host, city, and user participation.
 */
public interface EventRepository extends JpaRepository<Event, Long> {
    
    /**
     * Retrieves a paginated list of all events, ordered by date and time in ascending order.
     * This is typically used for displaying a timeline of upcoming events.
     *
     * @param pageable The pagination information.
     * @return A paginated list of events.
     */
    Page<Event> findAllByOrderByDateAndTimeAsc(Pageable pageable);

    /**
     * Retrieves a paginated list of events hosted by a specific host, ordered by date.
     *
     * @param host The host whose events are to be retrieved.
     * @param pageable The pagination information.
     * @return A paginated list of the host's events.
     */
    Page<Event> findAllByHostOrderByDateAndTimeAsc(Host host, Pageable pageable);

    /**
     * Retrieves a paginated list of events taking place in a specific city, ordered by date.
     *
     * @param city The name of the city.
     * @param pageable The pagination information.
     * @return A paginated list of events in the specified city.
     */
    Page<Event> findAllByCityOrderByDateAndTimeAsc(String city, Pageable pageable);

    /**
     * Retrieves a paginated list of distinct events that a specific user is participating in.
     *
     * @param user The user whose participating events are to be retrieved.
     * @param pageable The pagination information.
     * @return A paginated list of events the user has joined.
     */
    Page<Event> findDistinctByParticipationsUser(User user, Pageable pageable);

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
