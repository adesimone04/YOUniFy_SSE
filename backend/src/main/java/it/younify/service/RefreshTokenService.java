package it.younify.service;

import it.younify.model.dto.AuthenticationResponse;
import it.younify.model.dto.RefreshTokenRequest;
import it.younify.model.entity.RefreshToken;
import it.younify.model.entity.User;
import it.younify.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;


    public AuthenticationResponse refresh(RefreshTokenRequest refreshTokenRequest){

        RefreshToken existingToken = refreshTokenRepository.findByToken(refreshTokenRequest.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh Token not found"));

        if(existingToken.isRevoked()){ throw new RuntimeException("Refresh token revoked or not valid"); }

        if (existingToken.getExpiryDate().isBefore(Instant.now())) {

            existingToken.setRevoked(true);
            refreshTokenRepository.save(existingToken);
            throw new RuntimeException("Refresh token expired. Login requested");
        }

        User user = existingToken.getUser();

        String jwtToken = jwtService.generateToken(user);
        String refreshToken = UUID.randomUUID().toString();
        existingToken.setRevoked(true);

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
