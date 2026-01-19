package it.younify.modules.sgu.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditUniversityRequest {

    /**
     * The user's email address.
     * This field is mandatory.
     */
    @NotBlank
    @Size(max = 40)
    private String email;


    /**
     * The user's course of study (Corso di Laurea).
     * This field is mandatory.
     */
    @NotBlank
    @Size(max = 100)
    private String cdl;

    /**
     * The user's university.
     * This field is mandatory.
     */
    @NotBlank
    @Size(max = 50)
    private String university;


}
