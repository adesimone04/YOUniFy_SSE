package it.younify.modules.sgm.service;

import it.younify.core.service.ImageStorageService;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.UserRepository;
import it.younify.modules.sgm.model.dto.response.MatchCardResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

/**
 * This service provides functionality to retrieve user profiles for display as "match cards" in the matching feature.
 * It allows filtering by university and course of study.
 *
 * @see UserRepository
 * @see ImageStorageService
 */
@Service
@RequiredArgsConstructor
public class MatchCardViewService {

    private final ImageStorageService imageStorageService;
    private final UserRepository userRepository;

    /**
     * Retrieves a paginated list of match cards for users from a specific university, excluding the currently authenticated user.
     *
     * @param university The university to filter by.
     * @param pageable The pagination information.
     * @return A paginated list of {@link MatchCardResponse} objects.
     */
    @Transactional(readOnly = true)
    public Page<MatchCardResponse> getMatchCards(@NonNull String university, @NonNull Pageable pageable) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;

        return userRepository.findAllByUniversity(university, user.getID() ,pageable).map(this::toResponse);
    }

    /**
     * Retrieves a paginated list of match cards for users from a specific university and course of study, excluding the currently authenticated user.
     *
     * @param university The university to filter by.
     * @param cdl The course of study to filter by.
     * @param pageable The pagination information.
     * @return A paginated list of {@link MatchCardResponse} objects.
     */
    @Transactional(readOnly = true)
    public Page<MatchCardResponse> getMatchCardsByUniAndCdl(@NonNull String university, @NonNull String cdl, @NonNull Pageable pageable) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;

        return userRepository.findAllByUniversityAndCdl(university, cdl, user.getID(), pageable).map(this::toResponse);
    }


    private MatchCardResponse toResponse(User user) {

        return new MatchCardResponse(
                user.getID(),
                imageStorageService.getPresignedUrl(user.getProfileImage()),
                user.getFirstName() + " " + user.getLastName(),
                user.getUniversity(),
                user.getCdl(),
                Period.between(user.getDateOfBirth(), LocalDate.now()).getYears()
        );
    }
}
