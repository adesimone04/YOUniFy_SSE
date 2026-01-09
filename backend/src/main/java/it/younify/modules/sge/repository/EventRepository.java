package it.younify.modules.sge.repository;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sge.model.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByOrderByDateandtimeAsc(Pageable pageable);
    Page<Event> findAllByHostOrderByDateandtimeAsc(Host host, Pageable pageable);
    Page<Event> findAllByCityOrderByDateandtimeAsc(String city, Pageable pageable);

    @Query("SELECT p.event FROM Partecipation p WHERE p.user = :user ORDER BY p.event.dateandtime ASC")
    Page<Event> findJoinedEventsByUser(@Param("user") User user, Pageable pageable);
}
