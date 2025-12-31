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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class SessionController {

    private final Authentication2FAService authentication2FAService;
    private final RefreshTokenService refreshTokenService;
    private final LogOutService logOutService;


    @PostMapping("/2FA")
    public ResponseEntity<ApiResponse<AccountSummary>> confirm2FA(@Valid @RequestBody Authentication2FARequest authentication2FARequest) {

        AccountSummary summary= authentication2FAService.confirm2FA(authentication2FARequest);

        return ResponseEntity.ok(
                ApiResponse.<AccountSummary>builder()
                        .message("2FA_CONFIRMED")
                        .data(summary)
                        .build());

    }


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
