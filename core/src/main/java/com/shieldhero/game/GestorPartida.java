package com.shieldhero.game;

public class GestorPartida {

    // GM-6: Patron Singleton
    private static final GestorPartida instancia =
            new GestorPartida();

    // GM-9: Configuraciones creadas con Builder
    private final ConfiguracionRonda[] rondas;

    private int record;
    private int rondaActual;
    private boolean victoria;

    private GestorPartida() {

        rondas = new ConfiguracionRonda[] {

            new ConfiguracionRonda.Builder()
                .numero(1)
                .puntosObjetivo(100)
                .intervaloAparicion(1.2f)
                .construir(),

            new ConfiguracionRonda.Builder()
                .numero(2)
                .puntosObjetivo(250)
                .intervaloAparicion(1.0f)
                .construir(),

            new ConfiguracionRonda.Builder()
                .numero(3)
                .puntosObjetivo(450)
                .intervaloAparicion(0.85f)
                .construir(),

            new ConfiguracionRonda.Builder()
                .numero(4)
                .puntosObjetivo(700)
                .intervaloAparicion(0.7f)
                .construir(),

            new ConfiguracionRonda.Builder()
                .numero(5)
                .puntosObjetivo(1000)
                .intervaloAparicion(0.6f)
                .construir()
        };

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

    // Avanzar de ronda al alcanzar la puntuacion requerida
    public void actualizar(int puntos) {

        if (victoria) {
            return;
        }

        while (!victoria &&
               puntos >= getConfiguracionActual().getPuntosObjetivo()) {

            if (rondaActual < rondas.length) {
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
        return rondas.length;
    }

    public int getObjetivoActual() {
        return getConfiguracionActual().getPuntosObjetivo();
    }

    public float getIntervaloAparicionActual() {
        return getConfiguracionActual().getIntervaloAparicion();
    }

    public int getPuntosRestantes(int puntos) {
        return Math.max(0, getObjetivoActual() - puntos);
    }

    public ConfiguracionRonda getConfiguracionActual() {
        return rondas[rondaActual - 1];
    }

    public boolean hayVictoria() {
        return victoria;
    }
}
