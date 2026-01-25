package it.younify.modules.sgu.service;

import it.younify.core.exception.BadRequestException;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.HostRepository;
import it.younify.modules.sgac.repository.UserRepository;
import it.younify.modules.sgu.model.dto.request.ChangePassRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * This service handles the business logic for changing a user's or host's password.
 * It validates the new password and updates the account in the database.
 *
 * @see ChangePassRequest
 * @see UserRepository
 * @see HostRepository
 * @see PasswordEncoder
 */
@Service
@RequiredArgsConstructor
public class ChangePasswordService {

    private final UserRepository userRepository;
    private final HostRepository hostRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Changes the password for the currently authenticated user or host.
     * This method validates that the new password and its confirmation match, and that the new password meets the required complexity.
     * It also prevents changing the password to the same one.
     *
     * @param changePassRequest The DTO containing the new password and its confirmation.
     * @throws BadRequestException if the passwords do not match, do not meet complexity requirements, or are the same as the current password.
     */
    public void changePassword(@NonNull ChangePassRequest changePassRequest) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String password = changePassRequest.getPassword();
        String confirmPassword = changePassRequest.getConfirmPassword();

        if (validatePassword(password, confirmPassword)){

            if (principal instanceof User &&
                    !passwordEncoder.matches(password, ((User) principal).getPassword())) {

                User user = (User) principal;

                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
            }
            else {

                if (principal instanceof Host &&
                        !passwordEncoder.matches(password, ((Host) principal).getPassword())){

                    Host host = (Host) principal;

                    host.setPassword(passwordEncoder.encode(password));
                    hostRepository.save(host);
                }
                else throw new BadRequestException("The password is equal to current", "PASS_NOT_CHANGED");
            }
        }
        else throw new BadRequestException("Some data are not valid", "DATA_NOT_VALID");
    }


    private boolean validatePassword(String password, String confirmPassword) {

        return password.equals(confirmPassword) &&
                password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!.]).{8,}$");

    }
}
