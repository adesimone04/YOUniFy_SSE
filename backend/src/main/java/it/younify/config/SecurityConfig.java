package it.younify.config;

import org.springframework.context.annotation.Bean;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * The {@code SecurityConfig} class provides the main security configuration for the application.
 * It enables web security and defines the security filter chain.
 *
 * @see org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    /**
     * Defines the security filter chain.
     * This method configures the application's security policies, including:
     * <ul>
     *     <li>Disabling CSRF protection (common for stateless APIs).</li>
     *     <li>Defining authorization rules for different API endpoints.</li>
     *     <li>Setting the session management policy to stateless.</li>
     *     <li>Registering the custom {@link JwtAuthenticationFilter} and the {@link AuthenticationProvider}.</li>
     * </ul>
     *
     * @param http The {@link HttpSecurity} to configure.
     * @return A {@link SecurityFilterChain} instance.
     * @throws Exception if an error occurs during the configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/auth/logout").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/host/**").hasRole("HOST")
                        .requestMatchers("/api/user/**").hasRole("USER")
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.
                        sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}