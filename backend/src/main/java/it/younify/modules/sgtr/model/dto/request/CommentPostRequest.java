package it.younify.modules.sgtr.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentPostRequest {

    @NotBlank
    private Long threadId;
    @NotBlank
    private String content;
}
