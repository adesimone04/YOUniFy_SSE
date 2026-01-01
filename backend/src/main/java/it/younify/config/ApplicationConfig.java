package it.younify.config;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.HostRepository;
import it.younify.modules.sgac.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestClient;

import java.util.Optional;

/**
 * The {@code ApplicationConfig} class provides central application-level configuration.
 * This class is responsible for defining beans related to security, authentication,
 * and other core components.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;
    private final HostRepository hostRepository;

    /**
     * Defines the password encoder bean.
     * This application uses {@link BCryptPasswordEncoder} for hashing passwords.
     *
     * @return A {@link PasswordEncoder} instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines the authentication manager bean.
     * This is a core component of Spring Security, responsible for processing authentication requests.
     *
     * @param config The authentication configuration.
     * @return An {@link AuthenticationManager} instance.
     * @throws Exception if an error occurs while retrieving the authentication manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Defines the user details service bean.
     * This service is responsible for loading user-specific data. It attempts to load a {@link User}
     * by email, and if not found, falls back to loading a {@link Host} by PEC.
     *
     * @return A {@link UserDetailsService} instance.
     * @throws UsernameNotFoundException if no user or host is found for the given username (email/PEC).
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {

            Optional<User> user = userRepository.findByEmail(username);
            if (user.isPresent()) {
                return user.get();
            }

            Optional<Host> host = hostRepository.findByPEC(username);
            if (host.isPresent()) {
                return host.get();
            }

            throw new UsernameNotFoundException("No User or Host found with this email/certified email address");
        };
    }

    /**
     * Defines the authentication provider bean.
     * This application uses a {@link DaoAuthenticationProvider}, which retrieves user details
     * from the {@link UserDetailsService} and uses the {@link PasswordEncoder} to verify passwords.
     *
     * @return An {@link AuthenticationProvider} instance.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());

        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }


    /**
     * Defines the RestClient builder bean.
     * This provides a pre-configured builder for creating {@link RestClient} instances,
     * which are used for making HTTP requests to external services.
     *
     * @return A {@link RestClient.Builder} instance.
     */
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
