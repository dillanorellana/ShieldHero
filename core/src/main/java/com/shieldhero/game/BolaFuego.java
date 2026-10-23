
package com.shieldhero.game;

import com.badlogic.gdx.graphics.Texture;

public class BolaFuego extends Proyectil implements Bloqueable {

    public BolaFuego(float x, float y, Texture textura) {
        super(x, y, textura, 380, 3);
    }

    @Override
    public int getPuntosBloqueo() {
        return 30;
    }

    @Override
    public void alSerBloqueado(Caballero caballero) {
        caballero.sumarPuntos(getPuntosBloqueo());
    }
}
