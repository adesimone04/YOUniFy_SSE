package it.younify.modules.sgn.model.dto.request;

import it.younify.modules.sgn.model.entity.Type;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

@Data
public class NotificationRequest {
    @NotNull
    private String receiverId;

    @NotNull
    private Type type;

    @NotNull
    private String content;
}
