package it.younify.modules.sgm.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {

    PENDING("Pending"),
    WAITING("Waiting"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected");

    private String label;
}
