package it.younify.modules.sgtr.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentPostRequest {

    private Long threadId;
    @NotBlank
    private String content;
}
