package it.younify.modules.sgac.controller;

import it.younify.modules.sgac.model.dto.request.HostSignUpRequest;
import it.younify.modules.sgac.model.dto.request.LogInRequest;
import it.younify.modules.sgac.model.dto.request.UserSignUpRequest;
import it.younify.modules.sgac.service.HostSignUpService;
import it.younify.modules.sgac.service.LogInService;
import it.younify.modules.sgac.service.UserSignUpService;
import it.younify.core.model.ApiResponse;
import it.younify.modules.sgac.model.dto.response.AuthenticationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserSignUpService userSignUpService;
    private final HostSignUpService hostSignUpService;
    private final LogInService logInService;


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
}
