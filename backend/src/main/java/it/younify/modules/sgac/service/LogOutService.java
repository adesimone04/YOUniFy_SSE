package it.younify.modules.sgac.service;

import it.younify.core.utils.TokenHashUtility;
import it.younify.modules.sgac.model.entity.RefreshToken;
import it.younify.modules.sgac.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The {@code LogOutService} class is responsible for handling user and host logout.
 * It invalidates the refresh token, effectively ending the user's session.
 *
 * @see it.younify.modules.sgac.repository.RefreshTokenRepository
 */
@Service
@RequiredArgsConstructor
public class LogOutService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Logs out a user or host by deleting their refresh token.
     * If the provided refresh token is found in the database, it is deleted.
     *
     * @param refreshToken The refresh token to invalidate.
     */
    public void logOut(String refreshToken) {

        Optional<RefreshToken> opt = refreshTokenRepository.findByTokenHashed(TokenHashUtility.hashToken(refreshToken));
        opt.ifPresent(refreshTokenRepository::delete);
    }
}
