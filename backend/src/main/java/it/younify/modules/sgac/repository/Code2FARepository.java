package it.younify.modules.sgac.repository;

import it.younify.modules.sgac.model.entity.Code2FA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Code2FARepository extends JpaRepository<Code2FA, String> {

    Optional<Code2FA> findById(String id);
}
