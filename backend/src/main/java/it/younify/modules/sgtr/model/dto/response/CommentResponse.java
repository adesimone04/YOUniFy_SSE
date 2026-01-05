package it.younify.modules.sgtr.model.dto.response;


import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long ID;

    private String content;

    private int CFU;

    private Instant publicationDate;

    private String hostUsername;

    private String userUsername;

}
