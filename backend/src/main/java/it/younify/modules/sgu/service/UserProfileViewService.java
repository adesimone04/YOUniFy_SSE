package it.younify.modules.sgu.service;

import it.younify.core.service.ImageStorageService;
import it.younify.modules.sgac.model.entity.User;
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

            return UserProfileViewResponse.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .profileImage(imageStorageService.getPresignedUrl(user.getProfileImage()))
                    .cdl(user.getCdl())
                    .university(user.getUniversity())
                    .dateOfBirth(user.getDateOfBirth())
                    .contactsInfo(user.getContactsInfo())
                    .build();
        }
        else throw new AccessDeniedException("Access is denied for this operation");
    }
}
