package it.younify.modules.sgtr.repository;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.model.entity.Threads;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ThreadRepository extends JpaRepository<Threads, Long> {
    Optional<Threads> findByID(Long ID);
    Optional<Threads> findByFlag(String flag);
    Optional<Threads> findByIDUser(User user);
    Optional<Threads> findByIDHost(Host host);
    Optional<Threads> findBypublicationDate(Instant publicationDate);
    List<Threads> findAllByOrderByCFUAsc(User user);
    List<Threads> findAllByOrderByCFUDesc(User user);
}
