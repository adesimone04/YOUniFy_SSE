package it.younify.service;

import it.younify.config.TokenHashUtil;
import it.younify.model.dto.LogInRequest;
import it.younify.model.dto.AuthenticationResponse;
import it.younify.model.entity.Host;
import it.younify.model.entity.RefreshToken;
import it.younify.model.entity.User;
import it.younify.repository.HostRepository;
import it.younify.repository.RefreshTokenRepository;
import it.younify.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class LogInService {

    private final UserRepository userRepository;
    private final HostRepository hostRepository;

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private int refreshExpiration;

    public AuthenticationResponse logIn(@NotNull LogInRequest logInRequest){

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(logInRequest.getEmail(), logInRequest.getPassword()));

        Optional<User> userOpt = userRepository.findByEmail(logInRequest.getEmail());

        String jwtToken = null;
        String refreshToken = null;

        if(userOpt.isPresent()){

            User user =  userOpt.get();
            jwtToken = jwtService.generateToken(user);
            refreshToken = UUID.randomUUID().toString();

            String tokenHashed = TokenHashUtil.hashToken(refreshToken);

            RefreshToken token = RefreshToken.builder()
                    .tokenHashed(tokenHashed)
                    .revoked(false)
                    .expiryDate(Instant.now().plus(refreshExpiration, ChronoUnit.DAYS))
                    .user(user)
                    .build();

            refreshTokenRepository.save(token);
        }
        else{

            Host host = hostRepository.findByPEC(logInRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Nessun account è registrato con questo indirizzo: " + logInRequest.getEmail()));

            jwtToken = jwtService.generateToken(host);
            refreshToken = UUID.randomUUID().toString();

            String tokenHashed = TokenHashUtil.hashToken(refreshToken);

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
