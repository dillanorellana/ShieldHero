package com.shieldhero.game;

import com.badlogic.gdx.graphics.Texture;

public class Piedra extends Proyectil implements Bloqueable {

    public Piedra(float x, float y, Texture textura) {
        super(x, y, textura, 220, 2);
    }

    @Override
    public int getPuntosBloqueo() {
        return 20;
    }

    @Override
    public void alSerBloqueado(Caballero caballero) {
        caballero.sumarPuntos(getPuntosBloqueo());
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
