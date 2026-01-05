package it.younify.modules.sgtr.service;

import it.younify.core.exception.ResourceNotFoundException;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.model.dto.request.ThreadVoteRequest;
import it.younify.modules.sgtr.model.entity.Threads;
import it.younify.modules.sgtr.model.entity.ThreadsVote;
import it.younify.modules.sgtr.model.entity.VoteType;
import it.younify.modules.sgtr.repository.ThreadRepository;
import it.younify.modules.sgtr.repository.ThreadVoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ThreadVoteService {

    private final ThreadVoteRepository threadVoteRepository;
    private final ThreadRepository threadRepository;

    @Transactional(rollbackFor = Exception.class)
    public void vote(ThreadVoteRequest threadVoteRequest){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        VoteType accountVoteType = threadVoteRequest.getVoteType();

        Optional<Threads> optT = threadRepository.findById(threadVoteRequest.getThreadId());

        if(optT.isPresent()) {

            Threads thread = optT.get();

            if(principal instanceof User){

                Optional<ThreadsVote> vote = threadVoteRepository.findByThreadAndUser(thread, ((User) principal));

                if(vote.isPresent()) {

                    ThreadsVote threadsVote = vote.get();
                    changeVote(threadsVote, accountVoteType, thread);

                }
                else{

                    threadVoteRepository.save(
                            ThreadsVote.builder()
                                    .voteType(accountVoteType)
                                    .thread(thread)
                                    .user((User) principal)
                                    .build()
                    );

                    if(accountVoteType.equals(VoteType.UPVOTE)){

                        thread.setCFU(thread.getCFU() + 1);
                    }
                    else thread.setCFU(thread.getCFU() - 1);
                }

                threadRepository.save(thread);
            }
            else{

                if(principal instanceof Host){

                    Optional<ThreadsVote> vote = threadVoteRepository.findByThreadAndHost(thread, ((Host) principal));

                    if(vote.isPresent()) {

                        ThreadsVote threadsVote = vote.get();
                        changeVote(threadsVote, accountVoteType, thread);
                    }
                    else{

                        threadVoteRepository.save(
                                ThreadsVote.builder()
                                        .voteType(accountVoteType)
                                        .thread(thread)
                                        .host((Host) principal)
                                        .build()
                        );

                        if(accountVoteType.equals(VoteType.UPVOTE)){

                            thread.setCFU(thread.getCFU() + 1);
                        }
                        else thread.setCFU(thread.getCFU() - 1);
                    }

                    threadRepository.save(thread);
                }
            }
        } else throw new ResourceNotFoundException("Thread not found");
    }


    private void changeVote(ThreadsVote threadsVote, VoteType accountVoteType, Threads thread){


        if(threadsVote.getVoteType().equals(accountVoteType)) {

            threadVoteRepository.delete(threadsVote);

            if(accountVoteType.equals(VoteType.UPVOTE)){

                thread.setCFU(thread.getCFU() - 1);
            }
            else thread.setCFU(thread.getCFU() + 1);
        }
        else{

            if(accountVoteType.equals(VoteType.UPVOTE)){

                thread.setCFU(thread.getCFU() + 2);
                threadsVote.setVoteType(VoteType.UPVOTE);

            }
            else{

                thread.setCFU(thread.getCFU() - 2);
                threadsVote.setVoteType(VoteType.DOWNVOTE);
            }

            threadVoteRepository.save(threadsVote);
        }
    }
}
