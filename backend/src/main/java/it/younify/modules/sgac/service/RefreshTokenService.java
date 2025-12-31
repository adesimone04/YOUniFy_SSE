package it.younify.modules.sgac.service;

import it.younify.core.utils.TokenHashUtility;
import it.younify.core.exception.TokenRefreshException;
import it.younify.modules.sgac.model.dto.response.AuthenticationResponse;
import it.younify.modules.sgac.model.dto.request.RefreshTokenRequest;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.RefreshToken;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private int refreshExpiration;

    public AuthenticationResponse refresh(RefreshTokenRequest refreshTokenRequest){

        RefreshToken existingToken = refreshTokenRepository.findByTokenHashed(TokenHashUtility.hashToken(refreshTokenRequest.getRefreshToken()))
                .orElseThrow(() -> new TokenRefreshException("Refresh Token not found"));

        if(existingToken.isRevoked()){ throw new TokenRefreshException("Refresh token revoked or not valid"); }

        //SERIO PROBLEMA
        if (existingToken.getExpiryDate().isBefore(Instant.now())) {

            existingToken.setRevoked(true);
            refreshTokenRepository.save(existingToken);

            throw new TokenRefreshException("Refresh token expired. Login requested");
        }

        String jwtToken = null;

        String refreshToken = UUID.randomUUID().toString();
        existingToken.setRevoked(true);
        String tokenHashed = TokenHashUtility.hashToken(refreshToken);

        if(existingToken.getUser() != null) {

            User user = existingToken.getUser();
            jwtToken = jwtService.generateToken(user);

            RefreshToken token = RefreshToken.builder()
                    .tokenHashed(tokenHashed)
                    .revoked(false)
                    .expiryDate(Instant.now().plus(refreshExpiration, ChronoUnit.DAYS))
                    .user(user)
                    .build();

            refreshTokenRepository.save(token);
        }
        else{

            Host host = existingToken.getHost();
            jwtToken = jwtService.generateToken(host);

            RefreshToken token = RefreshToken.builder()
                    .tokenHashed(tokenHashed)
                    .revoked(false)
                    .expiryDate(Instant.now().plus(refreshExpiration, ChronoUnit.DAYS))
                    .host(host)
                    .build();

            refreshTokenRepository.save(token);
        }

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }
}
