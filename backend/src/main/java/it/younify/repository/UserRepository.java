package it.younify.repository;

import it.younify.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUniversity(String university);
    Optional<User> findByCdl(String cdl);
}
