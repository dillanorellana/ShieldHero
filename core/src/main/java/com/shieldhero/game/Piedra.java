
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
}
