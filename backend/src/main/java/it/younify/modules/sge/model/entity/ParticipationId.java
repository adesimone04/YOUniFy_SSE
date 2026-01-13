package it.younify.modules.sge.model.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ParticipationId implements Serializable {

    private Long eventId;

    private String userId;

}