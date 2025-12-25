package it.younify.repository;
import it.younify.model.entity.Host;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HostRepository extends JpaRepository<Host, Long> {
    Optional<Host> findByHostName(String name);
    Optional<Host> findByPEC(String PEC);
    Optional<Host> findByProvince(String province);
    Optional<Host> findByCity(String city);
}