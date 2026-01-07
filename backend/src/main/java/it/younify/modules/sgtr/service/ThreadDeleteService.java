package it.younify.modules.sgtr.service;


import it.younify.core.exception.ResourceNotFoundException;
import it.younify.core.utils.SecurityUtils;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.model.entity.Threads;
import it.younify.modules.sgtr.repository.ThreadRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ThreadDeleteService {

    private final ThreadRepository threadRepository;

    @Transactional(rollbackFor = Exception.class)
    public void delete(@NotNull Long threadId) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Threads> optT = threadRepository.findByID(threadId);

        if(optT.isPresent()) {

            Threads thread = optT.get();

            if(principal instanceof User) {

                User userC = (User) principal;
                User userT = thread.getUser();

                if((userT.getID()).equals(userC.getID())){

                    threadRepository.delete(thread);
                }
            }
            else{

                if(principal instanceof Host){

                    Host hostC = (Host) principal;
                    Host hostT = thread.getHost();

                    if((hostT.getID()).equals(hostC.getID())){

                        threadRepository.delete(thread);
                    }
                }
                else {

                    if (SecurityUtils.isAdmin()) {

                        threadRepository.delete(thread);
                    }
                }
            }
        }
        else throw new ResourceNotFoundException("Thread not found");
    }
}
