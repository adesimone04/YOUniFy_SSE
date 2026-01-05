package it.younify.modules.sgtr.service;

import it.younify.core.exception.ResourceNotFoundException;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.model.dto.request.CommentVoteRequest;
import it.younify.modules.sgtr.model.entity.Comment;
import it.younify.modules.sgtr.model.entity.CommentVote;
import it.younify.modules.sgtr.model.entity.VoteType;
import it.younify.modules.sgtr.repository.CommentRepository;
import it.younify.modules.sgtr.repository.CommentVoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentVoteService {

    private final CommentVoteRepository commentVoteRepository;
    private final CommentRepository commentRepository;

    @Transactional(rollbackFor = Exception.class)
    public void vote(CommentVoteRequest commentVoteRequest){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        VoteType accountVoteType = commentVoteRequest.getVoteType();

        Optional<Comment> optC = commentRepository.findById(commentVoteRequest.getCommentId());

        if(optC.isPresent()) {

            Comment comment = optC.get();

            if(principal instanceof User){

                Optional<CommentVote> vote = commentVoteRepository.findByCommentAndUser(comment, ((User) principal));

                if(vote.isPresent()) {

                    CommentVote commentVote = vote.get();
                    changeVote(commentVote, accountVoteType, comment);

                }
                else{

                    commentVoteRepository.save(
                            CommentVote.builder()
                                    .voteType(accountVoteType)
                                    .comment(comment)
                                    .user((User) principal)
                                    .build()
                    );

                    if(accountVoteType.equals(VoteType.UPVOTE)){

                        comment.setCFU(comment.getCFU() + 1);
                    }
                    else comment.setCFU(comment.getCFU() - 1);
                }

                commentRepository.save(comment);
            }
            else{

                if(principal instanceof Host){

                    Optional<CommentVote> vote = commentVoteRepository.findByCommentAndHost(comment, ((Host) principal));

                    if(vote.isPresent()) {

                        CommentVote commentVote = vote.get();
                        changeVote(commentVote, accountVoteType, comment);
                    }
                    else{

                        commentVoteRepository.save(
                                CommentVote.builder()
                                        .voteType(accountVoteType)
                                        .comment(comment)
                                        .host((Host) principal)
                                        .build()
                        );

                        if(accountVoteType.equals(VoteType.UPVOTE)){

                            comment.setCFU(comment.getCFU() + 1);
                        }
                        else comment.setCFU(comment.getCFU() - 1);
                    }

                    commentRepository.save(comment);
                }
            }
        } else throw new ResourceNotFoundException("Thread not found");
    }


    private void changeVote(CommentVote commentVote, VoteType accountVoteType, Comment comment){


        if(commentVote.getVoteType().equals(accountVoteType)) {

            commentVoteRepository.delete(commentVote);

            if(accountVoteType.equals(VoteType.UPVOTE)){

                comment.setCFU(comment.getCFU() - 1);
            }
            else comment.setCFU(comment.getCFU() + 1);
        }
        else{

            if(accountVoteType.equals(VoteType.UPVOTE)){

                comment.setCFU(comment.getCFU() + 2);
                commentVote.setVoteType(VoteType.UPVOTE);

            }
            else{

                comment.setCFU(comment.getCFU() - 2);
                commentVote.setVoteType(VoteType.DOWNVOTE);
            }

            commentVoteRepository.save(commentVote);
        }
    }
}

