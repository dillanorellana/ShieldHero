package com.shieldhero.game;

public class Castillo {

    private int vidas;
    private final int vidasMaximas;

    public Castillo() {
        this.vidasMaximas = 5;
        this.vidas = vidasMaximas;
    }

    public void recibirDaño(int daño) {
        if (daño > 0) {
            vidas = Math.max(0, vidas - daño);
        }
    }

    public boolean estaDestruido() {
        return vidas <= 0;
    }

    public int getVidas() {
        return vidas;
    }

    public int getVidasMaximas() {
        return vidasMaximas;
    }
}