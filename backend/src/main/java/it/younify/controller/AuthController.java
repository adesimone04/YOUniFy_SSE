package it.younify.controller;

import it.younify.model.dto.request.*;
import it.younify.model.dto.response.AccountSummary;
import it.younify.model.dto.response.ApiResponse;
import it.younify.model.dto.response.AuthenticationResponse;
import it.younify.service.*;
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
public class AuthController {

    private final UserSignUpService userSignUpService;
    private final HostSignUpService hostSignUpService;
    private final Authentication2FAService authentication2FAService;
    private final RefreshTokenService refreshTokenService;
    private final LogInService logInService;
    private final LogOutService logOutService;


    @PostMapping("/user/signup")
    public ResponseEntity<ApiResponse<String>> signUp(@Valid @RequestBody UserSignUpRequest userSignUpRequest) {

        String ID = userSignUpService.signUp(userSignUpRequest);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .message("USER_REGISTERED")
                        .data(ID)
                        .build()
        );
    }


    @PostMapping("/host/signup")
    public ResponseEntity<ApiResponse<String>> signUp(@Valid @RequestBody HostSignUpRequest hostSignUpRequest) {

        String ID = hostSignUpService.signUp(hostSignUpRequest);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .message("HOST_REGISTERED")
                        .data(ID)
                        .build()
        );
    }


    @PostMapping("/2FA")
    public ResponseEntity<ApiResponse<AccountSummary>> confirm2FA(@Valid @RequestBody Authentication2FARequest authentication2FARequest) {

        AccountSummary summary= authentication2FAService.confirm2FA(authentication2FARequest);

        return ResponseEntity.ok(
                ApiResponse.<AccountSummary>builder()
                    .message("2FA_CONFIRMED")
                    .data(summary)
                    .build());

    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> logIn(@Valid @RequestBody LogInRequest logInRequest) {

        AuthenticationResponse response = logInService.logIn(logInRequest);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", response.getRefreshToken())
                .httpOnly(true)
                .secure(false)  //in production (with https) set on true
                .path("/")
                .maxAge(180 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();


        ResponseCookie accessCookie = ResponseCookie.from("access_token", response.getAccessToken())
                .httpOnly(true)
                .secure(false)  //in production (with https) set on true
                .path("/")
                .maxAge(15 * 60)
                .sameSite("Strict")
                .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(
                        ApiResponse.<String>builder()
                                .message(response.getStatus())
                                .data(response.getAccountID())
                                .build()
                );
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
