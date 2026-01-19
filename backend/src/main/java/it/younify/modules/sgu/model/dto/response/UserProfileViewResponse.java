package it.younify.modules.sgu.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Represents the response body for viewing a user's profile.
 * This DTO is used to transfer a subset of a user's details from the server to the client for display.
 */
@Data
@Builder
public class UserProfileViewResponse {

    /**
     * The user's first name.
     */
    private String firstName;

    /**
     * The user's last name.
     */
    private String lastName;

    /**
     * The user's email address.
     */
    private String email;

    /**
     * The user's course of study (Corso di Laurea).
     */
    private String cdl;

    /**
     * The user's university.
     */
    private String university;

    /**
     * The user's date of birth.
     */

    private LocalDate dateOfBirth;

    /**
     * The user's contact information.
     */
    private String contactsInfo;
}
