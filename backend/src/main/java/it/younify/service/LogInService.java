package it.younify.service;

import it.younify.config.TokenHashUtility;
import it.younify.model.dto.request.LogInRequest;
import it.younify.model.dto.response.AuthenticationResponse;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final Generate2FAService generate2FAService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private int refreshExpiration;

    public AuthenticationResponse logIn(@NotNull LogInRequest logInRequest){

        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(logInRequest.getEmail(), logInRequest.getPassword()));
        }
        catch(DisabledException e){

            String ID = null;
            String email =  logInRequest.getEmail();

            Optional<User> optU = userRepository.findByEmail(email);
            if(optU.isPresent()){

                User user = optU.get();
                if (!passwordEncoder.matches(logInRequest.getPassword(), user.getPassword())) {
                    throw new BadCredentialsException("Email or password incorrect");
                }

                ID = user.getID();
            }
            else{

                Optional<Host> optH = hostRepository.findByPEC(email);
                if(optH.isPresent()){

                    Host host = optH.get();
                    if (!passwordEncoder.matches(logInRequest.getPassword(), host.getPassword())) {
                        throw new BadCredentialsException("Email or password incorrect");
                    }

                    ID = host.getID();
                }
            }

            generate2FAService.send2FARequest(ID);
            return AuthenticationResponse.builder()
                    .status("2FA_REQUIRED")
                    .accountID(ID)
                    .build();
        }


        Optional<User> userOpt = userRepository.findByEmail(logInRequest.getEmail());

        String jwtToken = null;
        String refreshToken = null;

        if(userOpt.isPresent()){

            User user =  userOpt.get();
            jwtToken = jwtService.generateToken(user);
            refreshToken = UUID.randomUUID().toString();

            String tokenHashed = TokenHashUtility.hashToken(refreshToken);

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
                    .orElseThrow(() -> new UsernameNotFoundException("Email or password incorrect"));

            jwtToken = jwtService.generateToken(host);
            refreshToken = UUID.randomUUID().toString();

            String tokenHashed = TokenHashUtility.hashToken(refreshToken);

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
                .status("LOGIN")
                .build();
    }
}
