
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
}
