package it.younify.modules.sgtr.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FlagCategory {

    GENERALE("Generale"),
    APPUNTI("Appunti"),
    ESAMI("Esami"),
    CERCO_CASA("Cerco Casa"),
    CERCASI_COINQUILINO("Cercasi Coinquilino"),
    INFO("Informazioni"),
    GRUPPO_STUDIO("Gruppo Studio"),
    SESSIONE("Sessione"),
    VITA_FUORISEDE("Vita da fuorisede"),
    APERITIVO("Aperitivo"),
    NOTTE_ESAME("Notte prima degli esami");

    private String label;
}
