package it.younify.modules.sgtr.repository;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.model.entity.FlagCategory;
import it.younify.modules.sgtr.model.entity.Threads;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ThreadRepository extends JpaRepository<Threads, Long> {

    Optional<Threads> findByID(Long ID);
    Optional<Threads> findByFlag(FlagCategory flag);
    Optional<Threads> findByUser(User user);
    Optional<Threads> findByHost(Host host);
    Optional<Threads> findByPublicationDate(Instant publicationDate);

    List<Threads> findAllByOrderByCFUAsc(User user);
    List<Threads> findAllByOrderByCFUDesc(User user);

    Page<Threads> findAllByOrderByPublicationDateDesc(Pageable pageable);
    Page<Threads> findAllByFlagOrderByPublicationDateDesc(FlagCategory flag, Pageable pageable);
    Page<Threads> findByHost_UniListContainingIgnoreCaseOrUser_UniversityIgnoreCaseOrderByPublicationDateDesc(

            String universityInHost,
            String universityInUser,
            Pageable pageable
    );
}
