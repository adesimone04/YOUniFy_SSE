package it.younify.modules.sgtr.repository;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.model.entity.Threads;
import it.younify.modules.sgtr.model.entity.ThreadsVote;
import it.younify.modules.sgtr.model.entity.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThreadVoteRepository extends JpaRepository<ThreadsVote, Long> {

    Optional<ThreadsVote> findByID(Long id);
    Optional<ThreadsVote> findByThreadAndUser(Threads thread, User user);
    Optional<ThreadsVote> findByThreadAndHost(Threads thread, Host Host);
}
