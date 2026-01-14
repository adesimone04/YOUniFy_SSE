package it.younify.modules.sge.repository;

import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sge.model.entity.Event;
import it.younify.modules.sge.model.entity.Participation;
import it.younify.modules.sge.model.entity.ParticipationId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipationRepository extends JpaRepository<Participation, ParticipationId> {

    Optional<Participation> findByUserAndEvent(User user, Event event);
}
