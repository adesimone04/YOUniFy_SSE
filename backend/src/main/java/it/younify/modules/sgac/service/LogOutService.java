package it.younify.modules.sgac.service;

import it.younify.core.utils.TokenHashUtility;
import it.younify.modules.sgac.model.entity.RefreshToken;
import it.younify.modules.sgac.repository.RefreshTokenRepository;
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
