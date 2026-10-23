
package com.shieldhero.game;

import com.badlogic.gdx.math.Rectangle;

public class MovimientoVertical implements EstrategiaMovimiento {

    @Override
    public void mover(Rectangle area, float velocidad, float delta) {
        area.y -= velocidad * delta;
    }
}
