package it.younify.modules.sgtr.service;


import it.younify.core.exception.ResourceNotFoundException;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.model.dto.response.CommentResponse;
import it.younify.modules.sgtr.model.entity.Comment;
import it.younify.modules.sgtr.model.entity.Threads;
import it.younify.modules.sgtr.repository.CommentRepository;
import it.younify.modules.sgtr.repository.ThreadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


/**
 * The {@code CommentViewService} class provides methods for retrieving and viewing comments.
 * It handles the logic for fetching comments for a specific thread and converting them
 * into a client-friendly response format.
 *
 * @see it.younify.modules.sgtr.repository.CommentRepository
 * @see it.younify.modules.sgtr.repository.ThreadRepository
 * @see it.younify.modules.sgtr.model.dto.response.CommentResponse
 */
@Service
@RequiredArgsConstructor
public class CommentViewService {

    private final CommentRepository commentRepository;
    private final ThreadRepository threadRepository;

    /**
     * Retrieves a paginated list of comments for a specific thread, sorted by vote count (CFU) in descending order.
     *
     * @param threadId The ID of the thread to retrieve comments for.
     * @param pageable The pagination information.
     * @return A paginated list of {@link CommentResponse}.
     * @throws ResourceNotFoundException if the thread with the given ID does not exist.
     */
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentByCFU(Long threadId, Pageable pageable) {

        Optional<Threads> optT = threadRepository.findById(threadId);

        if(optT.isPresent()) {

            Threads thread = optT.get();
            return commentRepository.findByThreadIdOrderByCFUDesc(thread, pageable).map(this::toResponse);
        }

       throw new ResourceNotFoundException("Thread not found", "THREAD_NOT_FOUND");
    }

    /**
     * Retrieves a paginated list of comments posted by a specific user.
     * The results are ordered by publication date in descending order.
     *
     * @param user The user who posted the comments.
     * @param pageable The pagination information.
     * @return A {@link Page} of {@link CommentResponse} objects.
     * @see User
     * @see Page
     */
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentPostedByUser(User user, Pageable pageable) {

        return commentRepository.findByUserOrderByPublicationDateDesc(user, pageable).map(this::toResponse);
    }

    /**
     * Retrieves a paginated list of comments posted by a specific host.
     * The results are ordered by publication date in descending order.
     *
     * @param host The host who posted the comments.
     * @param pageable The pagination information.
     * @return A {@link Page} of {@link CommentResponse} objects.
     * @see Host
     * @see Page
     */
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentPostedByHost(Host host, Pageable pageable) {

        return commentRepository.findByHostOrderByPublicationDateDesc(host, pageable).map(this::toResponse);
    }


    /**
     * Converts a {@link Comment} entity into a {@link CommentResponse} DTO.
     * This method handles the mapping of author information, whether the author is a {@link it.younify.modules.sgac.model.entity.User}
     * or a {@link it.younify.modules.sgac.model.entity.Host}.
     *
     * @param comment The comment entity to convert.
     * @return The corresponding {@link CommentResponse}.
     */
    private CommentResponse toResponse(Comment comment) {

        if(comment.getUser() != null){

            return new CommentResponse(
                    comment.getID(),
                    comment.getContent(),
                    comment.getCFU(),
                    comment.getPublicationDate(),
                    "",
                    comment.getUser().getFirstName() + " " + comment.getUser().getLastName()
            );
        }
        else{

            return new CommentResponse(
                    comment.getID(),
                    comment.getContent(),
                    comment.getCFU(),
                    comment.getPublicationDate(),
                    comment.getHost().getHostName(),
                    ""
            );
        }
    }
}
