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

    // Recuperar vida sin superar el maximo del castillo
    public void recuperarVida(int cantidad) {
        if (cantidad > 0 && !estaDestruido()) {
            vidas = Math.min(vidasMaximas, vidas + cantidad);
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