package it.younify.modules.sgm.service;

import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgm.model.entity.Status;
import it.younify.modules.sgm.repository.MatchRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service provides functionality to calculate the total number of accepted matches for a user.
 *
 * @see MatchRepository
 */
@Service
@RequiredArgsConstructor
public class TotalMatchedService {

    private final MatchRepository matchRepository;

    /**
     * Calculates the total number of accepted matches for a given user.
     * This method queries the repository for all matches where the user is either the sender or receiver
     * and the status is {@link Status#ACCEPTED}.
     *
     * @param user The user for whom to count the accepted matches.
     * @return The total number of accepted matches.
     */
    @Transactional(readOnly = true)
    public Integer getTotalMatched(@NonNull User user) {

        return matchRepository.countMatches(user, Status.ACCEPTED);
    }
}
