package it.younify.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


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

    @Value("#{'${app.cors.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;

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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
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

    /**
     * Defines the global Cross-Origin Resource Sharing (CORS) configuration for the application.
     * <p>
     * This bean is essential for allowing the frontend application, which runs on a different origin
     * (domain), to securely communicate with this backend API. It establishes a clear policy
     * on which origins, methods, and headers are permitted.
     * <p>
     * Key configurations include:
     * <ul>
     *     <li><b>Allowed Origins:</b> Specifies the list of frontend URLs that are trusted to make requests.</li>
     *     <li><b>Allowed Methods/Headers:</b> Defines the standard HTTP methods and headers the client is permitted to use.</li>
     *     <li><b>Allow Credentials ({@code true}):</b> This is critical for session management. It permits the browser
     *     to include credentials like cookies in cross-origin requests, which is necessary for authentication.</li>
     *     <li><b>Exposed Headers ({@code "Set-Cookie"}):</b> Explicitly allows the browser's JavaScript to access the
     *     {@code Set-Cookie} header from the server's response. Without this, the frontend cannot read or process
     *     the cookies being set by the backend, even though the browser receives them.</li>
     * </ul>
     * This configuration is applied to all paths ({@code "/**"}) within the application.
     *
     * @return A {@link CorsConfigurationSource} that provides the defined CORS policy to the Spring Security filter chain.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        configuration.setAllowedHeaders(List.of(
                "Content-Type",
                "X-Requested-With",
                "X-XSRF-TOKEN"
        ));

        configuration.setExposedHeaders(List.of("Set-Cookie"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}