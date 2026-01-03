package it.younify.modules.sgac.repository;

import it.younify.modules.sgac.model.entity.Code2FA;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * The {@code Code2FARepository} interface is a Spring Data JPA repository for {@link Code2FA} entities.
 * <p>
 * This repository provides the standard CRUD operations for {@code Code2FA} entities,
 * which are used to store and manage two-factor authentication codes.
 */
public interface Code2FARepository extends JpaRepository<Code2FA, Long> {

    /**
     * Finds a 2FA code by its associated account ID.
     *
     * @param id The account ID.
     * @return An {@link Optional} containing the 2FA code if found, or an empty optional otherwise.
     */
    Optional<Code2FA> findById(Long id);
    Optional<Code2FA> findByHost(Host host);
    Optional<Code2FA> findByUser(User user);

    @Query("SELECT t FROM Code2FA t WHERE t.user.id = :id OR t.host.id = :id")
    Optional<Code2FA> findByOwnerId(@Param("id") String id);

    void deleteByUser(User user);
    void deleteByHost(Host host);
}