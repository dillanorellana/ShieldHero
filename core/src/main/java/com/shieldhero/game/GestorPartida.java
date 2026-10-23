
package com.shieldhero.game;

public class GestorPartida {

    // Patron Singleton
    private static final GestorPartida instancia =
            new GestorPartida();

    private static final int TOTAL_RONDAS = 5;

    // Puntuacion acumulada necesaria para superar cada ronda
    private static final int[] OBJETIVOS = {
        100, 250, 450, 700, 1000
    };

    private int record;
    private int rondaActual;
    private boolean victoria;

    private GestorPartida() {
        record = 0;
        reiniciarRondas();
    }

    public static GestorPartida getInstancia() {
        return instancia;
    }

    public int getRecord() {
        return record;
    }

    public void actualizarRecord(int puntos) {
        if (puntos > record) {
            record = puntos;
        }
    }

    // Actualizar el progreso segun los puntos del jugador
    public void actualizar(int puntos) {

        if (victoria) {
            return;
        }

        // Permite avanzar varias rondas si se supera
        // mas de un objetivo de una sola vez
        while (!victoria &&
               puntos >= OBJETIVOS[rondaActual - 1]) {

            if (rondaActual < TOTAL_RONDAS) {
                rondaActual++;
            } else {
                victoria = true;
            }
        }
    }

    public void reiniciarRondas() {
        rondaActual = 1;
        victoria = false;
    }

    public int getRondaActual() {
        return rondaActual;
    }

    public int getTotalRondas() {
        return TOTAL_RONDAS;
    }

    public int getObjetivoActual() {
        return OBJETIVOS[rondaActual - 1];
    }

    public int getPuntosRestantes(int puntos) {
        return Math.max(0, getObjetivoActual() - puntos);
    }

    public boolean hayVictoria() {
        return victoria;
    }
}
