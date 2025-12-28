package it.younify.controller;

import it.younify.model.dto.*;
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
    private final LogInService logInService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/user/signup")
    public String signUp(@Valid @RequestBody UserSignUpRequest userSignUpRequest) {

        return userSignUpService.signUp(userSignUpRequest);
    }


    @PostMapping("/host/signup")
    public String signUp(@Valid @RequestBody HostSignUpRequest hostSignUpRequest) {

        return hostSignUpService.signUp(hostSignUpRequest);
    }


    @PostMapping("/2FA")
    public String confirm2FA(@Valid @RequestBody Authentication2FARequest authentication2FARequest) {

        return authentication2FAService.confirm2FA(authentication2FARequest);
    }


    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> logIn(@Valid @RequestBody LogInRequest logInRequest) {

        AuthenticationResponse response = logInService.logIn(logInRequest);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", response.getRefreshToken())
                .httpOnly(true)
                .secure(false)  //in production (with https) set on true
                .path("/")
                .maxAge(180 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        response.setRefreshToken(null);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@CookieValue(name = "refresh_token", required = false) String refreshTokenString) {

        if (refreshTokenString == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshTokenString);

        AuthenticationResponse response = refreshTokenService.refresh(request);

        ResponseCookie newCookie = ResponseCookie.from("refresh_token", response.getRefreshToken())
                .httpOnly(true)
                .secure(false) //in production (with https) set on true
                .path("/")
                .maxAge(180 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        response.setRefreshToken(null);


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newCookie.toString())
                .body(response);
    }
}
