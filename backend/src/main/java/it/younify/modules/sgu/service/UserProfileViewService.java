package it.younify.modules.sgu.service;

import it.younify.core.service.ImageStorageService;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgm.model.entity.Status;
import it.younify.modules.sgm.repository.MatchRepository;
import it.younify.modules.sgtr.repository.CommentRepository;
import it.younify.modules.sgtr.repository.ThreadRepository;
import it.younify.modules.sgu.model.dto.response.UserProfileViewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * This service handles the business logic for viewing a user's profile information.
 * It retrieves the data for the currently authenticated user and maps it to a response DTO.
 *
 * @see UserProfileViewResponse
 * @see User
 */
@Service
@RequiredArgsConstructor
public class UserProfileViewService {

    private final ImageStorageService imageStorageService;
    private final MatchRepository matchRepository;
    private final ThreadRepository threadRepository;
    private final CommentRepository commentRepository;

    /**
     * Retrieves the profile information for the currently authenticated user.
     * This method ensures that the principal is a {@link User} before proceeding.
     *
     * @return A {@link UserProfileViewResponse} containing the user's profile information.
     * @throws AccessDeniedException if the authenticated principal is not a {@link User}.
     */
    public UserProfileViewResponse userProfileView() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User) {

            User user = (User) principal;

            int totalCfu = threadRepository.sumCFUByUserThread(user) + commentRepository.sumCFUByUserComment(user);
            int totalMatch = matchRepository.countMatches(user, Status.ACCEPTED);

            return UserProfileViewResponse.builder()
                    .avatar(imageStorageService.getPresignedUrl(user.getProfileImage()))
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .cdl(user.getCdl())
                    .university(user.getUniversity())
                    .dateOfBirth(user.getDateOfBirth())
                    .contactsInfo(user.getContactsInfo())
                    .totalCfu(totalCfu)
                    .totalMatch(totalMatch)
                    .build();
        }
        else throw new AccessDeniedException("Access is denied for this operation");
    }
}
