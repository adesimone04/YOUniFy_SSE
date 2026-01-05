package it.younify.modules.sgtr.service;


import it.younify.core.exception.ResourceNotFoundException;
import it.younify.modules.sgtr.model.dto.response.CommentResponse;
import it.younify.modules.sgtr.model.entity.Comment;
import it.younify.modules.sgtr.model.entity.Threads;
import it.younify.modules.sgtr.repository.CommentRepository;
import it.younify.modules.sgtr.repository.ThreadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CommentViewService {

    private final CommentRepository commentRepository;
    private final ThreadRepository threadRepository;

    public Page<CommentResponse> getCommentByCFU(Long threadId, Pageable pageable) {

        Optional<Threads> optT = threadRepository.findByID(threadId);

        if(optT.isPresent()) {

            Threads thread = optT.get();
            return commentRepository.findByThreadIdOrderByCFUDesc(thread, pageable).map(this::toResponse);
        }

       throw new ResourceNotFoundException("Thread not found");
    }

    private CommentResponse toResponse(Comment comment) {

        if(comment.getUser() != null){

            return new CommentResponse(

                    comment.getID(),
                    comment.getContent(),
                    comment.getCFU(),
                    comment.getPublicationDate(),
                    "",
                    comment.getUser().getFirstName()+" "+comment.getUser().getLastName()
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
