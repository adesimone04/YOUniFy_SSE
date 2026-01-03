package it.younify.modules.sgac.repository;

import it.younify.modules.sgac.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * The {@code RefreshTokenRepository} interface is a Spring Data JPA repository for {@link RefreshToken} entities.
 * <p>
 * This repository provides the standard CRUD operations for {@code RefreshToken} entities,
 * which are used to store and manage refresh tokens for user sessions.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Finds a refresh token by its hashed value.
     *
     * @param token The hashed refresh token.
     * @return An {@link Optional} containing the refresh token if found, or an empty optional otherwise.
     */
    Optional<RefreshToken> findByTokenHashed(String token);
}
