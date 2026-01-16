package it.younify.modules.sgn.repository;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgn.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByReceiverHost(Host host, Pageable pageable);
    Page<Notification> findAllByReceiverUser(User user, Pageable pageable);
}
