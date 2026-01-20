package it.younify.modules.sgm.repository;

import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgm.model.entity.Match;
import it.younify.modules.sgm.model.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * The {@code MatchRepository} interface is a Spring Data JPA repository for {@link Match} entities.
 * It provides standard CRUD operations and custom query methods for finding matches based on various criteria.
 *
 * @see Match
 * @see JpaRepository
 */
public interface MatchRepository extends JpaRepository<Match, Long> {

    /**
     * Finds a paginated list of matches sent by a specific user with a given status.
     *
     * @param sender The user who sent the matches.
     * @param status The status of the matches to find.
     * @param pageable The pagination information.
     * @return A paginated list of {@link Match} objects.
     */
    Page<Match> findAllBySenderAndStatus(User sender, Status status, Pageable pageable);

    /**
     * Finds a paginated list of matches received by a specific user with a given status.
     *
     * @param receiver The user who received the matches.
     * @param status The status of the matches to find.
     * @param pageable The pagination information.
     * @return A paginated list of {@link Match} objects.
     */
    Page<Match> findAllByReceiverAndStatus(User receiver, Status status, Pageable pageable);

    /**
     * Finds a paginated list of matches for a specific user (either as sender or receiver) with a given status.
     *
     * @param receiver The user as the receiver of the match.
     * @param sender The user as the sender of the match.
     * @param status The status of the matches to find.
     * @param pageable The pagination information.
     * @return A paginated list of {@link Match} objects.
     */
    @Query("SELECT m FROM Match m WHERE (m.receiver = :receiver OR m.sender = :sender) AND m.status = :status")
    Page<Match> findAllByReceiverAndStatusOrSenderAndStatus(
            @Param("receiver") User receiver,
            @Param("sender") User sender,
            @Param("status") Status status,
            Pageable pageable
    );

    /**
     * Counts the number of matches for a specific user (either as sender or receiver) with a given status.
     *
     * @param user The user for whom to count the matches.
     * @param status The status of the matches to count.
     * @return The total number of matches.
     */
    @Query("SELECT COUNT(m) FROM Match m WHERE (m.receiver = :user OR m.sender = :user) AND m.status = :status")
    Integer countMatches(@Param("user") User user, @Param("status") Status status);

}
