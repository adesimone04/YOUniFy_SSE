package it.younify.modules.sge.repository;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sge.model.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByOrderByDateAndTimeAsc(Pageable pageable);
    Page<Event> findAllByHostOrderByDateAndTimeAsc(Host host, Pageable pageable);
    Page<Event> findAllByCityOrderByDateAndTimeAsc(String city, Pageable pageable);
    Page<Event> findDistinctByParticipationsUser(User user, Pageable pageable);

    @Query("SELECT DISTINCT e.city FROM Event e WHERE e.city IS NOT NULL ORDER BY e.city ASC")
    List<String> findDistinctCities();
}
