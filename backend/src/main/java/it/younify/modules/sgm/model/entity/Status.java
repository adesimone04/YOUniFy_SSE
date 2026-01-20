package it.younify.modules.sgm.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the status of a match request.
 * This enum is used to track the state of a match from initiation to resolution.
 */
@Getter
@AllArgsConstructor
public enum Status {

    /**
     * The match request has been sent and is awaiting a response from the receiver.
     */
    PENDING("Pending"),

    /**
     * The match request has been received, and the sender is waiting for the receiver's decision.
     */
    WAITING("Waiting"),

    /**
     * The match request has been accepted by the receiver.
     */
    ACCEPTED("Accepted"),

    /**
     * The match request has been rejected by the receiver.
     */
    REJECTED("Rejected");

    /**
     * The human-readable label for the status.
     */
    private String label;
}
