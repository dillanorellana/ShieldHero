
package com.shieldhero.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class ArqueroEnemigo {

    private Texture textura;

    private float x;
    private float y;

    private float ancho = 52f;
    private float alto = 70f;

    // ANIMACION DE DISPARO
    private static final float DURACION_DISPARO = 0.30f;
    private static final float RETROCESO_MAXIMO = 5f;

    private static final float ANCHO_ESCENARIO = 800f;

    private float tiempoDisparo = 0f;

    public ArqueroEnemigo(
            Texture textura,
            float x,
            float y) {

        this.textura = textura;
        this.x = x;
        this.y = y;
    }

    // Iniciar animacion al disparar una flecha.
    public void disparar() {

        tiempoDisparo = DURACION_DISPARO;
    }

    // Actualizar tiempo de animacion.
    public void actualizar(float delta) {

        if (tiempoDisparo > 0f) {

            tiempoDisparo = Math.max(
                0f,
                tiempoDisparo - delta
            );
        }
    }

    public void dibujar(SpriteBatch batch) {

        float desplazamientoX = 0f;

        if (tiempoDisparo > 0f) {

            float progreso =
                tiempoDisparo / DURACION_DISPARO;

            float intensidad =
                MathUtils.sin(progreso * MathUtils.PI);

            desplazamientoX =
                RETROCESO_MAXIMO * intensidad;
        }

        // Evitar que la animacion saque al arquero
        // por el borde derecho de la pantalla.
        float posicionDibujoX = MathUtils.clamp(
            x + desplazamientoX,
            0f,
            ANCHO_ESCENARIO - ancho
        );

        batch.draw(
            textura,
            posicionDibujoX,
            y,
            ancho,
            alto
        );
    }

    public boolean estaDisparando() {

        return tiempoDisparo > 0f;
    }

    public void destruir() {

        // La textura compartida se libera en GameScreen.
    }
}
