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

/**
 * The {@code RefreshTokenService} class is responsible for managing refresh tokens.
 * This service handles the logic for creating, validating, and revoking refresh tokens,
 * allowing users to obtain new access tokens without re-authenticating.
 *
 * @see it.younify.modules.sgac.repository.RefreshTokenRepository
 * @see it.younify.modules.sgac.service.JwtService
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private int refreshExpiration;

    /**
     * Refreshes an access token using a provided refresh token.
     * This method validates the refresh token, and if it is valid,
     * revokes it and issues a new pair of access and refresh tokens.
     *
     * @param refreshTokenRequest The request containing the refresh token.
     * @return An {@link AuthenticationResponse} containing the new access and refresh tokens.
     * @throws TokenRefreshException If the refresh token is not found, has been revoked, or is expired.
     * @see it.younify.modules.sgac.model.dto.request.RefreshTokenRequest
     * @see it.younify.modules.sgac.model.dto.response.AuthenticationResponse
     * @see it.younify.modules.sgac.model.entity.User
     * @see it.younify.modules.sgac.model.entity.Host
     */
    public AuthenticationResponse refresh(RefreshTokenRequest refreshTokenRequest){

        if(refreshTokenRequest.getRefreshToken() == null){ throw new TokenRefreshException("Refresh token not valid", "WRONG_REFRESH_TOKEN"); }

        RefreshToken existingToken = refreshTokenRepository.findByTokenHashed(TokenHashUtility.hashToken(refreshTokenRequest.getRefreshToken()))
                .orElseThrow(() -> new TokenRefreshException("Refresh Token not found", "TOKEN_NOT_FOUND"));

        if(existingToken.isRevoked()){ throw new TokenRefreshException("Refresh token revoked or not valid", "TOKEN_REVOKED"); }

        if (existingToken.getExpiryDate().isBefore(Instant.now())) {

            existingToken.setRevoked(true);
            refreshTokenRepository.save(existingToken);

            throw new TokenRefreshException("Refresh token expired. Login requested", "TOKEN_EXPIRED");
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
