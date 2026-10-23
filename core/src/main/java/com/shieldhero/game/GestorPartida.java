package com.shieldhero.game;

public class GestorPartida {

    // Patron Singleton
    private static final GestorPartida instancia =
        new GestorPartida();

    private static final float DURACION_DESCANSO = 4f;

    private final ConfiguracionRonda[] rondas;

    private int record;
    private int rondaActual;

    private boolean victoria;
    private boolean enDescanso;

    private float tiempoDescanso;

    private GestorPartida() {

        rondas = new ConfiguracionRonda[] {

            // RONDA 1: Solo flechas
            new ConfiguracionRonda.Builder()
                .numero(1)
                .puntosObjetivo(200)
                .intervaloAparicion(1.20f)
                .tiposProyectiles(1)
                .probabilidadDiagonal(0f)
                .distanciaMaxima(250f)
                .construir(),

            // RONDA 2: Flechas y piedras
            new ConfiguracionRonda.Builder()
                .numero(2)
                .puntosObjetivo(500)
                .intervaloAparicion(1.10f)
                .tiposProyectiles(2)
                .probabilidadDiagonal(0f)
                .distanciaMaxima(350f)
                .construir(),

            // RONDA 3: Aparecen las bolas de fuego
            new ConfiguracionRonda.Builder()
                .numero(3)
                .puntosObjetivo(900)
                .intervaloAparicion(1.00f)
                .tiposProyectiles(3)
                .probabilidadDiagonal(0f)
                .distanciaMaxima(450f)
                .construir(),

            // RONDA 4: Primeros ataques diagonales
            new ConfiguracionRonda.Builder()
                .numero(4)
                .puntosObjetivo(1400)
                .intervaloAparicion(0.90f)
                .tiposProyectiles(3)
                .probabilidadDiagonal(0.10f)
                .distanciaMaxima(550f)
                .construir(),

            // RONDA 5: Asedio final
            new ConfiguracionRonda.Builder()
                .numero(5)
                .puntosObjetivo(2000)
                .intervaloAparicion(0.80f)
                .tiposProyectiles(3)
                .probabilidadDiagonal(0.20f)
                .distanciaMaxima(736f)
                .construir()
        };

        record = 0;
        reiniciarRondas();
    }

    public static GestorPartida getInstancia() {
        return instancia;
    }

    public void actualizarRecord(int puntos) {
        if (puntos > record) {
            record = puntos;
        }
    }

    // Comprobar si el jugador supero la ronda
    public void actualizar(int puntos) {

        if (victoria || enDescanso) {
            return;
        }

        if (puntos >= getObjetivoActual()) {

            if (rondaActual == rondas.length) {
                victoria = true;
            } else {
                iniciarDescanso();
            }
        }
    }

    private void iniciarDescanso() {
        enDescanso = true;
        tiempoDescanso = DURACION_DESCANSO;
    }

    // Actualizar la cuenta regresiva del descanso
    public void actualizarDescanso(float delta) {

        if (!enDescanso) {
            return;
        }

        tiempoDescanso -= delta;

        if (tiempoDescanso <= 0f) {

            tiempoDescanso = 0f;
            enDescanso = false;

            rondaActual++;
        }
    }

    public void reiniciarRondas() {
        rondaActual = 1;
        victoria = false;
        enDescanso = false;
        tiempoDescanso = 0f;
    }

    public int getRecord() {
        return record;
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

    public boolean estaEnDescanso() {
        return enDescanso;
    }

    public int getTiempoDescanso() {
        return (int) Math.ceil(tiempoDescanso);
    }

    public int getRondaSuperada() {
        return rondaActual;
    }

    public int getSiguienteRonda() {
        return Math.min(rondaActual + 1, rondas.length);
    }
}