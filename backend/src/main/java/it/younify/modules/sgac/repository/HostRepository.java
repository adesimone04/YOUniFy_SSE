package it.younify.modules.sgac.repository;
import it.younify.modules.sgac.model.entity.Host;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HostRepository extends JpaRepository<Host, String> {

    Optional<Host> findById(String id);
    Optional<Host> findByPEC(String PEC);
    Optional<Host> findByHostName(String name);
    Optional<Host> findByProvince(String province);
    Optional<Host> findByCity(String city);
    Optional<Host> findByUniList(String university);
}