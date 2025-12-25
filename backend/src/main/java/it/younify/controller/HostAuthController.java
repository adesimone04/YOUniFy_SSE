package it.younify.controller;

import it.younify.model.dto.HostSignUpRequest;
import it.younify.model.dto.UserSignUpRequest;
import it.younify.service.HostSignUpService;
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
@RequestMapping("/api/auth/host")
@RequiredArgsConstructor
public class HostAuthController {

    private final HostSignUpService hostSignUpService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signIn(@Valid @RequestBody HostSignUpRequest hostSignUpRequest) {

        return ResponseEntity.ok(Collections.singletonMap("message", hostSignUpService.signUp(hostSignUpRequest)));
    }
}
