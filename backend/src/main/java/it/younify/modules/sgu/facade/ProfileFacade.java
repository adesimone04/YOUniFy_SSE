package it.younify.modules.sgu.facade;

import it.younify.modules.sgac.model.dto.response.AccountSummary;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgu.model.dto.request.Confirm2FAUniversity;
import it.younify.modules.sgu.model.dto.request.EditHostInfoRequest;
import it.younify.modules.sgu.model.dto.request.EditUniversityRequest;
import it.younify.modules.sgu.model.dto.request.EditUserInfoRequest;
import it.younify.modules.sgu.service.ChangeUniversityService;
import it.younify.modules.sgu.service.Confirm2FAUniversityService;
import it.younify.modules.sgu.service.EditHostInfoService;
import it.younify.modules.sgu.service.EditUserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileFacade {

    private final EditUserInfoService editUserInfoService;
    private final EditHostInfoService editHostInfoService;

    private final ChangeUniversityService changeUniversityService;
    private final Confirm2FAUniversityService confirm2FAUniversityService;


    public void editUserInfoProfile(EditUserInfoRequest editUserInfoRequest) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User) {

            editUserInfoService.editUserProfile(editUserInfoRequest, (User) principal);
        }
        else throw new AccessDeniedException("Access id denied for this operation");
    }


    public void editHostInfoProfile(EditHostInfoRequest editHostInfoRequest) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal instanceof Host) {

            editHostInfoService.editHostProfile(editHostInfoRequest, (Host) principal);
        }
        else throw new AccessDeniedException("Access id denied for this operation");
    }


    public AccountSummary changeUniversity(EditUniversityRequest editUniversityRequest){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal instanceof User) {

            return changeUniversityService.changeUniversity(editUniversityRequest, (User) principal);
        }
        else throw new AccessDeniedException("Access id denied for this operation");
    }


    public void confirm2FAUniversity(Confirm2FAUniversity confirm2FAUniversity){

        confirm2FAUniversityService.confirm2FA(confirm2FAUniversity);
    }
}
