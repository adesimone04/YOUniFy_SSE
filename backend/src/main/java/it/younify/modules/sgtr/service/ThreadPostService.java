package it.younify.modules.sgtr.service;


import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.model.dto.request.ThreadPostRequest;
import it.younify.modules.sgtr.model.entity.Threads;
import it.younify.modules.sgtr.repository.ThreadRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ThreadPostService {

    private final ThreadRepository threadRepository;

    public void post(@NotNull ThreadPostRequest threadPostRequest) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User) {
            threadRepository.save(
                    Threads.builder()
                            .content(threadPostRequest.getContent())
                            .CFU(0)
                            .publicationDate(Instant.now())
                            .IDUser((User) principal)
                            .build()
            );
        }else if (principal instanceof Host) {
            threadRepository.save(
                    Threads.builder()
                            .content(threadPostRequest.getContent())
                            .CFU(0)
                            .publicationDate(Instant.now())
                            .IDHost((Host) principal)
                            .build()
            );
        }
    }

}
