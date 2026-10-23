package com.shieldhero.game;

public class GestorPartida {

    // Patron Singleton
    private static final GestorPartida instancia =
        new GestorPartida();

    // Duracion del descanso entre rondas
    private static final float DURACION_DESCANSO = 4f;

    // Configuracion del modo supervivencia
    private static final float INTERVALO_INICIAL_SUPERVIVENCIA = 1.05f;
    private static final float INTERVALO_MINIMO_SUPERVIVENCIA = 0.75f;
    private static final float TIEMPO_AUMENTO_DIFICULTAD = 30f;
    private static final float REDUCCION_INTERVALO = 0.05f;

    private final ConfiguracionRonda[] rondas;

    private int record;
    private int rondaActual;

    private boolean victoria;
    private boolean enDescanso;
    private boolean supervivencia;

    private float tiempoDescanso;
    private float tiempoSupervivencia;

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
                .intervaloAparicion(1.15f)
                .tiposProyectiles(3)
                .probabilidadDiagonal(0.10f)
                .distanciaMaxima(450f)
                .construir(),

            // RONDA 5: Asedio final
            new ConfiguracionRonda.Builder()
                .numero(5)
                .puntosObjetivo(2000)
                .intervaloAparicion(1.05f)
                .tiposProyectiles(3)
                .probabilidadDiagonal(0.15f)
                .distanciaMaxima(500f)
                .construir()
        };

        record = 0;

        reiniciarRondas();
    }

    // Obtener la unica instancia del gestor
    public static GestorPartida getInstancia() {
        return instancia;
    }

    // Actualizar el record de puntos
    public void actualizarRecord(int puntos) {

        if (puntos > record) {
            record = puntos;
        }
    }

    // Comprobar si el jugador supero la ronda actual
    public void actualizar(int puntos) {

        // En supervivencia ya no existen objetivos de puntos
        if (supervivencia || victoria || enDescanso) {
            return;
        }

        if (puntos >= getObjetivoActual()) {

            // Si supera la quinta ronda, obtiene la victoria
            if (rondaActual == rondas.length) {

                victoria = true;

            } else {

                iniciarDescanso();
            }
        }
    }

    // Iniciar descanso entre rondas
    private void iniciarDescanso() {

        enDescanso = true;
        tiempoDescanso = DURACION_DESCANSO;
    }

    // Actualizar cuenta regresiva del descanso
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

    // Activar supervivencia despues de completar las 5 rondas
    public void iniciarSupervivencia() {

        if (!victoria || supervivencia) {
            return;
        }

        supervivencia = true;
        victoria = false;
        enDescanso = false;

        tiempoDescanso = 0f;
        tiempoSupervivencia = 0f;
    }

    // Actualizar tiempo del modo supervivencia
    public void actualizarSupervivencia(float delta) {

        if (!supervivencia) {
            return;
        }

        tiempoSupervivencia += delta;
    }

    // Reiniciar el progreso para una nueva partida
    public void reiniciarRondas() {

        rondaActual = 1;

        victoria = false;
        enDescanso = false;
        supervivencia = false;

        tiempoDescanso = 0f;
        tiempoSupervivencia = 0f;
    }

    // Obtener el record
    public int getRecord() {
        return record;
    }

    // Obtener ronda actual
    public int getRondaActual() {
        return rondaActual;
    }

    // Obtener cantidad total de rondas
    public int getTotalRondas() {
        return rondas.length;
    }

    // Obtener objetivo de puntos de la ronda
    public int getObjetivoActual() {
        return getConfiguracionActual().getPuntosObjetivo();
    }

    // Obtener intervalo de aparicion de proyectiles
    public float getIntervaloAparicionActual() {

        // Durante supervivencia la dificultad aumenta
        if (supervivencia) {

            int aumentos =
                (int) (tiempoSupervivencia / TIEMPO_AUMENTO_DIFICULTAD);

            float intervalo =
                INTERVALO_INICIAL_SUPERVIVENCIA
                - aumentos * REDUCCION_INTERVALO;

            return Math.max(
                INTERVALO_MINIMO_SUPERVIVENCIA,
                intervalo
            );
        }

        // Durante las rondas normales se usa su configuracion
        return getConfiguracionActual().getIntervaloAparicion();
    }

    // Obtener puntos restantes para completar la ronda
    public int getPuntosRestantes(int puntos) {

        if (supervivencia) {
            return 0;
        }

        return Math.max(0, getObjetivoActual() - puntos);
    }

    // Obtener configuracion de la ronda actual
    public ConfiguracionRonda getConfiguracionActual() {
        return rondas[rondaActual - 1];
    }

    // Comprobar si se completaron las 5 rondas
    public boolean hayVictoria() {
        return victoria;
    }

    // Comprobar si estamos en descanso
    public boolean estaEnDescanso() {
        return enDescanso;
    }

    // Comprobar si estamos en supervivencia
    public boolean estaEnSupervivencia() {
        return supervivencia;
    }

    // Obtener tiempo transcurrido en supervivencia
    public float getTiempoSupervivencia() {
        return tiempoSupervivencia;
    }

    // Obtener segundos restantes del descanso
    public int getTiempoDescanso() {
        return (int) Math.ceil(tiempoDescanso);
    }

    // Obtener numero de ronda superada
    public int getRondaSuperada() {
        return rondaActual;
    }

    // Obtener numero de la siguiente ronda
    public int getSiguienteRonda() {

        return Math.min(
            rondaActual + 1,
            rondas.length
        );
    }
}