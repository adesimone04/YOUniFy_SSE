package it.younify.modules.sgtr.model.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class ThreadPostRequest {

    @NotBlank
    @Size(min = 1, max = 500)
    private String content;

    @NotBlank
    private String flag;

}
