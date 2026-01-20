package it.younify.modules.sgm.service;

import it.younify.modules.sgm.model.entity.Match;
import it.younify.modules.sgm.repository.MatchRepository;
import it.younify.modules.sgn.facade.NotificationFacade;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchDeleteService {

    private final NotificationFacade notificationFacade;
    private final MatchRepository matchRepository;

    @Transactional(rollbackFor = Exception.class)
    public void deleteMatch(@NonNull Match match){

        notificationFacade.deleteMatchNotify(match.getReceiver());
        matchRepository.deleteById(match.getID());
    }
}
