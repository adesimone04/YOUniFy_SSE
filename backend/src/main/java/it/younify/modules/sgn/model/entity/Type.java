package it.younify.modules.sgn.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the type of notification, which categorizes its purpose.
 * This enum is used to distinguish between different kinds of notifications within the system.
 */
@Getter
@AllArgsConstructor
public enum Type {

    /**
     * A notification related to a user match.
     */
    MATCH("Match"),

    /**
     * A notification related to an event.
     */
    EVENT("Event"),

    /**
     * A system-generated notification.
     */
    SYSTEM("Notifica di sistema");

    /**
     * The human-readable label for the notification type.
     */
    private final String label;
}
