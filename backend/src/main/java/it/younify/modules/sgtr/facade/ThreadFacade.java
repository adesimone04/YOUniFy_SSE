package it.younify.modules.sgtr.facade;


import it.younify.modules.sgtr.model.dto.request.ThreadPostRequest;
import it.younify.modules.sgtr.model.dto.request.ThreadVoteRequest;
import it.younify.modules.sgtr.model.dto.response.ThreadResponse;
import it.younify.modules.sgtr.model.entity.FlagCategory;
import it.younify.modules.sgtr.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ThreadFacade {

    private final ThreadPostService threadPostService;
    private final ThreadViewService threadViewService;
    private final ThreadDeleteService threadDeleteService;
    private final ThreadVoteService threadVoteService;
    private final CensorshipService censorshipService;

    public void createThread(ThreadPostRequest threadPostRequest){

        String content = threadPostRequest.getContent();

        if (censorshipService.containsSensitiveContent(content)) {

            content = censorshipService.censorText(content);
            threadPostRequest.setContent(content);
        }

        threadPostService.post(threadPostRequest);

    }

    public void deleteThread(Long threadId){

        threadDeleteService.delete(threadId);

    }

    public void voteThread(ThreadVoteRequest threadVoteRequest){

        threadVoteService.vote(threadVoteRequest);

    }

    public Page<ThreadResponse> getThreadsByDate(Pageable pageable){
        return threadViewService.getThreadsByDate(pageable);
    }

    public Page<ThreadResponse> getThreadsByFlag(String flag, Pageable pageable){
        FlagCategory flagCategory = FlagCategory.valueOf(flag);
        return threadViewService.getThreadsByFlag(flagCategory, pageable);
    }

    public Page<ThreadResponse> getThreadsByUniversity(String university, Pageable pageable){
        return threadViewService.getThreadsByUniversity(university, pageable);
    }

    public void refreshCensorship(){
        censorshipService.refresh();
    }

}
