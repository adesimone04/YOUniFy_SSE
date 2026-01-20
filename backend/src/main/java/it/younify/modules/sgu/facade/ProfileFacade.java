package it.younify.modules.sgu.facade;

import it.younify.modules.sgac.model.dto.response.AccountSummary;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgu.model.dto.request.*;
import it.younify.modules.sgu.model.dto.response.HostProfileViewResponse;
import it.younify.modules.sgu.model.dto.response.UserProfileViewResponse;
import it.younify.modules.sgu.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * The {@code ProfileFacade} class provides a simplified interface to the profile management module's functionality.
 * It orchestrates various services to handle profile-related operations for both {@link User} and {@link Host} accounts.
 * This facade is the primary entry point for all profile management operations from the controller layer.
 *
 * @see EditUserInfoService
 * @see EditHostInfoService
 * @see ChangeUniversityService
 * @see Confirm2FAUniversityService
 * @see ChangePasswordService
 * @see UserProfileViewService
 * @see HostProfileViewService
 */
@Component
@RequiredArgsConstructor
public class ProfileFacade {

    private final EditUserInfoService editUserInfoService;
    private final EditHostInfoService editHostInfoService;

    private final ChangeUniversityService changeUniversityService;
    private final Confirm2FAUniversityService confirm2FAUniversityService;
    private final ChangePasswordService changePasswordService;

    private final UserProfileViewService userProfileViewService;
    private final HostProfileViewService hostProfileViewService;


    /**
     * Edits the profile information for the currently authenticated {@link User}.
     *
     * @param editUserInfoRequest The request object containing the new user information.
     * @throws AccessDeniedException if the authenticated principal is not a {@link User}.
     */
    public void editUserInfoProfile(EditUserInfoRequest editUserInfoRequest) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        editUserInfoService.editUserProfile(editUserInfoRequest, (User) principal);
    }


    /**
     * Edits the profile information for the currently authenticated {@link Host}.
     *
     * @param editHostInfoRequest The request object containing the new host information.
     * @throws AccessDeniedException if the authenticated principal is not a {@link Host}.
     */
    public void editHostInfoProfile(EditHostInfoRequest editHostInfoRequest) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        editHostInfoService.editHostProfile(editHostInfoRequest, (Host) principal);
    }


    /**
     * Initiates the process of changing a user's university.
     *
     * @param editUniversityRequest The request object containing the new university details.
     * @return An {@link AccountSummary} of the user's account.
     * @throws AccessDeniedException if the authenticated principal is not a {@link User}.
     */
    public AccountSummary changeUniversity(EditUniversityRequest editUniversityRequest){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return changeUniversityService.changeUniversity(editUniversityRequest, (User) principal);
    }


    /**
     * Confirms a university change using a 2FA code.
     *
     * @param confirm2FAUniversityRequest The request object containing the 2FA code and new university details.
     */
    public void confirm2FAUniversity(Confirm2FAUniversityRequest confirm2FAUniversityRequest){

        confirm2FAUniversityService.confirm2FA(confirm2FAUniversityRequest);
    }


    /**
     * Changes the password for the currently authenticated user or host.
     *
     * @param changePassRequest The request object containing the new password.
     */
    public void changePassword(ChangePassRequest changePassRequest){

        changePasswordService.changePassword(changePassRequest);
    }


    /**
     * Retrieves the profile information for the currently authenticated {@link User}.
     *
     * @return A {@link UserProfileViewResponse} containing the user's profile information.
     */
    public UserProfileViewResponse UserProfileView(){

        return userProfileViewService.userProfileView();
    }


    /**
     * Retrieves the profile information for the currently authenticated {@link Host}.
     *
     * @return A {@link HostProfileViewResponse} containing the host's profile information.
     */
    public HostProfileViewResponse HostProfileView(){

        return hostProfileViewService.hostProfileView();
    }

}
