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
     * The URL or reference to the user's avatar image.
     */
    private String avatar;

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

    /**
     * The total count of CFU (credits or reputation points) accumulated by the user.
     */
    private int totalCfu;

    /**
     * The total number of matches or connections associated with the user.
     */
    private int totalMatch;
}
