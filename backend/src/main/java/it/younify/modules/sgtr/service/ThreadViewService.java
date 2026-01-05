package it.younify.modules.sgtr.service;


import it.younify.modules.sgtr.model.dto.response.ThreadResponse;
import it.younify.modules.sgtr.model.entity.Threads;
import it.younify.modules.sgtr.repository.ThreadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ThreadViewService {

    private final ThreadRepository threadRepository;

    public Page<ThreadResponse> getThreadsByDate(Pageable pageable) {

        return threadRepository.findAllByOrderByPublicationDateDesc(pageable).map(this::toResponse);
    }

    public Page<ThreadResponse> getThreadsByFlag(String flag, Pageable pageable) {
        return threadRepository.findAllByFlagOrderByPublicationDateDesc(flag, pageable).map(this::toResponse);
    }

    public Page<ThreadResponse> getThreadsByUniversity(String university, Pageable pageable) {
        return threadRepository.findByHost_UniListContainingIgnoreCaseOrUser_UniversityIgnoreCaseOrderByPublicationDateDesc(university, university, pageable).map(this::toResponse);
    }


    private ThreadResponse toResponse(Threads thread) {

        if(thread.getUser() != null){

            return new ThreadResponse(
                    thread.getID(),
                    thread.getContent(),
                    thread.getCFU(),
                    thread.getFlag(),
                    thread.getUser().getUniversity(),
                    thread.getPublicationDate(),
                    "",
                    thread.getUser().getFirstName()+" "+thread.getUser().getLastName()
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
                    ""
            );
        }
    }
}
