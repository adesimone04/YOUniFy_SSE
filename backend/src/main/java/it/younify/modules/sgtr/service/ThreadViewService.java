package it.younify.modules.sgtr.service;


import it.younify.core.service.ImageStorageService;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.model.dto.response.ThreadResponse;
import it.younify.modules.sgtr.model.entity.FlagCategory;
import it.younify.modules.sgtr.model.entity.Threads;
import it.younify.modules.sgtr.model.entity.ThreadsVote;
import it.younify.modules.sgtr.model.entity.VoteType;
import it.younify.modules.sgtr.repository.ThreadRepository;
import it.younify.modules.sgtr.repository.ThreadVoteRepository;
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
import java.util.stream.Collectors;


/**
 * The {@code ThreadViewService} class provides methods for retrieving and viewing threads.
 * It handles the logic for fetching threads based on different criteria and converting them
 * into a client-friendly response format, including the current user's vote on each thread.
 *
 * @see ThreadRepository
 * @see ThreadVoteRepository
 * @see ThreadResponse
 */
@Service
@RequiredArgsConstructor
public class ThreadViewService {

    private final ImageStorageService imageStorageService;

    private final ThreadRepository threadRepository;
    private final ThreadVoteRepository threadVoteRepository;

    /**
     * Retrieves a paginated list of all threads, sorted by publication date in descending order.
     * Each thread in the response includes the current user's vote type.
     *
     * @param account The currently authenticated user or host.
     * @param pageable The pagination information.
     * @return A paginated list of {@link ThreadResponse} objects.
     */
    @Transactional(readOnly = true)
    public Page<ThreadResponse> getThreadsByDate(@NonNull UserDetails account, @NonNull Pageable pageable) {

        Page<Threads> threadsPage = threadRepository.findAllByOrderByPublicationDateDesc(pageable);

        Map<Long, VoteType> finalVotesMap = getThreadsVote(account, threadsPage);

        return threadsPage.map(thread -> toResponse(thread, (finalVotesMap.get(thread.getID()))));
    }

    /**
     * Retrieves a paginated list of threads filtered by a specific flag category,
     * sorted by publication date in descending order.
     * Each thread in the response includes the current user's vote type.
     *
     * @param account The currently authenticated user or host.
     * @param flag The flag category to filter by.
     * @param pageable The pagination information.
     * @return A paginated list of {@link ThreadResponse}.
     */
    @Transactional(readOnly = true)
    public Page<ThreadResponse> getThreadsByFlag(@NonNull UserDetails account, @NonNull FlagCategory flag, @NonNull Pageable pageable) {

        Page<Threads> threadsPage = threadRepository.findAllByFlagOrderByPublicationDateDesc(flag, pageable);

        Map<Long, VoteType> finalVotesMap = getThreadsVote(account, threadsPage);

        return threadsPage.map(thread -> toResponse(thread, (finalVotesMap.get(thread.getID()))));
    }

    /**
     * Retrieves a paginated list of threads filtered by a specific university,
     * sorted by publication date in descending order.
     * This method searches for threads posted by both users and hosts associated with the given university.
     * Each thread in the response includes the current user's vote type.
     *
     * @param account The currently authenticated user or host.
     * @param university The university to filter by.
     * @param pageable The pagination information.
     * @return A paginated list of {@link ThreadResponse}.
     */
    @Transactional(readOnly = true)
    public Page<ThreadResponse> getThreadsByUniversity(@NonNull UserDetails account, @NonNull String university, @NonNull Pageable pageable) {

        Page<Threads> threadsPage = threadRepository.
                findByHost_UniListContainingIgnoreCaseOrUser_UniversityIgnoreCaseOrderByPublicationDateDesc(university, university, pageable);

        Map<Long, VoteType> finalVotesMap = getThreadsVote(account, threadsPage);

        return threadsPage.map(thread -> toResponse(thread, (finalVotesMap.get(thread.getID()))));
    }

    /**
     * Retrieves a paginated list of threads filtered by a specific university and flag category,
     * sorted by publication date in descending order.
     * Each thread in the response includes the current user's vote type.
     *
     * @param account The currently authenticated user or host.
     * @param university The university to filter by.
     * @param flag The flag category to filter by.
     * @param pageable The pagination information.
     * @return A paginated list of {@link ThreadResponse}.
     */
    @Transactional(readOnly = true)
    public Page<ThreadResponse> getThreadsByUniversityAndFlag(@NonNull UserDetails account, @NonNull String university,
                                                              @NonNull FlagCategory flag, @NonNull Pageable pageable) {

        Page<Threads> threadsPage = threadRepository.findByUniversityAndFlag(university, flag, pageable);

        Map<Long, VoteType> finalVotesMap = getThreadsVote(account, threadsPage);

        return threadsPage.map(thread -> toResponse(thread, (finalVotesMap.get(thread.getID()))));
    }

    /**
     * Retrieves a paginated list of threads posted by a specific user.
     * The results are ordered by publication date in descending order.
     * Each thread in the response includes the current user's vote type.
     *
     * @param user The user who posted the threads.
     * @param pageable The pagination information.
     * @return A {@link Page} of {@link ThreadResponse} objects.
     * @see User
     * @see Page
     */
    @Transactional(readOnly = true)
    public Page<ThreadResponse> getThreadsPostedByUser(@NonNull User user, @NonNull Pageable pageable) {

        Page<Threads> threadsPage = threadRepository.findByUserOrderByPublicationDateDesc(user, pageable);

        Map<Long, VoteType> finalVotesMap = getThreadsVote(user, threadsPage);

        return threadsPage.map(thread -> toResponse(thread, (finalVotesMap.get(thread.getID()))));
    }

    /**
     * Retrieves a paginated list of threads posted by a specific host.
     * The results are ordered by publication date in descending order.
     * Each thread in the response includes the current user's vote type.
     *
     * @param host The host who posted the threads.
     * @param pageable The pagination information.
     * @return A {@link Page} of {@link ThreadResponse} objects.
     * @see Host
     * @see Page
     */
    @Transactional(readOnly = true)
    public Page<ThreadResponse> getThreadsPostedByHost(@NonNull Host host, @NonNull Pageable pageable) {

        Page<Threads> threadsPage = threadRepository.findByHostOrderByPublicationDateDesc(host, pageable);

        Map<Long, VoteType> finalVotesMap = getThreadsVote(host, threadsPage);

        return threadsPage.map(thread -> toResponse(thread, (finalVotesMap.get(thread.getID()))));
    }


    /**
     * Fetches the vote type for each thread in a given page for a specific account.
     * This method efficiently retrieves all votes for the threads on the current page in a single query.
     *
     * @param account The currently authenticated user or host.
     * @param threadsPage The page of threads for which to fetch votes.
     * @return A map where the key is the thread ID and the value is the {@link VoteType}.
     */
    private Map<Long, VoteType> getThreadsVote(UserDetails account, Page<Threads> threadsPage) {

        Map<Long, VoteType> votesMap = new HashMap<>();

        if (!threadsPage.isEmpty()) {

            List<ThreadsVote> votes;

            if (account instanceof User) {

                votes = threadVoteRepository.findByUserAndThreadIn((User) account, threadsPage.getContent());
            }
            else votes = threadVoteRepository.findByHostAndThreadIn((Host) account, threadsPage.getContent());

            votesMap = votes.stream().collect(Collectors.toMap(
                    v -> v.getThread().getID(),
                    ThreadsVote::getVoteType
            ));
        }
        return votesMap;
    }


    /**
     * Converts a {@link Threads} entity into a {@link ThreadResponse} DTO.
     * This method handles the mapping of author information, whether the author is a {@link User}
     * or a {@link Host}, and includes the current user's vote on the thread.
     *
     * @param thread The thread entity to convert.
     * @param accountVote The vote type of the current user on this thread.
     * @return The corresponding {@link ThreadResponse}.
     */
    private ThreadResponse toResponse(Threads thread, VoteType accountVote) {

        if(thread.getUser() != null){

            return new ThreadResponse(
                    thread.getID(),
                    thread.getContent(),
                    thread.getCFU(),
                    thread.getFlag(),
                    thread.getUser().getUniversity(),
                    thread.getPublicationDate(),
                    "",
                    thread.getUser().getFirstName() + " " + thread.getUser().getLastName(),
                    imageStorageService.getPresignedUrl(thread.getUser().getProfileImage()),
                    accountVote
            );
        }
        else{

            return new ThreadResponse(
                    thread.getID(),
                    thread.getContent(),
                    thread.getCFU(),
                    thread.getFlag(),
                    thread.getHost().getUniList(),
                    thread.getPublicationDate(),
                    thread.getHost().getHostName(),
                    "",
                    imageStorageService.getPresignedUrl(thread.getHost().getProfileImage()),
                    accountVote
            );
        }
    }
}
