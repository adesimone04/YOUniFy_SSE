package it.younify.modules.sgu.service;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgu.model.dto.response.HostProfileViewResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class HostProfileViewService {

    public HostProfileViewResponse hostProfileView() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Host) {

            Host host = (Host) principal;

            return HostProfileViewResponse.builder()
                    .hostName(host.getHostName())
                    .PIVA(host.getPIVA())
                    .PEC(host.getPEC())
                    .province(host.getProvince())
                    .city(host.getCity())
                    .uniList(host.getUniList())
                    .build();
        }
        else throw new AccessDeniedException("Access is denied for this operation");
    }
}
