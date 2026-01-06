package it.younify.modules.sgtr.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentPostRequest {

    @NotBlank
    private Long threadId;

    @NotBlank
    @Size(min = 1, max = 500)
    private String content;
}
