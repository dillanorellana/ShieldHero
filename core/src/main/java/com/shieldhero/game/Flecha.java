package com.shieldhero.game;

import com.badlogic.gdx.graphics.Texture;

public class Flecha extends Proyectil implements Bloqueable {

    public Flecha(float x, float y, Texture textura) {
        super(x, y, textura, 300, 1);
    }

    @Override
    public int getPuntosBloqueo() {
        return 10;
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
