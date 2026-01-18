package it.younify.modules.sgm.repository;

import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgm.model.entity.Match;
import it.younify.modules.sgm.model.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
    Page<Match> findAllBySenderAndStatus(User sender, Status status, Pageable pageable);
    Page<Match> findAllByReceiverAndStatus(User receiver, Status status, Pageable pageable);
    Page<Match> findAllByReceiverOrSenderAndStatus(User receiver, User sender, Status status, Pageable pageable);


}
