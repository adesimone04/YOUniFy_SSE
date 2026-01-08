package it.younify.modules.sgac.controller;

import it.younify.core.model.ApiResponse;
import it.younify.modules.sgac.model.dto.request.Authentication2FARequest;
import it.younify.modules.sgac.model.dto.request.Resend2FARequest;
import it.younify.modules.sgac.model.dto.request.RefreshTokenRequest;
import it.younify.modules.sgac.model.dto.response.AccountSummary;
import it.younify.modules.sgac.model.dto.response.AuthenticationResponse;
import it.younify.modules.sgac.service.Authentication2FAService;
import it.younify.modules.sgac.service.Generate2FAService;
import it.younify.modules.sgac.service.LogOutService;
import it.younify.modules.sgac.service.RefreshTokenService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

/**
 * The {@code SessionController} class handles session-related endpoints,
 * such as two-factor authentication, token refreshing, and logging out.
 * This controller is responsible for managing the user's session state
 * after the initial login.
 *
 * @see it.younify.modules.sgac.service.Authentication2FAService
 * @see it.younify.modules.sgac.service.RefreshTokenService
 * @see it.younify.modules.sgac.service.LogOutService
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class SessionController {

    private final Authentication2FAService authentication2FAService;
    private final Generate2FAService generate2FAService;
    private final RefreshTokenService refreshTokenService;
    private final LogOutService logOutService;

    /**
     * Confirms the two-factor authentication for a user.
     * This endpoint is called after a user has successfully logged in
     * and needs to verify their identity using a 2FA code.
     *
     * @param authentication2FARequest The request body containing the 2FA code and the ID of the user or host account.
     * @return A {@link ResponseEntity} containing an {@link ApiResponse} with an {@link AccountSummary} on success.
     * @throws it.younify.core.exception.ResourceNotFoundException If the user is not found.
     * @throws it.younify.core.exception.BadRequestException If the 2FA code is invalid.
     */
    @PostMapping("/2FA")
    public ResponseEntity<ApiResponse<AccountSummary>> confirm2FA(@Valid @RequestBody Authentication2FARequest authentication2FARequest) {

        AccountSummary summary = authentication2FAService.confirm2FA(authentication2FARequest);

        return ResponseEntity.ok(
                ApiResponse.<AccountSummary>builder()
                        .message("2FA_CONFIRMED")
                        .data(summary)
                        .build());

    }

    /**
     * Resends a new two-factor authentication (2FA) code to the user.
     * This endpoint should be used when the original 2FA code has expired or was not received.
     * It generates a new code and returns its expiration time.
     *
     * @param resend2FARequest The request body containing the ID of the user or host account.
     * @return A {@link ResponseEntity} containing an {@link ApiResponse} with the new expiration date of the 2FA code.
     * @throws it.younify.core.exception.ResourceNotFoundException if the account ID does not exist.
     * @throws it.younify.core.exception.BadRequestException if the account ID is already enabled.
     */
    @PostMapping("/resend/2FA")
    public ResponseEntity<ApiResponse<Instant>> confirm2FA(@NotNull @RequestBody Resend2FARequest resend2FARequest) {

        Instant expiryDate = generate2FAService.send2FARequest(resend2FARequest.getID());

        return ResponseEntity.ok(
                ApiResponse.<Instant>builder()
                        .message("2FA_SENT")
                        .data(expiryDate)
                        .build());
    }

    /**
     * Refreshes the access token using a refresh token.
     * This endpoint is used to obtain a new access token without requiring the user to log in again.
     * The refresh token is expected to be provided in a cookie.
     *
     * @param refreshTokenString The refresh token from the "refresh_token" cookie.
     * @return A {@link ResponseEntity} containing {@link ApiResponse} with a new access token in a {@link ResponseCookie}.
     * @throws it.younify.core.exception.TokenRefreshException If the refresh token is not found or is expired in the database.
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> refresh(@CookieValue(name = "refresh_token", required = false) String refreshTokenString) {

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshTokenString);

        AuthenticationResponse response = refreshTokenService.refresh(request);

        ResponseCookie newAccessCookie = ResponseCookie.from("access_token", response.getAccessToken())
                .httpOnly(true)
                .secure(false)  //in production (with https) set on true
                .path("/")
                .maxAge(15 * 60)
                .sameSite("Strict")
                .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newAccessCookie.toString())
                .body(
                        ApiResponse.<Void>builder()
                                .message("TOKEN_REFRESHED")
                                .build()
                );
    }

    /**
     * Logs out the user by invalidating theirs token.
     * This endpoint clears the "refresh_token" and "access_token" cookies,
     * effectively ending the user's session.
     *
     * @param refreshTokenString The refresh token from the "refresh_token" cookie.
     * @return A {@link ResponseEntity} with an {@link ApiResponse} indicating successful logout.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@CookieValue(name = "refresh_token", required = false) String refreshTokenString) {

        logOutService.logOut(refreshTokenString);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)  //in production (with https) set on true
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        ResponseCookie accessCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)  //in production (with https) set on true
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(
                        ApiResponse.<Void>builder()
                                .message("LOGOUT")
                                .build()
                );
    }
}
