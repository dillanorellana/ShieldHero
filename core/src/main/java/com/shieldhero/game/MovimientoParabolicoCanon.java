
package com.shieldhero.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class MovimientoParabolicoCanon
        implements EstrategiaMovimiento {

    // Los cañonazos son mas lentos que las flechas.
    private static final float DURACION_VUELO = 2.4f;

    // Punto de control que determina la altura
    // de la trayectoria sobre la muralla.
    private static final float ALTURA_CONTROL = 650f;

    private final float inicioX;
    private final float inicioY;

    private final float destinoX;
    private final float destinoY;

    private final float controlX;
    private final float controlY;

    private float tiempoTranscurrido = 0f;

    public MovimientoParabolicoCanon(
            float inicioX,
            float inicioY,
            float destinoX,
            float destinoY) {

        this.inicioX = inicioX;
        this.inicioY = inicioY;

        this.destinoX = destinoX;
        this.destinoY = destinoY;

        // Punto de control entre el cañon
        // y el objetivo del patio.
        this.controlX = (inicioX + destinoX) / 2f;

        // Elevar la piedra sobre la muralla.
        this.controlY = ALTURA_CONTROL;
    }

    @Override
    public void mover(
            Rectangle area,
            float velocidad,
            float delta) {

        tiempoTranscurrido += delta;

        float t = MathUtils.clamp(
            tiempoTranscurrido / DURACION_VUELO,
            0f,
            1f
        );

        float inverso = 1f - t;

        // Curva de Bezier: movimiento horizontal.
        float nuevaX =
            inverso * inverso * inicioX
            + 2f * inverso * t * controlX
            + t * t * destinoX;

        // Curva de Bezier: subida y caida.
        float nuevaY =
            inverso * inverso * inicioY
            + 2f * inverso * t * controlY
            + t * t * destinoY;

        area.setPosition(nuevaX, nuevaY);
    }
}
