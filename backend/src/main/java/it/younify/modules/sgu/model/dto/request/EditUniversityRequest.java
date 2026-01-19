package it.younify.modules.sgu.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Represents the request body for editing a user's university and course of study.
 * This DTO is used to transfer the updated educational details from the client to the server.
 */
@Data
public class EditUniversityRequest {

    /**
     * The user's email address, used to identify the account to be updated.
     */
    @NotBlank
    @Size(max = 40)
    private String email;


    /**
     * The new course of study (Corso di Laurea) for the user.
     * This must be a valid course name.
     */
    @NotBlank
    @Size(max = 100)
    private String cdl;

    /**
     * The new university for the user.
     * This must be a valid Italian university.
     */
    @NotBlank
    @Size(max = 50)
    private String university;
}
