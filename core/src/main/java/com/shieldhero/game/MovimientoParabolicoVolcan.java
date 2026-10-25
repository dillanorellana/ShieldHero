
package com.shieldhero.game;

import com.badlogic.gdx.math.Rectangle;

public class MovimientoParabolicoVolcan
        implements EstrategiaMovimiento {

    // Tiempo que tarda la bola de fuego en llegar.
    private static final float DURACION_VUELO = 3.0f;

    // Altura adicional de la trayectoria.
    private static final float ALTURA_EXTRA = 170f;

    private final float inicioX;
    private final float inicioY;

    private final float velocidadHorizontal;
    private final float velocidadVerticalInicial;
    private final float gravedad;

    private float tiempoTranscurrido = 0f;

    public MovimientoParabolicoVolcan(
            float inicioX,
            float inicioY,
            float destinoX,
            float destinoY) {

        this.inicioX = inicioX;
        this.inicioY = inicioY;

        // Movimiento horizontal uniforme.
        velocidadHorizontal =
            (destinoX - inicioX) / DURACION_VUELO;

        float desplazamientoY = destinoY - inicioY;

        // Altura de referencia para la parabola.
        float alturaReferencia =
            Math.max(inicioY, destinoY) + ALTURA_EXTRA;

        // Calcular gravedad para alcanzar el destino.
        float gravedadCalculada = 2f * (
            2f * (alturaReferencia - inicioY)
            - desplazamientoY
        ) / (DURACION_VUELO * DURACION_VUELO);

        gravedad = Math.max(1f, gravedadCalculada);

        // Velocidad inicial vertical.
        velocidadVerticalInicial =
            desplazamientoY / DURACION_VUELO
            + gravedad * DURACION_VUELO / 2f;
    }

    @Override
    public void mover(
            Rectangle area,
            float velocidad,
            float delta) {

        tiempoTranscurrido = Math.min(
            tiempoTranscurrido + delta,
            DURACION_VUELO
        );

        float t = tiempoTranscurrido;

        float nuevaX =
            inicioX + velocidadHorizontal * t;

        float nuevaY =
            inicioY
            + velocidadVerticalInicial * t
            - 0.5f * gravedad * t * t;

        area.setPosition(nuevaX, nuevaY);
    }
}
