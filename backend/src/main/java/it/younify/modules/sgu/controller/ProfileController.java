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

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileFacade profileFacade;

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


    @PreAuthorize("hasRole('USER')")
    @PostMapping("/2FA")
    public ResponseEntity<ApiResponse<Void>> confirm2FAUniversity(@Valid @RequestBody Confirm2FAUniversity confirm2FAUniversity) {

        profileFacade.confirm2FAUniversity(confirm2FAUniversity);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .internalCode("2FA_CHANGE_CONFIRMED")
                        .message("2FA_CHANGE_CONFIRMED")
                        .build());
    }


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


    @PreAuthorize("hasRole('USER')")
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
