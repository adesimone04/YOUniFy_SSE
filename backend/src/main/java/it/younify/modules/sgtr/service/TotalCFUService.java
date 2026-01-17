package it.younify.modules.sgtr.service;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.repository.CommentRepository;
import it.younify.modules.sgtr.repository.ThreadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * The {@code TotalCFUService} class is responsible for calculating the total reputation score (CFU)
 * for an authenticated user or host.
 * <p>
 * It aggregates scores from different sources, such as threads and comments, to provide a single,
 * unified reputation metric.
 *
 * @see it.younify.modules.sgtr.repository.ThreadRepository
 * @see it.younify.modules.sgtr.repository.CommentRepository
 */
@Service
@RequiredArgsConstructor
public class TotalCFUService {

    private final ThreadRepository threadRepository;
    private final CommentRepository commentRepository;

    /**
     * Calculates the total CFU score for the currently authenticated principal (user or host).
     * <p>
     * This method retrieves the authenticated principal from the {@link SecurityContextHolder},
     * determines if it is a {@link User} or a {@link Host}, and then queries the repositories
     * to sum the CFU scores from both their threads and comments.
     *
     * @return The total aggregated CFU score.
     * @throws AccessDeniedException if the current principal is not an instance of {@link User} or {@link Host},
     * as this operation is only intended for authenticated application users.
     */
    public int getCFUTotal(){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal instanceof Host) {
            
            return threadRepository.sumCFUByHostThread((Host) principal) + commentRepository.sumCFUByHostComment((Host) principal);
        }
        else{
            
            if(principal instanceof User) {

                return threadRepository.sumCFUByUserThread((User) principal) +  commentRepository.sumCFUByUserComment((User) principal);
            }
            
            throw new AccessDeniedException("Access is denied for this operation");
        }
    }
}
