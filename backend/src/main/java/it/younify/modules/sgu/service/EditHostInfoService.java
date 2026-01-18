package it.younify.modules.sgu.service;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.repository.HostRepository;
import it.younify.modules.sgu.model.dto.request.EditHostInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EditHostInfoService {

    private final HostRepository hostRepository;

    @Transactional(rollbackFor = Exception.class)
    public void editHostProfile(EditHostInfoRequest editHostInfoRequest, Host host){

        String hostName = editHostInfoRequest.getHostName();
        String province =  editHostInfoRequest.getProvince();
        String city =  editHostInfoRequest.getCity();
        String uniList = editHostInfoRequest.getUniList();


        host.setHostName(hostName);
        host.setProvince(province);
        host.setCity(city);
        host.setUniList(uniList);

        hostRepository.save(host);
    }
}
