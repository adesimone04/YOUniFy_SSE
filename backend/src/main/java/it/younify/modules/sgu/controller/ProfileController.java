package it.younify.modules.sgu.controller;

import it.younify.core.model.ApiResponse;
import it.younify.modules.sgac.model.dto.response.AccountSummary;
import it.younify.modules.sgu.facade.ProfileFacade;
import it.younify.modules.sgu.model.dto.request.*;
import it.younify.modules.sgu.model.dto.response.HostProfileViewResponse;
import it.younify.modules.sgu.model.dto.response.UserProfileViewResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * The {@code ProfileController} class provides RESTful endpoints for managing user and host profiles.
 * It handles HTTP requests for editing and viewing profile information, as well as changing university and password.
 * This controller delegates the business logic to the {@link ProfileFacade}.
 *
 * @see ProfileFacade
 * @see ApiResponse
 */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileFacade profileFacade;

    /**
     * Handles the HTTP PUT request to edit a user's profile.
     * This endpoint is restricted to users with the 'USER' role.
     *
     * @param editUserInfoRequest The request body containing the updated user information.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} indicating the result of the operation.
     */
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/user/edit")
    public ResponseEntity<ApiResponse<Void>> editUserProfile(@Valid @RequestBody EditUserInfoRequest editUserInfoRequest) {

        profileFacade.editUserInfoProfile(editUserInfoRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("EDIT_SUCCESSFULLY")
                        .internalCode("EDIT_SUCCESSFULLY")
                        .build()
        );
    }


    /**
     * Handles the HTTP PUT request to edit a host's profile.
     * This endpoint is restricted to users with the 'HOST' role.
     *
     * @param editHostInfoRequest The request body containing the updated host information.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} indicating the result of the operation.
     */
    @PreAuthorize("hasRole('HOST')")
    @PutMapping("/host/edit")
    public ResponseEntity<ApiResponse<Void>> editHostProfile(@Valid @RequestBody EditHostInfoRequest editHostInfoRequest) {

        profileFacade.editHostInfoProfile(editHostInfoRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("EDIT_SUCCESSFULLY")
                        .internalCode("EDIT_SUCCESSFULLY")
                        .build()
        );
    }



    /**
     * Handles the HTTP PUT request to change a user's university.
     * This endpoint is restricted to users with the 'USER' role.
     *
     * @param editUniversityRequest The request body containing the new university details.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} containing an {@link AccountSummary} for the 2FA process if initiated.
     */
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/change/university")
    public ResponseEntity<ApiResponse<AccountSummary>> changeUniversity(@Valid @RequestBody EditUniversityRequest editUniversityRequest) {

        AccountSummary account = profileFacade.changeUniversity(editUniversityRequest);

        if(account.getExpiryDate() == null){

            return ResponseEntity.ok(
                    ApiResponse.<AccountSummary>builder()
                            .data(account)
                            .message("CHANGE_SUCCESSFULLY")
                            .internalCode("CHANGE_SUCCESSFULLY")
                            .build()
            );
        }

        return ResponseEntity.ok(
                ApiResponse.<AccountSummary>builder()
                        .data(account)
                        .message("2FA_CHANGE_SENT")
                        .internalCode("2FA_CHANGE_SENT")
                        .build()
        );
    }


    /**
     * Handles the HTTP POST request to confirm a university change with a 2FA code.
     * This endpoint is restricted to users with the 'USER' role.
     *
     * @param confirm2FAUniversityRequest The request body containing the 2FA code and new university details.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} indicating the result of the operation.
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/2FA")
    public ResponseEntity<ApiResponse<Void>> confirm2FAUniversity(@Valid @RequestBody Confirm2FAUniversityRequest confirm2FAUniversityRequest) {

        profileFacade.confirm2FAUniversity(confirm2FAUniversityRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .internalCode("2FA_CHANGE_CONFIRMED")
                        .message("2FA_CHANGE_CONFIRMED")
                        .build());
    }


    /**
     * Handles the HTTP PUT request to change a user's or host's password.
     * This endpoint is restricted to users with 'USER' or 'HOST' roles.
     *
     * @param changePassRequest The request body containing the new password.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} indicating the result of the operation.
     */
    @PreAuthorize("hasAnyRole('USER', 'HOST')")
    @PutMapping("/change/password")
    public ResponseEntity<ApiResponse<Void>> confirm2FAUniversity(@Valid @RequestBody ChangePassRequest changePassRequest) {

        profileFacade.changePassword(changePassRequest);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .internalCode("CHANGE_SUCCESSFULLY")
                        .message("CHANGE_SUCCESSFULLY")
                        .build());
    }


    /**
     * Handles the HTTP GET request to view a user's profile.
     * This endpoint is restricted to users with the 'USER' role.
     *
     * @return A {@link ResponseEntity} with an {@link ApiResponse} containing the user's profile information.
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<UserProfileViewResponse>> myUserProfile() {

        UserProfileViewResponse user = profileFacade.UserProfileView();

        return ResponseEntity.ok(
                ApiResponse.<UserProfileViewResponse>builder()
                        .data(user)
                        .internalCode("VIEW_SUCCESSFULLY")
                        .message("VIEW_SUCCESSFULLY")
                        .build());
    }


    /**
     * Handles the HTTP GET request to view a host's profile.
     * This endpoint is restricted to users with the 'HOST' role.
     *
     * @return A {@link ResponseEntity} with an {@link ApiResponse} containing the host's profile information.
     */
    @PreAuthorize("hasRole('HOST')")
    @GetMapping("/host")
    public ResponseEntity<ApiResponse<HostProfileViewResponse>> myHostProfile() {

        HostProfileViewResponse host = profileFacade.HostProfileView();

        return ResponseEntity.ok(
                ApiResponse.<HostProfileViewResponse>builder()
                        .data(host)
                        .internalCode("VIEW_SUCCESSFULLY")
                        .message("VIEW_SUCCESSFULLY")
                        .build());
    }
}
