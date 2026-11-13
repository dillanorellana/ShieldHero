package com.shieldhero.game;

import com.badlogic.gdx.graphics.Texture;

public class BolaFuego extends Proyectil implements Bloqueable {

    public BolaFuego(float x, float y, Texture textura) {
        super(x, y, textura, 380, 2);
    }

    @Override
    public int getPuntosBloqueo() {
        return 30;
    }

    @Override
    public void alSerBloqueado(Caballero caballero) {
        caballero.sumarPuntosBloqueo(getPuntosBloqueo());
    }

    // Template Method: accion cuando se bloquea
    @Override
    protected void alBloquear(Caballero caballero) {
        alSerBloqueado(caballero);
    }

    // Template Method: accion cuando impacta
    @Override
    protected void alImpactar(Castillo castillo) {
        castillo.recibirDaño(getDaño());
    }
}
