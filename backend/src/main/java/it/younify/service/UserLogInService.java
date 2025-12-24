package it.younify.service;

import it.younify.model.dto.UserLogInRequest;
import it.younify.model.dto.AuthenticationResponse;
import it.younify.model.entity.RefreshToken;
import it.younify.model.entity.User;
import it.younify.repository.RefreshTokenRepository;
import it.younify.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserLogInService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationResponse logIn(@NotNull UserLogInRequest userLogInRequest){

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLogInRequest.getEmail(), userLogInRequest.getPassword()));

        User user = userRepository.findByEmail(userLogInRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));

        String jwtToken = jwtService.generateToken(user);
        String refreshToken = UUID.randomUUID().toString();

        RefreshToken token = RefreshToken.builder()
            .token(refreshToken)
                .revoked(false)
                .expiryDate(Instant.now().plus(180, ChronoUnit.DAYS))
                .user(user)
                .build();

        refreshTokenRepository.save(token);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }
}
