package it.younify.modules.sgn.model.dto.response;

import it.younify.modules.sgn.model.entity.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private Long ID;

    private String senderName;

    private String receiverName;

    private Type type;

    private String content;

    private Instant date;
}
