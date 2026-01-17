package it.younify.modules.sgtr.repository;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.model.entity.FlagCategory;
import it.younify.modules.sgtr.model.entity.Threads;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * The {@code ThreadRepository} interface is a Spring Data JPA repository for {@link Threads} entities.
 * It provides standard CRUD operations and custom query methods for finding threads based on various criteria.
 */
public interface ThreadRepository extends JpaRepository<Threads, Long> {

    /**
     * Finds a thread by its unique ID.
     *
     * @param ID The ID of the thread.
     * @return An {@link Optional} containing the thread if found.
     */
    Optional<Threads> findById(Long ID);

    /**
     * Retrieves a paginated list of all threads, sorted by publication date in descending order.
     *
     * @param pageable The pagination information.
     * @return A paginated list of threads.
     */
    Page<Threads> findAllByOrderByPublicationDateDesc(Pageable pageable);

    /**
     * Retrieves a paginated list of threads filtered by a flag category, sorted by publication date in descending order.
     *
     * @param flag The flag category to filter by.
     * @param pageable The pagination information.
     * @return A paginated list of threads.
     */
    Page<Threads> findAllByFlagOrderByPublicationDateDesc(FlagCategory flag, Pageable pageable);

    /**
     * Retrieves a paginated list of threads filtered by university, sorted by publication date in descending order.
     * This method performs a case-insensitive search across both user and host authors.
     *
     * @param universityInHost The university string to search for in hosts' university lists.
     * @param universityInUser The university string to search for in users' universities.
     * @param pageable The pagination information.
     * @return A paginated list of threads.
     */
    Page<Threads> findByHost_UniListContainingIgnoreCaseOrUser_UniversityIgnoreCaseOrderByPublicationDateDesc(

            String universityInHost,
            String universityInUser,
            Pageable pageable
    );

    /**
     * Retrieves a paginated list of threads posted by a specific host, sorted by publication date in descending order.
     *
     * @param host The host who posted the threads.
     * @param pageable The pagination information.
     * @return A paginated list of threads.
     */
    Page<Threads> findByHostOrderByPublicationDateDesc(Host host, Pageable pageable);

    /**
     * Retrieves a paginated list of threads posted by a specific user, sorted by publication date in descending order.
     *
     * @param user The user who posted the threads.
     * @param pageable The pagination information.
     * @return A paginated list of threads.
     */
    Page<Threads> findByUserOrderByPublicationDateDesc(User user, Pageable pageable);



    /**
     * Calculates the total sum of CFU (votes) for all threads posted by a specific user.
     * <p>
     * This query uses {@code COALESCE} to ensure it returns 0 if the user has not created any threads,
     * which is crucial for accurately calculating a user's total reputation score.
     *
     * @param user The user whose thread scores are to be summed.
     * @return The total CFU score from the user's threads.
     */
    @Query("SELECT COALESCE(SUM(t.CFU), 0) FROM Threads t WHERE t.user = :user")
    int sumCFUByUserThread(@Param("user") User user);


    /**
     * Calculates the total sum of CFU (votes) for all threads posted by a specific host.
     * <p>
     * This query uses {@code COALESCE} to ensure it returns 0 if the host has not created any threads,
     * which is crucial for accurately calculating a host's total reputation score.
     *
     * @param host The host whose thread scores are to be summed.
     * @return The total CFU score from the host's threads.
     */
    @Query("SELECT COALESCE(SUM(t.CFU), 0) FROM Threads t WHERE t.host = :host")
    int sumCFUByHostThread(@Param("host") Host host);

}
