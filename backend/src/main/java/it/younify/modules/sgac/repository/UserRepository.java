package it.younify.modules.sgac.repository;

import it.younify.modules.sgac.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * The {@code UserRepository} interface is a Spring Data JPA repository for {@link User} entities.
 * <p>
 * This repository provides the standard CRUD operations for {@code User} entities,
 * as well as custom query methods for finding users by various attributes.
 */
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Finds a user by their unique ID.
     *
     * @param id The ID of the user.
     * @return An {@link Optional} containing the user if found, or an empty optional otherwise.
     */
    Optional<User> findById(String id);

    /**
     * Finds a user by their email address.
     *
     * @param email The email address of the user.
     * @return An {@link Optional} containing the user if found, or an empty optional otherwise.
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their university.
     *
     * @param university The university of the user.
     * @return An {@link Optional} containing the user if found, or an empty optional otherwise.
     */
    Optional<User> findByUniversity(String university);

    /**
     * Finds a user by their course of study (Corso di Laurea).
     *
     * @param cdl The course of study of the user.
     * @return An {@link Optional} containing the user if found, or an empty optional otherwise.
     */
    Optional<User> findByCdl(String cdl);
}
