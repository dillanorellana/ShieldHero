
package com.shieldhero.game;

public class GestorPartida {

    private static final GestorPartida instancia =
        new GestorPartida();

    private static final float DURACION_DESCANSO = 4f;
    private static final float DURACION_RONDA = 30f;

    private static final float INTERVALO_INICIAL = 1.50f;
    private static final float INTERVALO_MINIMO = 0.95f;
    private static final float AUMENTO_CADA = 60f;
    private static final float REDUCCION = 0.05f;

    public enum TipoEvento {
        NINGUNO,
        MEJORA_NORMAL,
        MEJORA_ESPECIAL
    }

    private final ConfiguracionRonda[] rondas;

    private int record;
    private int rondaActual;
    private int rondaSupervivencia;

    private boolean victoria;
    private boolean enDescanso;
    private boolean supervivencia;

    private float tiempoDescanso;
    private float tiempoSupervivencia;

    private int mejorasNormalesPendientes;
    private int mejorasEspecialesPendientes;

    private boolean jefePendiente;
    private boolean jefeActivo;

    private GestorPartida() {

        rondas = new ConfiguracionRonda[] {

            new ConfiguracionRonda.Builder()
                .numero(1)
                .puntosObjetivo(200)
                .intervaloAparicion(1.20f)
                .tiposProyectiles(1)
                .probabilidadDiagonal(0f)
                .distanciaMaxima(250f)
                .construir(),

            new ConfiguracionRonda.Builder()
                .numero(2)
                .puntosObjetivo(500)
                .intervaloAparicion(1.10f)
                .tiposProyectiles(2)
                .probabilidadDiagonal(0f)
                .distanciaMaxima(350f)
                .construir(),

            new ConfiguracionRonda.Builder()
                .numero(3)
                .puntosObjetivo(900)
                .intervaloAparicion(1.00f)
                .tiposProyectiles(3)
                .probabilidadDiagonal(0f)
                .distanciaMaxima(450f)
                .construir(),

            new ConfiguracionRonda.Builder()
                .numero(4)
                .puntosObjetivo(1400)
                .intervaloAparicion(1.15f)
                .tiposProyectiles(3)
                .probabilidadDiagonal(0.10f)
                .distanciaMaxima(450f)
                .construir(),

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

    public static GestorPartida getInstancia() {
        return instancia;
    }

    public void actualizarRecord(int puntos) {
        record = Math.max(record, puntos);
    }

    public int getRecord() {
        return record;
    }

    // =====================================
    // TUTORIAL
    // =====================================

    public void actualizar(int puntos) {

        if (supervivencia || victoria || enDescanso) {
            return;
        }

        if (puntos >= getObjetivoActual()) {

            if (rondaActual == rondas.length) {
                victoria = true;
            } else {
                enDescanso = true;
                tiempoDescanso = DURACION_DESCANSO;
            }
        }
    }

    public void actualizarDescanso(float delta) {

        if (!enDescanso) {
            return;
        }

        tiempoDescanso -= Math.max(0f, delta);

        if (tiempoDescanso <= 0f) {
            tiempoDescanso = 0f;
            enDescanso = false;
            rondaActual++;
        }
    }

    // =====================================
    // SUPERVIVENCIA
    // =====================================

    public void iniciarSupervivencia() {

        if (!victoria || supervivencia) {
            return;
        }

        victoria = false;
        supervivencia = true;
        enDescanso = false;

        rondaSupervivencia = 6;

        tiempoDescanso = 0f;
        tiempoSupervivencia = 0f;

        mejorasNormalesPendientes = 0;
        mejorasEspecialesPendientes = 0;

        jefePendiente = false;
        jefeActivo = false;

        registrarEventosDeRonda(6);
    }

    public void actualizarSupervivencia(float delta) {

        if (!supervivencia
                || hayEventoPendiente()
                || jefePendiente
                || jefeActivo) {
            return;
        }

        tiempoSupervivencia += Math.max(0f, delta);

        int nuevaRonda = 6 + (int) (
            tiempoSupervivencia / DURACION_RONDA
        );

        while (rondaSupervivencia < nuevaRonda) {

            rondaSupervivencia++;

            registrarEventosDeRonda(rondaSupervivencia);

            if (hayEventoPendiente()) {
                break;
            }
        }
    }

    private void registrarEventosDeRonda(int ronda) {

        if (ronda % 3 == 0) {
            mejorasNormalesPendientes++;
        }

        if (ronda % 10 == 0) {

            mejorasEspecialesPendientes++;

            // El jefe espera a que se elija
            // la recompensa especial.
            jefePendiente = true;
        }

        System.out.println(
            "SUPERVIVENCIA - RONDA " + ronda
        );
    }

    // =====================================
    // MEJORAS
    // =====================================

    public TipoEvento getEventoPendiente() {

        if (mejorasNormalesPendientes > 0) {
            return TipoEvento.MEJORA_NORMAL;
        }

        if (mejorasEspecialesPendientes > 0) {
            return TipoEvento.MEJORA_ESPECIAL;
        }

        return TipoEvento.NINGUNO;
    }

    public boolean hayEventoPendiente() {

        return mejorasNormalesPendientes > 0
            || mejorasEspecialesPendientes > 0;
    }

    public void confirmarEventoMejora() {

        TipoEvento evento = getEventoPendiente();

        if (evento == TipoEvento.MEJORA_NORMAL) {
            mejorasNormalesPendientes--;

        } else if (evento == TipoEvento.MEJORA_ESPECIAL) {
            mejorasEspecialesPendientes--;
        }
    }

    // =====================================
    // JEFES
    // =====================================

    public boolean debeAparecerJefe() {

        return supervivencia
            && jefePendiente
            && !jefeActivo
            && !hayEventoPendiente();
    }

    public void iniciarCombateJefe() {

        if (!debeAparecerJefe()) {
            return;
        }

        jefePendiente = false;
        jefeActivo = true;

        System.out.println(
            "INICIA COMBATE DE JEFE - RONDA "
            + rondaSupervivencia
        );
    }

    public void finalizarCombateJefe() {

        jefeActivo = false;

        System.out.println(
            "COMBATE DE JEFE FINALIZADO"
        );
    }

    public boolean hayJefeActivo() {
        return jefeActivo;
    }

    public boolean esRondaDeJefe() {

        return supervivencia
            && rondaSupervivencia >= 10
            && rondaSupervivencia % 10 == 0;
    }

    public int getNumeroJefeActual() {
        return Math.max(1, rondaSupervivencia / 10);
    }

    public int getPrimeraRondaJefe() {
        return 10;
    }

    // =====================================
    // DIFICULTAD
    // =====================================

    public float getIntervaloAparicionActual() {

        if (!supervivencia) {

            return getConfiguracionActual()
                .getIntervaloAparicion();
        }

        int aumentos = (int) (
            tiempoSupervivencia / AUMENTO_CADA
        );

        float intervalo = Math.max(
            INTERVALO_MINIMO,
            INTERVALO_INICIAL - aumentos * REDUCCION
        );

        // Durante el jefe, enemigos normales
        // al 60% de su frecuencia.
        if (jefeActivo) {
            intervalo /= 0.60f;
        }

        return intervalo;
    }

    // =====================================
    // CONSULTAS
    // =====================================

    public int getRondaActual() {
        return supervivencia
            ? rondaSupervivencia : rondaActual;
    }

    public int getRondaSupervivencia() {
        return rondaSupervivencia;
    }

    public int getTotalRondas() {
        return rondas.length;
    }

    public int getObjetivoActual() {
        return getConfiguracionActual().getPuntosObjetivo();
    }

    public int getPuntosRestantes(int puntos) {

        if (supervivencia) {
            return 0;
        }

        return Math.max(0, getObjetivoActual() - puntos);
    }

    public ConfiguracionRonda getConfiguracionActual() {

        int indice = Math.max(
            0,
            Math.min(rondaActual - 1, rondas.length - 1)
        );

        return rondas[indice];
    }

    public float getTiempoSupervivencia() {
        return tiempoSupervivencia;
    }

    public float getDuracionRondaSupervivencia() {
        return DURACION_RONDA;
    }

    public float getTiempoRestanteRondaSupervivencia() {

        if (!supervivencia) {
            return 0f;
        }

        return DURACION_RONDA
            - tiempoSupervivencia % DURACION_RONDA;
    }

    public boolean hayVictoria() {
        return victoria;
    }

    public boolean estaEnDescanso() {
        return enDescanso;
    }

    public boolean estaEnSupervivencia() {
        return supervivencia;
    }

    public int getTiempoDescanso() {
        return (int) Math.ceil(tiempoDescanso);
    }

    public int getRondaSuperada() {
        return rondaActual;
    }

    public int getSiguienteRonda() {

        return Math.min(
            rondaActual + 1,
            rondas.length
        );
    }

    // =====================================
    // REINICIAR
    // =====================================

    public void reiniciarRondas() {

        rondaActual = 1;
        rondaSupervivencia = 6;

        victoria = false;
        enDescanso = false;
        supervivencia = false;

        tiempoDescanso = 0f;
        tiempoSupervivencia = 0f;

        mejorasNormalesPendientes = 0;
        mejorasEspecialesPendientes = 0;

        jefePendiente = false;
        jefeActivo = false;
    }
}
