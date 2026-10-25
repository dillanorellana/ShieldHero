
package com.shieldhero.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class PiedraGigante {

    private static final float ANCHO = 42f;
    private static final float ALTO = 42f;

    private static final float DURACION_IDA = 2.5f;
    private static final float ALTURA_IDA = 260f;

    private static final float DURACION_VUELTA = 1.2f;
    private static final float ALTURA_VUELTA = 150f;

    private static final float SUELO = 120f;

    private static final int DANIO_JEFE = 2;

    private enum Estado {
        VOLANDO,
        DEVUELTA,
        TERMINADA
    }

    private final Texture textura;
    private final Rectangle area;
    private final JefeGigante jefe;

    private final float origenX;
    private final float origenY;
    private final float destinoX;
    private final float destinoY;

    private float retornoInicioX;
    private float retornoInicioY;
    private float retornoDestinoX;
    private float retornoDestinoY;

    private Estado estado;

    private float tiempoActual;
    private float anguloRotacion;

    private boolean impactoProcesado;

    public PiedraGigante(
            Texture textura,
            JefeGigante jefe,
            float destinoX) {

        this.textura = textura;
        this.jefe = jefe;

        origenX = jefe.getOrigenAtaqueX();
        origenY = jefe.getOrigenAtaqueY();

        this.destinoX = MathUtils.clamp(
            destinoX,
            42f,
            373f
        );

        destinoY = SUELO;

        area = new Rectangle(
            origenX,
            origenY,
            ANCHO,
            ALTO
        );

        estado = Estado.VOLANDO;

        tiempoActual = 0f;
        anguloRotacion = 0f;

        impactoProcesado = false;
    }

    public void actualizar(
            float delta,
            Caballero caballero) {

        if (estado == Estado.TERMINADA) {
            return;
        }

        tiempoActual += Math.max(0f, delta);

        if (estado == Estado.VOLANDO) {

            actualizarIda(caballero);

        } else if (estado == Estado.DEVUELTA) {

            actualizarVuelta();
        }

        anguloRotacion += 180f * delta;
    }

    private void actualizarIda(Caballero caballero) {

        float t = MathUtils.clamp(
            tiempoActual / DURACION_IDA,
            0f,
            1f
        );

        float x = MathUtils.lerp(
            origenX,
            destinoX,
            t
        );

        float baseY = MathUtils.lerp(
            origenY,
            destinoY,
            t
        );

        float arco =
            4f * ALTURA_IDA * t * (1f - t);

        area.setPosition(
            x,
            baseY + arco
        );

        if (area.overlaps(caballero.getArea())) {

            if (caballero.estaEnParry()) {

                devolverAlGigante();

            } else {

                caballero.aplicarRalentizacion();

                System.out.println(
                    "PIEDRA GIGANTE: CABALLERO RALENTIZADO"
                );

                terminar();
            }

            return;
        }

        if (t >= 1f || area.y <= SUELO) {

            if (!impactoProcesado) {

                impactoProcesado = true;

                System.out.println(
                    "PIEDRA GIGANTE CAYO EN EL PATIO"
                );
            }

            terminar();
        }
    }

    private void devolverAlGigante() {

        estado = Estado.DEVUELTA;

        tiempoActual = 0f;

        retornoInicioX = area.x;
        retornoInicioY = area.y;

        retornoDestinoX = jefe.getOrigenAtaqueX();
        retornoDestinoY = jefe.getOrigenAtaqueY();

        System.out.println(
            "PARRY PERFECTO: ROCA DEVUELTA"
        );
    }

    private void actualizarVuelta() {

        float t = MathUtils.clamp(
            tiempoActual / DURACION_VUELTA,
            0f,
            1f
        );

        float x = MathUtils.lerp(
            retornoInicioX,
            retornoDestinoX,
            t
        );

        float baseY = MathUtils.lerp(
            retornoInicioY,
            retornoDestinoY,
            t
        );

        float arco =
            4f * ALTURA_VUELTA * t * (1f - t);

        area.setPosition(
            x,
            baseY + arco
        );

        if (area.overlaps(jefe.getArea()) || t >= 1f) {

            if (!impactoProcesado) {

                impactoProcesado = true;

                if (jefe.estaActivo()) {
                    jefe.recibirDanio(DANIO_JEFE);
                }
            }

            terminar();
        }
    }

    private void terminar() {
        estado = Estado.TERMINADA;
    }

    public void dibujar(SpriteBatch batch) {

        if (estado == Estado.TERMINADA) {
            return;
        }

        batch.draw(
            textura,
            area.x,
            area.y,
            area.width / 2f,
            area.height / 2f,
            area.width,
            area.height,
            1f,
            1f,
            anguloRotacion,
            0,
            0,
            textura.getWidth(),
            textura.getHeight(),
            false,
            false
        );
    }

    public boolean estaTerminada() {
        return estado == Estado.TERMINADA;
    }

    public boolean fueDevuelta() {
        return estado == Estado.DEVUELTA;
    }

    public Rectangle getArea() {
        return area;
    }

    public void destruir() {
        // Textura compartida con GameScreen.
    }
}
