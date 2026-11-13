
package com.shieldhero.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Flecha extends Proyectil implements Bloqueable {

    private static final int PUNTOS_BLOQUEO = 10;
    private static final float CORRECCION_ANGULO = 90f;

    private float anguloRotacion = 0f;

    public Flecha(float x, float y, Texture textura) {
        super(x, y, textura, 300, 1);
    }

    @Override
    public void actualizar(float delta) {

        float anteriorX = getArea().x;
        float anteriorY = getArea().y;

        super.actualizar(delta);

        float movimientoX = getArea().x - anteriorX;
        float movimientoY = getArea().y - anteriorY;

        if (Math.abs(movimientoX) > 0.001f
                || Math.abs(movimientoY) > 0.001f) {

            float anguloMovimiento = MathUtils.atan2(
                movimientoY,
                movimientoX
            ) * MathUtils.radiansToDegrees;

            anguloRotacion =
                anguloMovimiento + CORRECCION_ANGULO;
        }
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        dibujarRotado(batch, anguloRotacion);
    }

    @Override
    public int getPuntosBloqueo() {
        return PUNTOS_BLOQUEO;
    }

    // INTERFAZ BLOQUEABLE
    @Override
    public void alSerBloqueado(Caballero caballero) {
        caballero.sumarPuntosBloqueo(getPuntosBloqueo());
    }

    // TEMPLATE METHOD

    @Override
    protected void alBloquear(Caballero caballero) {
    	SistemaBloqueo.procesarBloqueo(this, caballero);
    }

    @Override
    protected void alImpactar(Castillo castillo) {
        castillo.recibirDaño(getDaño());
    }
}
