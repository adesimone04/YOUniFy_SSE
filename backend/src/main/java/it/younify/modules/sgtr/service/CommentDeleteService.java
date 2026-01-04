package it.younify.modules.sgtr.service;

import it.younify.core.exception.ResourceNotFoundException;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.model.entity.Comment;
import it.younify.modules.sgtr.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentDeleteService {

    private final CommentRepository commentRepository;

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long commentId) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Comment> optC = commentRepository.findByID(commentId);

        if(optC.isPresent()) {

            if(principal instanceof User) {

                Comment comment = optC.get();

                User userP = (User) principal;
                User userC = comment.getUser();

                if((userP.getID()).equals(userC.getID())){

                    commentRepository.delete(comment);
                }
            }
            else{

                if(principal instanceof Host){

                    Comment comment = optC.get();

                    Host hostP = (Host) principal;
                    Host hostC = comment.getHost();

                    if((hostC.getID()).equals(hostC.getID())){

                        commentRepository.delete(comment);
                    }
                }
            }
        }
        else throw new ResourceNotFoundException("Comment not found");
    }
}
