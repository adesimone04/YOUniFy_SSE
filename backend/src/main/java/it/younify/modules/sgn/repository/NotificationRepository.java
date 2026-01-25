package it.younify.modules.sgn.repository;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgn.model.entity.Notification;
import it.younify.modules.sgn.model.entity.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;


/**
 * The NotificationRepository interface is a Spring Data JPA repository for {@link Notification} entities.
 * It provides the necessary methods to perform CRUD operations on the `notification` table
 * and includes custom query methods for finding notifications based on specific criteria.
 *
 * @see Notification
 * @see JpaRepository
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Finds a paginated list of notifications for a specific {@link Host}.
     * This method is used to retrieve all notifications where the given host is the receiver.
     *
     * @param host The host for whom to retrieve notifications.
     * @param pageable The pagination information.
     * @return A {@link Page} of {@link Notification} objects.
     * @see Host
     * @see Page
     */
    Page<Notification> findAllByReceiverHost(Host host, Pageable pageable);

    /**
     * Finds a paginated list of notifications for a specific {@link User}.
     * This method is used to retrieve all notifications where the given user is the receiver.
     *
     * @param user The user for whom to retrieve notifications.
     * @param pageable The pagination information.
     * @return A {@link Page} of {@link Notification} objects.
     * @see User
     * @see Page
     */
    Page<Notification> findAllByReceiverUser(User user, Pageable pageable);

    /**
     * Deletes all notifications that were created before a specified date.
     * This is a bulk delete operation and should be used with caution as it can lead to performance issues on large datasets.
     *
     * @param date The timestamp before which all notifications will be deleted.
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.date < :date")
    void deleteAllByDateBefore(@Param("date") Instant date);


    /**
     * Deletes all notifications of a specific type sent from a user to another user.
     * This method is useful for cleaning up specific notifications, such as un-matching.
     *
     * @param senderUser The user who sent the notification.
     * @param receiverUser The user who received the notification.
     * @param type The type of notification to delete.
     */
    void deleteAllBySenderUserAndReceiverUserAndType(User senderUser, User receiverUser, Type type);
}
