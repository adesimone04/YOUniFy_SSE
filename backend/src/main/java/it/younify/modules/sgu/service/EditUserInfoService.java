package it.younify.modules.sgu.service;

import it.younify.core.exception.BadRequestException;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.UserRepository;
import it.younify.modules.sgu.model.dto.request.EditUserInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EditUserInfoService {

    private final UserRepository userRepository;

    @Transactional(rollbackFor = Exception.class)
    public void editUserProfile(EditUserInfoRequest editUserInfoRequest, User user) {

        String firstName = editUserInfoRequest.getFirstName();
        String lastName = editUserInfoRequest.getLastName();
        LocalDate dateOfBirth = editUserInfoRequest.getDateOfBirth();
        String contactsInfo = editUserInfoRequest.getContactsInfo();

        if(validateData(firstName, lastName, dateOfBirth)){

            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setDateOfBirth(dateOfBirth);
            user.setContactsInfo(contactsInfo);

            userRepository.save(user);
        }
        else throw new BadRequestException("Some data are not valid", "DATA_NOT_VALID");
    }


    private boolean validateData(String firstName, String lastName, LocalDate dateOfBirth) {

        LocalDate today = LocalDate.now();

        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();
        int academicStartYear = (currentMonth < 9) ? currentYear - 1 : currentYear;
        int maxBirthYear = academicStartYear - 18;

        LocalDate cutoffDate = LocalDate.of(maxBirthYear, 4, 30);

        return firstName.trim().length() >= 3 &&
                lastName.trim().length() >= 3 &&
                dateOfBirth.isBefore(cutoffDate);
    }
}
