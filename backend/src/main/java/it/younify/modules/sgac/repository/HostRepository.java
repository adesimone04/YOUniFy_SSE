package it.younify.modules.sgac.repository;
import it.younify.modules.sgac.model.entity.Host;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * The {@code HostRepository} interface is a Spring Data JPA repository for {@link Host} entities.
 * <p>
 * This repository provides the standard CRUD operations for {@code Host} entities,
 * as well as custom query methods for finding hosts by various attributes.
 */
public interface HostRepository extends JpaRepository<Host, String> {

    /**
     * Finds a host by its unique ID.
     *
     * @param id The ID of the host.
     * @return An {@link Optional} containing the host if found, or an empty optional otherwise.
     */
    Optional<Host> findByID(String id);

    /**
     * Finds a host by its certified email address (PEC).
     *
     * @param PEC The PEC of the host.
     * @return An {@link Optional} containing the host if found, or an empty optional otherwise.
     */
    Optional<Host> findByPEC(String PEC);

    /**
     * Finds a host by its name.
     *
     * @param name The name of the host.
     * @return An {@link Optional} containing the host if found, or an empty optional otherwise.
     */
    Optional<Host> findByHostName(String name);

    /**
     * Finds a host by its province.
     *
     * @param province The province of the host.
     * @return An {@link Optional} containing the host if found, or an empty optional otherwise.
     */
    Optional<Host> findByProvince(String province);

    /**
     * Finds a host by its city.
     *
     * @param city The city of the host.
     * @return An {@link Optional} containing the host if found, or an empty optional otherwise.
     */
    Optional<Host> findByCity(String city);

    /**
     * Finds a host by its associated university list.
     *
     * @param university The university list nearest the host.
     * @return An {@link Optional} containing the host if found, or an empty optional otherwise.
     */
    Optional<Host> findByUniList(String university);
}
