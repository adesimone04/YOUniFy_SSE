package it.younify.modules.sgu.service;

import it.younify.core.exception.BadRequestException;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.repository.HostRepository;
import it.younify.modules.sgac.service.validator.GeoValidationService;
import it.younify.modules.sgac.service.validator.UniversityValidationService;
import it.younify.modules.sgu.model.dto.request.EditHostInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * This service handles the business logic for editing a host's profile information.
 * It validates the provided data and updates the host's record in the database.
 *
 * @see EditHostInfoRequest
 * @see HostRepository
 * @see GeoValidationService
 * @see UniversityValidationService
 */
@Service
@RequiredArgsConstructor
public class EditHostInfoService {

    private final HostRepository hostRepository;

    private final GeoValidationService geoValidationService;
    private final UniversityValidationService universityValidationService;

    /**
     * Edits the profile of a given host with the information from the request.
     * This method validates the geographical and university data before saving the changes.
     * The operation is transactional and will roll back in case of any error.
     *
     * @param editHostInfoRequest The DTO containing the new profile information.
     * @param host The host entity to be updated.
     * @throws BadRequestException if the provided geographical or university data is not valid.
     */
    @Transactional(rollbackFor = Exception.class)
    public void editHostProfile(EditHostInfoRequest editHostInfoRequest, Host host){

        String hostName = editHostInfoRequest.getHostName();
        String province =  editHostInfoRequest.getProvince();
        String city =  editHostInfoRequest.getCity();
        String uniList = editHostInfoRequest.getUniList();

        host.setHostName(hostName);

        List<String> universities = Arrays.asList(uniList.split(","));
        if(validateData(province, city, universities)){

            host.setProvince(province);
            host.setCity(city);
            host.setUniList(uniList);

            hostRepository.save(host);
        }
        else throw new BadRequestException("Some data are not valid", "DATA_NOT_VALID");
    }


    private boolean validateData(String province,String city, List<String> universities){

        return geoValidationService.validateGeo(province, city) &&
                universityValidationService.isValidUniversity(universities);
    }
}
