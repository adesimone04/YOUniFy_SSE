package it.younify.modules.sgac.controller;

import it.younify.core.model.ApiResponse;
import it.younify.modules.sgac.model.dto.request.Authentication2FARequest;
import it.younify.modules.sgac.model.dto.request.RefreshTokenRequest;
import it.younify.modules.sgac.model.dto.response.AccountSummary;
import it.younify.modules.sgac.model.dto.response.AuthenticationResponse;
import it.younify.modules.sgac.service.Authentication2FAService;
import it.younify.modules.sgac.service.LogOutService;
import it.younify.modules.sgac.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private final RefreshTokenService refreshTokenService;
    private final LogOutService logOutService;

    /**
     * Confirms the two-factor authentication for a user.
     * This endpoint is called after a user has successfully logged in
     * and needs to verify their identity using a 2FA code.
     *
     * @param authentication2FARequest The request body containing the 2FA code.
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
     * Refreshes the access token using a refresh token.
     * This endpoint is used to obtain a new access token without requiring the user to log in again.
     * The refresh token is expected to be provided in a cookie.
     *
     * @param refreshTokenString The refresh token from the "refresh_token" cookie.
     * @return A {@link ResponseEntity} with a new access token in a cookie.
     *         Returns a 403 Forbidden status if the refresh token is missing.
     * @throws it.younify.core.exception.TokenRefreshException If the refresh token is not found or is expired in the database.
     */
    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(@CookieValue(name = "refresh_token", required = false) String refreshTokenString) {

        if (refreshTokenString == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

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
                .body(null);
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
