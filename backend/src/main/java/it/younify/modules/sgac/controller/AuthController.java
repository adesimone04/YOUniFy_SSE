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


/**
 * The {@code AuthController} class handles primary authentication endpoints,
 * including user and host sign-up, and login.
 *
 * @see it.younify.modules.sgac.service.UserSignUpService
 * @see it.younify.modules.sgac.service.HostSignUpService
 * @see it.younify.modules.sgac.service.LogInService
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserSignUpService userSignUpService;
    private final HostSignUpService hostSignUpService;
    private final LogInService logInService;


    /**
     * Handles the sign-up process for a new user.
     *
     * @param userSignUpRequest The request body containing the user's sign-up information.
     * @return A {@link ResponseEntity} containing an {@link ApiResponse} with the new user's ID.
     * @throws it.younify.core.exception.ResourceAlreadyExistsException if a user with the given email already exists.
     * @throws it.younify.core.exception.BadRequestException if the password or email is invalid.
     */
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


    /**
     * Handles the sign-up process for a new host.
     *
     * @param hostSignUpRequest The request body containing the host's sign-up information.
     * @return A {@link ResponseEntity} containing an {@link ApiResponse} with the new host's ID.
     * @throws it.younify.core.exception.ResourceAlreadyExistsException if a host with the given PEC already exists.
     * @throws it.younify.core.exception.BadRequestException if the password is invalid or the PEC does not match the "Partita IVA".
     */
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


    /**
     * Handles the login process for both users and hosts.
     * Upon successful authentication, it sets "refresh_token" and "access_token" cookies.
     * If 2FA is required, it returns a response with the status "2FA_SENT".
     *
     * @param logInRequest The request body containing the login credentials.
     * @return A {@link ResponseEntity} with cookies set and an {@link ApiResponse} containing the login status and account ID.
     * @throws org.springframework.security.authentication.BadCredentialsException if the credentials are invalid.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> logIn(@Valid @RequestBody LogInRequest logInRequest) {

        AuthenticationResponse response = logInService.logIn(logInRequest);

        if(response.getStatus().equals("LOGIN")){

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

        //handling status "2FA_SENT"
        return ResponseEntity.ok()
                .body(
                        ApiResponse.<String>builder()
                                .message(response.getStatus())
                                .data(response.getAccountID())
                                .build()
                );
    }
}
