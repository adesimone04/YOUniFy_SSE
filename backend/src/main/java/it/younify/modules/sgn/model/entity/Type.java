package it.younify.modules.sgn.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Type {

    MATCH("Match"),
    EVENT("Event"),
    SYSTEM("Notifica di sistema");

    private final String label;
}