package it.younify.modules.sgtr.service;


import it.younify.core.exception.ResourceNotFoundException;
import it.younify.core.service.ImageStorageService;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.model.dto.response.CommentResponse;
import it.younify.modules.sgtr.model.entity.*;
import it.younify.modules.sgtr.repository.CommentRepository;
import it.younify.modules.sgtr.repository.CommentVoteRepository;
import it.younify.modules.sgtr.repository.ThreadRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * The {@code CommentViewService} class provides methods for retrieving and viewing comments.
 * It handles the logic for fetching comments for a specific thread and converting them
 * into a client-friendly response format, including the current user's vote on each comment.
 *
 * @see CommentRepository
 * @see ThreadRepository
 * @see CommentVoteRepository
 * @see CommentResponse
 */
@Service
@RequiredArgsConstructor
public class CommentViewService {

    private final ImageStorageService imageStorageService;

    private final CommentRepository commentRepository;
    private final ThreadRepository threadRepository;
    private final CommentVoteRepository commentVoteRepository;

    /**
     * Retrieves a paginated list of comments for a specific thread, sorted by vote count (CFU) in descending order.
     * Each comment in the response includes the current user's vote type.
     *
     * @param account The currently authenticated user or host.
     * @param threadId The ID of the thread to retrieve comments for.
     * @param pageable The pagination information.
     * @return A paginated list of {@link CommentResponse}.
     * @throws ResourceNotFoundException if the thread with the given ID does not exist.
     */
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentByCFU(@NonNull UserDetails account, @NonNull Long threadId, Pageable pageable) {

        Optional<Threads> optT = threadRepository.findById(threadId);

        if(optT.isPresent()) {

            Threads thread = optT.get();
            Page<Comment> commentPage = commentRepository.findByThreadIdOrderByCFUDesc(thread, pageable);

            Map<Long, VoteType> finalVotesMap = getCommentVote(account, commentPage);

            return commentPage.map(comment -> toResponse(comment, (finalVotesMap.get(comment.getID()))));
        }

       throw new ResourceNotFoundException("Thread not found", "THREAD_NOT_FOUND");
    }

    /**
     * Retrieves a paginated list of comments posted by a specific user.
     * The results are ordered by publication date in descending order.
     * Each comment in the response includes the current user's vote type.
     *
     * @param user The user who posted the comments.
     * @param pageable The pagination information.
     * @return A {@link Page} of {@link CommentResponse} objects.
     * @see User
     * @see Page
     */
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentPostedByUser(@NonNull User user, @NonNull Pageable pageable) {

        Page<Comment> commentPage = commentRepository.findByUserOrderByPublicationDateDesc(user, pageable);

        Map<Long, VoteType> finalVotesMap = getCommentVote(user, commentPage);

        return commentPage.map(comment -> toResponse(comment, (finalVotesMap.get(comment.getID()))));

    }

    /**
     * Retrieves a paginated list of comments posted by a specific host.
     * The results are ordered by publication date in descending order.
     * Each comment in the response includes the current user's vote type.
     *
     * @param host The host who posted the comments.
     * @param pageable The pagination information.
     * @return A {@link Page} of {@link CommentResponse} objects.
     * @see Host
     * @see Page
     */
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentPostedByHost(@NonNull Host host, @NonNull Pageable pageable) {

        Page<Comment> commentPage = commentRepository.findByHostOrderByPublicationDateDesc(host, pageable);

        Map<Long, VoteType> finalVotesMap = getCommentVote(host, commentPage);

        return commentPage.map(comment -> toResponse(comment, (finalVotesMap.get(comment.getID()))));


    }


    /**
     * Fetches the vote type for each comment in a given page for a specific account.
     * This method efficiently retrieves all votes for the comments on the current page in a single query.
     *
     * @param account The currently authenticated user or host.
     * @param commentPage The page of comments for which to fetch votes.
     * @return A map where the key is the comment ID and the value is the {@link VoteType}.
     */
    private Map<Long, VoteType> getCommentVote(UserDetails account, Page<Comment> commentPage) {

        Map<Long, VoteType> votesMap = new HashMap<>();

        if (!commentPage.isEmpty()) {

            List<CommentVote> votes;

            if (account instanceof User) {

                votes = commentVoteRepository.findByUserAndCommentIn((User) account, commentPage.getContent());
            }
            else votes = commentVoteRepository.findByHostAndCommentIn((Host) account, commentPage.getContent());

            votesMap = votes.stream().collect(Collectors.toMap(
                    v -> v.getComment().getID(),
                    CommentVote::getVoteType
            ));
        }
        return votesMap;
    }


    /**
     * Converts a {@link Comment} entity into a {@link CommentResponse} DTO.
     * This method handles the mapping of author information, whether the author is a {@link User}
     * or a {@link Host}, and includes the current user's vote on the comment.
     *
     * @param comment The comment entity to convert.
     * @param accountVote The vote type of the current user on this comment.
     * @return The corresponding {@link CommentResponse}.
     */
    private CommentResponse toResponse(Comment comment, VoteType accountVote) {

        if(comment.getUser() != null){

            return new CommentResponse(
                    comment.getID(),
                    comment.getContent(),
                    comment.getCFU(),
                    comment.getPublicationDate(),
                    "",
                    comment.getUser().getFirstName() + " " + comment.getUser().getLastName(),
                    imageStorageService.getPresignedUrl(comment.getUser().getProfileImage()),
                    accountVote
            );
        }
        else{

            return new CommentResponse(
                    comment.getID(),
                    comment.getContent(),
                    comment.getCFU(),
                    comment.getPublicationDate(),
                    comment.getHost().getHostName(),
                    "",
                    imageStorageService.getPresignedUrl(comment.getHost().getProfileImage()),
                    accountVote
            );
        }
    }
}
