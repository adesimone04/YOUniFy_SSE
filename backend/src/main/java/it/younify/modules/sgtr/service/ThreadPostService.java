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
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Service
@RequiredArgsConstructor
public class ThreadPostService {

    private final ThreadRepository threadRepository;
    private final CensorshipService censorshipService;

    @Transactional(rollbackFor = Exception.class)
    public void post(@NotNull ThreadPostRequest threadPostRequest) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String content = threadPostRequest.getContent();
        if (censorshipService.containsSensitiveContent(content)) {

            content = censorshipService.censorText(content);
        }


        if (principal instanceof User) {

            threadRepository.save(
                    Threads.builder()
                            .content(content)
                            .CFU(0)
                            .flag(threadPostRequest.getFlag())
                            .publicationDate(Instant.now())
                            .User((User) principal)
                            .build()
            );
        }
        else {

            if (principal instanceof Host) {

                threadRepository.save(
                        Threads.builder()
                                .content(content)
                                .CFU(0)
                                .flag(threadPostRequest.getFlag())
                                .publicationDate(Instant.now())
                                .Host((Host) principal)
                                .build()
                );
            }
        }
    }
}
