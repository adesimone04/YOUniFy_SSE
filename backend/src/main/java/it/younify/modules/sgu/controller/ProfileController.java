package it.younify.modules.sgu.controller;

import it.younify.core.model.ApiResponse;
import it.younify.modules.sgu.facade.ProfileFacade;
import it.younify.modules.sgu.model.dto.request.EditHostInfoRequest;
import it.younify.modules.sgu.model.dto.request.EditUserInfoRequest;
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















}
