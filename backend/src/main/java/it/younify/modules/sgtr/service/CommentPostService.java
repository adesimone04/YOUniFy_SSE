package it.younify.modules.sgtr.service;

import it.younify.core.exception.ResourceNotFoundException;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.model.dto.request.CommentPostRequest;
import it.younify.modules.sgtr.model.entity.Comment;
import it.younify.modules.sgtr.model.entity.Threads;
import it.younify.modules.sgtr.repository.CommentRepository;
import it.younify.modules.sgtr.repository.ThreadRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentPostService {

    private final ThreadRepository threadRepository;
    private final CommentRepository commentRepository;
    private final CensorshipService censorshipService;

    public void post(@NotNull CommentPostRequest commentPostRequest) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String content = commentPostRequest.getContent();
        Long id = commentPostRequest.getThreadId();


        if(censorshipService.containsSensitiveContent(content)){

            content = censorshipService.censorText(content);
        }

        Optional<Threads> optT = threadRepository.findById(id);

        if (optT.isPresent()) {

            Threads thread = optT.get();

            if (principal instanceof User) {

                commentRepository.save(
                        Comment.builder()
                                .CFU(0)
                                .content(content)
                                .publicationDate(Instant.now())
                                .threadId(thread)
                                .user((User) principal)
                                .build()
                );
            }
            else {

                if (principal instanceof Host) {

                    commentRepository.save(
                            Comment.builder()
                                    .CFU(0)
                                    .content(content)
                                    .publicationDate(Instant.now())
                                    .threadId(thread)
                                    .host((Host) principal)
                                    .build()
                    );
                }
            }
        }
        else throw new ResourceNotFoundException("Thread not found");
    }
}