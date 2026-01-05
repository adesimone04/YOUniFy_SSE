package it.younify.modules.sgtr.repository;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.model.entity.Comment;
import it.younify.modules.sgtr.model.entity.CommentVote;
import it.younify.modules.sgtr.model.entity.Threads;
import it.younify.modules.sgtr.model.entity.ThreadsVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentVoteRepository extends JpaRepository<CommentVote, Long> {

    Optional<CommentVote> findByID(Long id);
    Optional<CommentVote> findByCommentAndUser(Comment comment, User user);
    Optional<CommentVote> findByCommentAndHost(Comment comment, Host Host);
}
