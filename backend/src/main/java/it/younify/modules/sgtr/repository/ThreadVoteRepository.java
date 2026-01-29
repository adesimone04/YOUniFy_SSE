package it.younify.modules.sgtr.repository;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.model.entity.Threads;
import it.younify.modules.sgtr.model.entity.ThreadsVote;
import it.younify.modules.sgtr.model.entity.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * The {@code ThreadVoteRepository} interface is a Spring Data JPA repository for {@link ThreadsVote} entities.
 * It provides methods to find votes on threads, which is essential for managing user interactions and preventing duplicate votes.
 */
public interface ThreadVoteRepository extends JpaRepository<ThreadsVote, Long> {

    /**
     * Finds a thread vote by its unique ID.
     *
     * @return An {@link Optional} containing the vote if found.
     */
    Optional<ThreadsVote> findById(Long id);

    /**
     * Finds a vote cast by a specific user on a specific thread.
     * This is crucial for checking if a user has already voted on a thread.
     *
     * @param thread The thread that was voted on.
     * @param user The user who cast the vote.
     * @return An {@link Optional} containing the vote if it exists.
     */
    Optional<ThreadsVote> findByThreadAndUser(Threads thread, User user);

    /**
     * Finds a vote cast by a specific host on a specific thread.
     * This is crucial for checking if a host has already voted on a thread.
     *
     * @param thread The thread that was voted on.
     * @param host The host who cast the vote.
     * @return An {@link Optional} containing the vote if it exists.
     */
    Optional<ThreadsVote> findByThreadAndHost(Threads thread, Host host);


    /**
     * Finds all votes cast by a specific user on a given list of threads.
     * This is useful for efficiently fetching a user's vote status for multiple threads at once.
     *
     * @param user The user whose votes are to be found.
     * @param threads The list of threads to check for votes.
     * @return A list of {@link ThreadsVote} objects.
     */
    List<ThreadsVote> findByUserAndThreadIn(User user, List<Threads> threads);


    /**
     * Finds all votes cast by a specific host on a given list of threads.
     * This is useful for efficiently fetching a host's vote status for multiple threads at once.
     *
     * @param host The host whose votes are to be found.
     * @param threads The list of threads to check for votes.
     * @return A list of {@link ThreadsVote} objects.
     */
    List<ThreadsVote> findByHostAndThreadIn(Host host, List<Threads> threads);
}
