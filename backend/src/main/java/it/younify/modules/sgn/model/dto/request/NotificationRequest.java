package it.younify.modules.sgn.model.dto.request;

import it.younify.modules.sgn.model.entity.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequest {

    @NotBlank
    private String receiverId;

    @NotNull
    private Type type;

    @NotBlank
    private String content;
}
