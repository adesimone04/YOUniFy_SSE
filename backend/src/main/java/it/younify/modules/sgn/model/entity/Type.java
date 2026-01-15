package it.younify.modules.sgn.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Type {

    MATCH("Match"),
    DELETED_EVENT("Evento cacellato"),
    SYSTEM("Notifica di sistema");

    private final String label;
}