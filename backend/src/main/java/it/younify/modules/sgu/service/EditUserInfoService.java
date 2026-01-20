package it.younify.modules.sgu.service;

import it.younify.core.exception.BadRequestException;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.UserRepository;
import it.younify.modules.sgu.model.dto.request.EditUserInfoRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * This service handles the business logic for editing a user's profile information.
 * It validates the provided data and updates the user's record in the database.
 *
 * @see EditUserInfoRequest
 * @see UserRepository
 */
@Service
@RequiredArgsConstructor
public class EditUserInfoService {

    private final UserRepository userRepository;

    /**
     * Edits the profile of a given user with the information from the request.
     * This method validates the user's date of birth to ensure they are at least 18 years old based on the academic year.
     * The operation is transactional and will roll back in case of any error.
     *
     * @param editUserInfoRequest The DTO containing the new profile information.
     * @param user The user entity to be updated.
     * @throws BadRequestException if the provided date of birth is not valid.
     */
    @Transactional(rollbackFor = Exception.class)
    public void editUserProfile(@NonNull EditUserInfoRequest editUserInfoRequest, User user) {

        String firstName = editUserInfoRequest.getFirstName();
        String lastName = editUserInfoRequest.getLastName();
        LocalDate dateOfBirth = editUserInfoRequest.getDateOfBirth();
        String contactsInfo = editUserInfoRequest.getContactsInfo();

        if(validateData(dateOfBirth)){

            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setDateOfBirth(dateOfBirth);
            user.setContactsInfo(contactsInfo);

            userRepository.save(user);
        }
        else throw new BadRequestException("Some data are not valid", "DATA_NOT_VALID");
    }


    private boolean validateData(LocalDate dateOfBirth) {

        LocalDate today = LocalDate.now();

        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();
        int academicStartYear = (currentMonth < 9) ? currentYear - 1 : currentYear;
        int maxBirthYear = academicStartYear - 18;

        LocalDate cutoffDate = LocalDate.of(maxBirthYear, 4, 30);

        return dateOfBirth.isBefore(cutoffDate);
    }
}
