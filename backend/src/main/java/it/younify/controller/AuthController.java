package it.younify.controller;

import it.younify.model.dto.AuthenticationResponse;
import it.younify.model.dto.RefreshTokenRequest;
import it.younify.model.dto.UserLogInRequest;
import it.younify.model.dto.UserSignUpRequest;
import it.younify.service.RefreshTokenService;
import it.younify.service.UserLogInService;
import it.younify.service.UserSignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserSignUpService userSignUpService;
    private final UserLogInService userLogInService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signIn(@Valid @RequestBody UserSignUpRequest userSignUpRequest) {

        return ResponseEntity.ok(Collections.singletonMap("message", userSignUpService.signUp(userSignUpRequest)));
    }


    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> logIn(@Valid @RequestBody UserLogInRequest userLogInRequest) {

        AuthenticationResponse response = userLogInService.logIn(userLogInRequest);

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
