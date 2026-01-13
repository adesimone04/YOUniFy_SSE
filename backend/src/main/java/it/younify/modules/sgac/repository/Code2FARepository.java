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
     * Finds a 2FA code by the unique ID of its owner, who can be either a {@link User} or a {@link Host}.
     * This custom query is necessary because the owner is polymorphic (can be one of two different entity types).
     *
     * @param id The unique identifier of the user or host.
     * @return An {@link Optional} containing the 2FA code if found.
     */
    @Query("SELECT t FROM Code2FA t WHERE t.user.ID = :id OR t.host.ID = :id")
    Optional<Code2FA> findByOwnerId(@Param("id") String id);

    /**
     * Deletes the 2FA code associated with a specific user.
     * This is used to clean up old codes before generating a new one.
     *
     * @param user The user whose 2FA code should be deleted.
     */
    void deleteByUser(User user);


    /**
     * Deletes the 2FA code associated with a specific host.
     * This is used to clean up old codes before generating a new one.
     *
     * @param host The host whose 2FA code should be deleted.
     */
    void deleteByHost(Host host);
}