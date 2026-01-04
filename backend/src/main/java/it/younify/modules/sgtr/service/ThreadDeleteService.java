package it.younify.modules.sgtr.service;


import it.younify.core.exception.ResourceNotFoundException;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.model.entity.Threads;
import it.younify.modules.sgtr.repository.ThreadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ThreadDeleteService {

    private final ThreadRepository threadRepository;

    public void delete(Long postId) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Threads> opt = threadRepository.findByID(postId);
        if (opt.isPresent()) {
            if(principal instanceof User) {

                User user = (User) principal;


                Threads thread = opt.get();
                User userT = thread.getIDUser();

                if((userT.getID()).equals(user.getID())){

                    threadRepository.delete(thread);
                }

            }else if(principal instanceof Host){

                Host host = (Host) principal;


                Threads thread = opt.get();
                Host hostT = thread.getIDHost();

                if((hostT.getID()).equals(host.getID())){

                    threadRepository.delete(thread);
                }
            }
        }
        else throw new ResourceNotFoundException("Thread not found");
    }
}
