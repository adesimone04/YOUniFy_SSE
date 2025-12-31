package it.younify.modules.sgac.repository;

import it.younify.modules.sgac.model.entity.Code2FA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * The {@code Code2FARepository} interface is a Spring Data JPA repository for {@link Code2FA} entities.
 * <p>
 * This repository provides the standard CRUD operations for {@code Code2FA} entities,
 * which are used to store and manage two-factor authentication codes.
 */
public interface Code2FARepository extends JpaRepository<Code2FA, String> {

    /**
     * Finds a 2FA code by its associated account ID.
     *
     * @param id The account ID.
     * @return An {@link Optional} containing the 2FA code if found, or an empty optional otherwise.
     */
    Optional<Code2FA> findById(String id);
}