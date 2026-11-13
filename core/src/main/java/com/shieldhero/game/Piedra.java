
package com.shieldhero.game;

import com.badlogic.gdx.graphics.Texture;

public class Piedra extends Proyectil implements Bloqueable {

    private static final int PUNTOS_BLOQUEO = 20;

    public Piedra(float x, float y, Texture textura) {
        super(x, y, textura, 220, 2);
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
