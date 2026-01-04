package it.younify.modules.sgtr.repository;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByID(Long id);
    Optional<Comment> findByUser(User user);
    Optional<Comment> findByHost(Host host);
    Optional<Comment> findByPublicationDate(Instant publicationDate);
    List<Comment> findAllByOrderByCFUAsc();
    List<Comment> findAllByOrderByCFUDesc();
}
