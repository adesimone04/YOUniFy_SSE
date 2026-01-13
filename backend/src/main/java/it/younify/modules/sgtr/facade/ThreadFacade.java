package it.younify.modules.sgtr.facade;


import it.younify.modules.sgtr.model.dto.request.ThreadPostRequest;
import it.younify.modules.sgtr.model.dto.request.ThreadVoteRequest;
import it.younify.modules.sgtr.model.dto.response.ThreadResponse;
import it.younify.modules.sgtr.model.entity.FlagCategory;
import it.younify.modules.sgtr.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * The {@code ThreadFacade} class acts as a high-level entry point for all thread-related operations.
 * It orchestrates multiple services to fulfill complex business logic, such as creating,
 * deleting, voting on, and retrieving threads.
 *
 * @see it.younify.modules.sgtr.service.ThreadPostService
 * @see it.younify.modules.sgtr.service.ThreadViewService
 * @see it.younify.modules.sgtr.service.ThreadDeleteService
 * @see it.younify.modules.sgtr.service.ThreadVoteService
 * @see it.younify.modules.sgtr.service.CensorshipService
 */
@Component
@RequiredArgsConstructor
public class ThreadFacade {

    private final ThreadPostService threadPostService;
    private final ThreadViewService threadViewService;
    private final ThreadDeleteService threadDeleteService;
    private final ThreadVoteService threadVoteService;
    private final CensorshipService censorshipService;
    private final TotalCFUService totalCFUService;

    /**
     * Creates a new thread after checking for and censoring any sensitive content.
     *
     * @param threadPostRequest The request containing the thread's data.
     */
    public void createThread(ThreadPostRequest threadPostRequest){

        String content = threadPostRequest.getContent();

        if (censorshipService.containsSensitiveContent(content)) {

            threadPostRequest.setContent(censorshipService.censorText(content));
        }

        threadPostService.post(threadPostRequest);
    }

    /**
     * Deletes a thread by its ID.
     *
     * @param threadId The ID of the thread to delete.
     */
    public void deleteThread(Long threadId){

        threadDeleteService.delete(threadId);
    }

    /**
     * Casts a vote on a thread.
     *
     * @param threadVoteRequest The request containing the vote information.
     */
    public void voteThread(ThreadVoteRequest threadVoteRequest){

        threadVoteService.vote(threadVoteRequest);
    }

    /**
     * Retrieves a paginated list of threads, sorted by their creation date.
     *
     * @param pageable The pagination information.
     * @return A paginated list of threads.
     */
    public Page<ThreadResponse> getThreadsByDate(Pageable pageable){

        return threadViewService.getThreadsByDate(pageable);
    }

    /**
     * Retrieves a paginated list of threads filtered by a specific flag category.
     *
     * @param flag The string representation of the flag category.
     * @param pageable The pagination information.
     * @return A paginated list of threads matching the flag.
     */
    public Page<ThreadResponse> getThreadsByFlag(String flag, Pageable pageable){

        FlagCategory flagCategory = FlagCategory.valueOf(flag);
        return threadViewService.getThreadsByFlag(flagCategory, pageable);
    }

    /**
     * Retrieves a paginated list of threads filtered by a specific university.
     *
     * @param university The name of the university.
     * @param pageable The pagination information.
     * @return A paginated list of threads from the specified university.
     */
    public Page<ThreadResponse> getThreadsByUniversity(String university, Pageable pageable){

        return threadViewService.getThreadsByUniversity(university, pageable);
    }

    /**
     * Triggers a refresh of the censorship word list from its source.
     * This is typically an administrative action.
     */
    public void refreshCensorship() {

        censorshipService.refresh();
    }

    /**
     * Retrieves the total CFU score for the currently authenticated user.
     */
    public int getCFUTotal() {

        return totalCFUService.getCFUTotal();
    }

}
