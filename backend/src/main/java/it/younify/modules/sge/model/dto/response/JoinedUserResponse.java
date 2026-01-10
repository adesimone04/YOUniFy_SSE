package it.younify.modules.sge.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JoinedUserResponse {

    private String fullName;

    private String profileImageUrl;
}
