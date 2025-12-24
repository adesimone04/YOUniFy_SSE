package it.younify.controller;

import it.younify.model.dto.UserSignUpRequest;
import it.younify.service.UserSignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserSignUpService userSignUpService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signIn(@Valid @RequestBody UserSignUpRequest userSignUpRequest) {

        return ResponseEntity.ok(Collections.singletonMap("message", userSignUpService.signIn(userSignUpRequest)));
    }
}
