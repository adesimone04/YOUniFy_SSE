package it.younify.modules.sgtr.model.dto.response;


import it.younify.modules.sgtr.model.entity.FlagCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThreadResponse {

    private Long ID;

    private String content;

    private int CFU;

    private FlagCategory flag;

    private String University;

    private Instant publicationDate;

    private String hostUsername;

    private String userUsername;

}
