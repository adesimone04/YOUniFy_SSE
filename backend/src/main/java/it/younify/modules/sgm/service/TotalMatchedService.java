package it.younify.modules.sgm.service;

import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgm.model.entity.Status;
import it.younify.modules.sgm.repository.MatchRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TotalMatchedService {

    private final MatchRepository matchRepository;

    @Transactional(readOnly = true)
    public Integer getTotalMatched(@NonNull User user) {

        return matchRepository.countMatches(user, Status.ACCEPTED);
    }
}
