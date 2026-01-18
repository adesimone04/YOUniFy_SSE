package it.younify.modules.sgm.service;

import it.younify.core.service.ImageStorageService;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.UserRepository;
import it.younify.modules.sgm.model.dto.Response.MatchcardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class MatchcardViewService {
    private final ImageStorageService imageStorageService;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<MatchcardResponse> getMatchcards(String university, Pageable pageable) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;
        return userRepository.findAllByUniversity(university, user.getID() ,pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<MatchcardResponse> getMatchcardsByCdl(String university, String cdl, Pageable pageable) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;
        return userRepository.findAllByUniversityAndCdl(university, cdl, user.getID(), pageable).map(this::toResponse);
    }

    private MatchcardResponse toResponse(User user) {

        return new MatchcardResponse(
                user.getID(),
                imageStorageService.getPresignedUrl(user.getProfileImage()),
                user.getFirstName() + " " + user.getLastName(),
                user.getUniversity(),
                user.getCdl(),
                Period.between(user.getDateOfBirth(), LocalDate.now()).getYears()
        );
    }
}
