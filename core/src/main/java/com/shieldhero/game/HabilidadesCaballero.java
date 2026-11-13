
package com.shieldhero.game;

public class HabilidadesCaballero {

    private static final float DURACION_DASH = 0.15f;
    private static final float DISTANCIA_DASH = 150f;

    private static final float RECARGA_DASH_BASE = 1.5f;
    private static final float RECARGA_DASH_MINIMA = 0.5f;

    private static final float DURACION_PARRY_BASE = 0.20f;
    private static final float DURACION_PARRY_MAXIMA = 0.50f;

    private static final float RECARGA_PARRY = 1f;
    private static final float DURACION_FURIA = 45f;

    private static final float DURACION_LENTITUD = 1.5f;
    private static final float MULTIPLICADOR_LENTITUD = 0.70f;

    private float tiempoDash;
    private float tiempoRecargaDash;
    private int direccionDash;

    private float tiempoParry;
    private float tiempoRecargaParry;

    private float recargaDashActual;
    private float duracionParryActual;

    private int nivelesParry;
    private int nivelesDash;

    private boolean parrySupremo;
    private boolean dashSupremo;

    private float tiempoFuria;
    private float tiempoLentitud;

    public void crear() {

        tiempoDash = 0f;
        tiempoRecargaDash = 0f;
        direccionDash = 0;

        tiempoParry = 0f;
        tiempoRecargaParry = 0f;

        recargaDashActual = RECARGA_DASH_BASE;
        duracionParryActual = DURACION_PARRY_BASE;

        nivelesParry = 0;
        nivelesDash = 0;

        parrySupremo = false;
        dashSupremo = false;

        tiempoFuria = 0f;
        tiempoLentitud = 0f;
    }

    public void actualizarTemporizadores(float delta) {

        tiempoRecargaDash = Math.max(
            0f,
            tiempoRecargaDash - delta
        );

        tiempoRecargaParry = Math.max(
            0f,
            tiempoRecargaParry - delta
        );

        tiempoFuria = Math.max(
            0f,
            tiempoFuria - delta
        );

        tiempoLentitud = Math.max(
            0f,
            tiempoLentitud - delta
        );
    }

    public void procesarParry(
            boolean teclaPresionada,
            float delta) {

        if (teclaPresionada
                && tiempoRecargaParry <= 0f
                && tiempoParry <= 0f) {

            tiempoParry = duracionParryActual;
            tiempoRecargaParry = RECARGA_PARRY;

        } else {

            tiempoParry = Math.max(
                0f,
                tiempoParry - delta
            );
        }
    }

    public void iniciarDash(
            boolean teclaPresionada,
            int direccion) {

        if (teclaPresionada
                && direccion != 0
                && tiempoRecargaDash <= 0f
                && tiempoDash <= 0f) {

            direccionDash = direccion;
            tiempoDash = DURACION_DASH;
            tiempoRecargaDash = recargaDashActual;
        }
    }

    public float calcularDesplazamientoX(
            int direccion,
            float velocidadBase,
            float delta) {

        float multiplicador = estaRalentizado()
            ? MULTIPLICADOR_LENTITUD : 1f;

        if (tiempoDash > 0f) {

            float tiempoAplicado = Math.min(
                delta,
                tiempoDash
            );

            float velocidadDash =
                DISTANCIA_DASH / DURACION_DASH;

            float desplazamiento =
                direccionDash
                * velocidadDash
                * tiempoAplicado
                * multiplicador;

            tiempoDash = Math.max(
                0f,
                tiempoDash - delta
            );

            return desplazamiento;
        }

        return direccion
            * velocidadBase
            * delta
            * multiplicador;
    }

    // RALENTIZACION

    public void aplicarRalentizacion() {

        tiempoLentitud = Math.max(
            tiempoLentitud,
            DURACION_LENTITUD
        );

        System.out.println(
            "CABALLERO RALENTIZADO POR EL GIGANTE"
        );
    }

    public boolean estaRalentizado() {
        return tiempoLentitud > 0f;
    }

    public float getTiempoLentitud() {
        return tiempoLentitud;
    }

    // MEJORAS NORMALES

    public void mejorarParry() {

        if (parryAlMaximo()) {
            return;
        }

        duracionParryActual = Math.min(
            DURACION_PARRY_MAXIMA,
            duracionParryActual + 0.10f
        );

        nivelesParry++;
    }

    public void mejorarDash() {

        if (dashAlMaximo()) {
            return;
        }

        recargaDashActual = Math.max(
            RECARGA_DASH_MINIMA,
            recargaDashActual - 0.25f
        );

        nivelesDash++;

        tiempoRecargaDash = Math.min(
            tiempoRecargaDash,
            recargaDashActual
        );
    }

    // MEJORAS ESPECIALES

    public void activarParrySupremo() {

        parrySupremo = true;

        duracionParryActual = DURACION_PARRY_MAXIMA;
        nivelesParry = Math.max(nivelesParry, 3);
    }

    public void activarDashSupremo() {

        dashSupremo = true;

        recargaDashActual = RECARGA_DASH_MINIMA;
        nivelesDash = Math.max(nivelesDash, 4);

        tiempoRecargaDash = Math.min(
            tiempoRecargaDash,
            recargaDashActual
        );
    }

    public void activarFuriaPuntos() {
        tiempoFuria = DURACION_FURIA;
    }

    // CONSULTAS DE HABILIDADES

    public boolean tieneParryMejorado() {
        return nivelesParry > 0;
    }

    public boolean tieneDashMejorado() {
        return nivelesDash > 0;
    }

    public boolean tieneParrySupremo() {
        return parrySupremo;
    }

    public boolean tieneDashSupremo() {
        return dashSupremo;
    }

    public boolean tieneFuriaPuntos() {
        return tiempoFuria > 0f;
    }

    public boolean parryAlMaximo() {
        return duracionParryActual >= DURACION_PARRY_MAXIMA;
    }

    public boolean dashAlMaximo() {
        return recargaDashActual <= RECARGA_DASH_MINIMA;
    }

    public int getNivelParry() {
        return nivelesParry;
    }

    public int getNivelDash() {
        return nivelesDash;
    }

    public float getDuracionParryActual() {
        return duracionParryActual;
    }

    public float getRecargaDashActual() {
        return recargaDashActual;
    }

    public float getTiempoFuriaPuntos() {
        return tiempoFuria;
    }

    public boolean estaEnParry() {
        return tiempoParry > 0f;
    }

    public boolean puedeHacerParry() {
        return tiempoParry <= 0f
            && tiempoRecargaParry <= 0f;
    }

    public float getTiempoRecargaParry() {
        return tiempoRecargaParry;
    }

    public boolean puedeHacerDash() {
        return tiempoDash <= 0f
            && tiempoRecargaDash <= 0f;
    }

    public boolean estaEnDash() {
        return tiempoDash > 0f;
    }

    public float getTiempoRecargaDash() {
        return tiempoRecargaDash;
    }
}
