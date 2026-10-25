
package com.shieldhero.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class MovimientoParabolicoArquero
        implements EstrategiaMovimiento {

    // Tiempo que tarda la flecha en completar su vuelo.
    private static final float DURACION_VUELO = 1.8f;

    // Altura del punto de control de la parabola.
    // Se ajusta para superar la muralla exterior.
    private static final float ALTURA_CONTROL = 700f;

    private final float inicioX;
    private final float inicioY;

    private final float destinoX;
    private final float destinoY;

    private final float controlX;
    private final float controlY;

    private float tiempoTranscurrido = 0f;

    public MovimientoParabolicoArquero(
            float inicioX,
            float inicioY,
            float destinoX,
            float destinoY) {

        this.inicioX = inicioX;
        this.inicioY = inicioY;

        this.destinoX = destinoX;
        this.destinoY = destinoY;

        // El punto de control se ubica entre
        // el arquero y el lugar de impacto.
        this.controlX = (inicioX + destinoX) / 2f;

        // Elevar la curva sobre la muralla.
        this.controlY = ALTURA_CONTROL;
    }

    @Override
    public void mover(
            Rectangle area,
            float velocidad,
            float delta) {

        tiempoTranscurrido += delta;

        // Progreso del vuelo entre 0 y 1.
        float t = MathUtils.clamp(
            tiempoTranscurrido / DURACION_VUELO,
            0f,
            1f
        );

        float inverso = 1f - t;

        // Curva de Bezier cuadratica para X.
        float nuevaX =
            inverso * inverso * inicioX
            + 2f * inverso * t * controlX
            + t * t * destinoX;

        // Curva de Bezier cuadratica para Y.
        float nuevaY =
            inverso * inverso * inicioY
            + 2f * inverso * t * controlY
            + t * t * destinoY;

        // Actualizar la posicion del proyectil.
        area.setPosition(
            nuevaX,
            nuevaY
        );
    }
}
