package com.dam.lic;

public enum Stare {
    CREATA("Creată"),
    IN_ASTEPTARE_ACCEPT("În așteptare accept"),
    IN_ASTEPTARE_CURIER("În așteptare curier"),
    IN_CURS_DE_PRELUARE("În curs de preluare"),
    PRELUATA("Preluată"),
    IN_CURS_DE_LIVRARE("În curs de livrare"),
    LIVRATA("Livrată"),
    INCHEIATA("Încheiată");

    private final String descriere;

    Stare(String descriere) {
        this.descriere = descriere;
    }

    public String getDescriere() {
        return descriere;
    }
}
