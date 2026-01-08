package it.younify.modules.sgtr.model.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class ThreadPostRequest {

    @NotBlank
    private String content;

}
