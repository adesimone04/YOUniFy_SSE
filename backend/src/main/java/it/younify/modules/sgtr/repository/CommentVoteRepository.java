package it.younify.modules.sgtr.repository;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.model.entity.Comment;
import it.younify.modules.sgtr.model.entity.CommentVote;
import it.younify.modules.sgtr.model.entity.Threads;
import it.younify.modules.sgtr.model.entity.ThreadsVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * The {@code CommentVoteRepository} interface is a Spring Data JPA repository for {@link CommentVote} entities.
 * It provides methods to find votes on comments, which is essential for managing user interactions and preventing duplicate votes.
 */
public interface CommentVoteRepository extends JpaRepository<CommentVote, Long> {

    /**
     * Finds a vote cast by a specific user on a specific comment.
     * This is crucial for checking if a user has already voted on a comment.
     *
     * @param comment The comment that was voted on.
     * @param user The user who cast the vote.
     * @return An {@link Optional} containing the vote if it exists.
     */
    Optional<CommentVote> findByCommentAndUser(Comment comment, User user);

    /**
     * Finds a vote cast by a specific host on a specific comment.
     * This is crucial for checking if a host has already voted on a comment.
     *
     * @param comment The comment that was voted on.
     * @param host The host who cast the vote.
     * @return An {@link Optional} containing the vote if it exists.
     */
    Optional<CommentVote> findByCommentAndHost(Comment comment, Host host);

    /**
     * Finds all votes cast by a specific user on a given list of comments.
     * This is useful for efficiently fetching a user's vote status for multiple comments at once.
     *
     * @param user The user whose votes are to be found.
     * @param comments The list of comments to check for votes.
     * @return A list of {@link CommentVote} objects.
     */
    List<CommentVote> findByUserAndCommentIn(User user, List<Comment> comments);

    /**
     * Finds all votes cast by a specific host on a given list of comments.
     * This is useful for efficiently fetching a host's vote status for multiple comments at once.
     *
     * @param host The host whose votes are to be found.
     * @param comments The list of comments to check for votes.
     * @return A list of {@link CommentVote} objects.
     */
    List<CommentVote> findByHostAndCommentIn(Host host, List<Comment> comments);

}
