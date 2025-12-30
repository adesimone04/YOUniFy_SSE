package it.younify.service;

import it.younify.config.TokenHashUtility;
import it.younify.model.entity.RefreshToken;
import it.younify.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogOutService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void logOut(String refreshToken) {

        Optional<RefreshToken> opt = refreshTokenRepository.findByTokenHashed(TokenHashUtility.hashToken(refreshToken));
        opt.ifPresent(refreshTokenRepository::delete);
    }
}
