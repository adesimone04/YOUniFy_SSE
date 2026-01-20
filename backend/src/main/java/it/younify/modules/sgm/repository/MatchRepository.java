package it.younify.modules.sgm.repository;

import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgm.model.entity.Match;
import it.younify.modules.sgm.model.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchRepository extends JpaRepository<Match, Long> {

    Page<Match> findAllBySenderAndStatus(User sender, Status status, Pageable pageable);

    Page<Match> findAllByReceiverAndStatus(User receiver, Status status, Pageable pageable);

    @Query("SELECT m FROM Match m WHERE (m.receiver = :receiver OR m.sender = :sender) AND m.status = :status")
    Page<Match> findAllByReceiverAndStatusOrSenderAndStatus(
            @Param("receiver") User receiver,
            @Param("sender") User sender,
            @Param("status") Status status,
            Pageable pageable
    );

    @Query("SELECT COUNT(m) FROM Match m WHERE (m.receiver = :user OR m.sender = :user) AND m.status = :status")
    Integer countMatches(@Param("user") User user, @Param("status") Status status);

}
