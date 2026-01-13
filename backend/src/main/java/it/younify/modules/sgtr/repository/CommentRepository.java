package it.younify.modules.sgtr.repository;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.model.entity.Comment;
import it.younify.modules.sgtr.model.entity.Threads;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * The {@code CommentRepository} interface is a Spring Data JPA repository for {@link Comment} entities.
 * It provides standard CRUD operations and custom query methods for finding comments based on various criteria.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Finds a comment by its unique ID.
     *
     * @param id The ID of the comment.
     * @return An {@link Optional} containing the comment if found.
     */
    Optional<Comment> findById(Long id);

    /**
     * Finds a comment by its author.
     *
     * @param user The user who authored the comment.
     * @return An {@link Optional} containing a comment if found.
     */
    Optional<Comment> findByUser(User user);

    /**
     * Finds a comment by its author.
     *
     * @param host The host who authored the comment.
     * @return An {@link Optional} containing a comment if found.
     */
    Optional<Comment> findByHost(Host host);

    /**
     * Finds a comment by its exact publication date.
     *
     * @param publicationDate The publication date to search for.
     * @return An {@link Optional} containing a comment if found.
     */
    Optional<Comment> findByPublicationDate(Instant publicationDate);

    /**
     * Finds all comments, ordered by vote count (CFU) in ascending order.
     *
     * @return A list of all comments.
     */
    List<Comment> findAllByOrderByCFUAsc();

    /**
     * Finds all comments, ordered by vote count (CFU) in descending order.
     *
     * @return A list of all comments.
     */
    List<Comment> findAllByOrderByCFUDesc();

    /**
     * Retrieves a paginated list of comments for a specific thread, sorted by vote count (CFU) in descending order.
     *
     * @param thread The thread to retrieve comments for.
     * @param pageable The pagination information.
     * @return A paginated list of comments.
     */
    Page<Comment> findByThreadIdOrderByCFUDesc(Threads thread, Pageable pageable);

    /**
     * Retrieves a paginated list of comments for a specific thread, sorted by publication date in descending order.
     *
     * @param thread The thread to retrieve comments for.
     * @param pageable The pagination information.
     * @return A paginated list of comments.
     */
    Page<Comment> findByThreadIdOrderByPublicationDateDesc(Threads thread, Pageable pageable);

    /**
     * Calculates the total sum of CFU (votes) for all comments posted by a specific user.
     * This query uses {@code COALESCE} to ensure it returns 0 if the user has made no comments,
     * which is crucial for calculating a user's total reputation score.
     *
     * @param user The user whose comment scores are to be summed.
     * @return The total CFU score from the user's comments.
     */
    @Query("SELECT COALESCE(SUM(c.CFU), 0) FROM Comment c WHERE c.user = :user_id")
    int sumCFUByUserComment(@Param("user_id") User user);

    /**
     * Calculates the total sum of CFU (votes) for all comments posted by a specific host.
     * This query uses {@code COALESCE} to ensure it returns 0 if the host has made no comments,
     * which is crucial for calculating a host's total reputation score.
     *
     * @param host The host whose comment scores are to be summed.
     * @return The total CFU score from the host's comments.
     */
    @Query("SELECT COALESCE(SUM(c.CFU), 0) FROM Comment c WHERE c.host= :host_id")
    int sumCFUByHostComment(@Param("host_id") Host host);
}
