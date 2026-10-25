
package com.shieldhero.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Flecha extends Proyectil
        implements Bloqueable {

    // Angulo visual actual.
    private float anguloRotacion = 0f;

    // La textura original apunta hacia ABAJO.
    // Para alinearla con atan2 se usan -90 grados.
    private static final float CORRECCION_ANGULO = 90f;

    public Flecha(
            float x,
            float y,
            Texture textura) {

        super(x, y, textura, 300, 1);
    }

    @Override
    public void actualizar(float delta) {

        // Posicion antes de moverse.
        float anteriorX = getArea().x;
        float anteriorY = getArea().y;

        // Actualizar con Strategy.
        super.actualizar(delta);

        // Movimiento real durante este fotograma.
        float movimientoX =
            getArea().x - anteriorX;

        float movimientoY =
            getArea().y - anteriorY;

        // Determinar hacia donde viaja.
        if (Math.abs(movimientoX) > 0.001f
                || Math.abs(movimientoY) > 0.001f) {

            float anguloMovimiento =
                MathUtils.atan2(
                    movimientoY,
                    movimientoX
                ) * MathUtils.radiansToDegrees;

            // Corregir orientacion original del sprite.
            anguloRotacion =
                anguloMovimiento + CORRECCION_ANGULO;
        }
    }

    @Override
    public void dibujar(SpriteBatch batch) {

        // Dibujo con rotacion alrededor del centro.
        dibujarRotado(
            batch,
            anguloRotacion
        );
    }

    @Override
    public int getPuntosBloqueo() {
        return 10;
    }

    @Override
    public void alSerBloqueado(Caballero caballero) {

        caballero.sumarPuntosBloqueo(
            getPuntosBloqueo()
        );
    } 

    // TEMPLATE METHOD: bloqueo.
    @Override
    protected void alBloquear(Caballero caballero) {

        alSerBloqueado(caballero);
    }

    // TEMPLATE METHOD: impacto.
    @Override
    protected void alImpactar(Castillo castillo) {

        castillo.recibirDaño(getDaño());
    }
}
