package it.younify.modules.sgu.service;

import it.younify.core.service.ImageStorageService;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgu.model.dto.response.HostProfileViewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * This service handles the business logic for viewing a host's profile information.
 * It retrieves the data for the currently authenticated host and maps it to a response DTO.
 *
 * @see HostProfileViewResponse
 * @see Host
 */
@Service
@RequiredArgsConstructor
public class HostProfileViewService {

    private final ImageStorageService imageStorageService;

    /**
     * Retrieves the profile information for the currently authenticated host.
     * This method ensures that the principal is a {@link Host} before proceeding.
     *
     * @return A {@link HostProfileViewResponse} containing the host's profile information.
     * @throws AccessDeniedException if the authenticated principal is not a {@link Host}.
     */
    public HostProfileViewResponse hostProfileView() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Host) {

            Host host = (Host) principal;

            return HostProfileViewResponse.builder()
                    .hostName(host.getHostName())
                    .PIVA(host.getPIVA())
                    .PEC(host.getPEC())
                    .profileImage(imageStorageService.getPresignedUrl(host.getProfileImage()))
                    .province(host.getProvince())
                    .city(host.getCity())
                    .uniList(host.getUniList())
                    .build();
        }
        else throw new AccessDeniedException("Access is denied for this operation");
    }
}
