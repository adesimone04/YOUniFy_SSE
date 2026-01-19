package it.younify.modules.sgu.service;

import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgu.model.dto.response.UserProfileViewResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserProfileViewService {

    public UserProfileViewResponse userProfileView() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User) {

            User user = (User) principal;

            return UserProfileViewResponse.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .cdl(user.getCdl())
                    .university(user.getUniversity())
                    .dateOfBirth(user.getDateOfBirth())
                    .contactsInfo(user.getContactsInfo())
                    .build();
        }
        else throw new AccessDeniedException("Access is denied for this operation");
    }
}
