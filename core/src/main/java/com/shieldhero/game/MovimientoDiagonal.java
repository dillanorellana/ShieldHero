
package com.shieldhero.game;

import com.badlogic.gdx.math.Rectangle;

public class MovimientoDiagonal implements EstrategiaMovimiento {

    private float direccionHorizontal;

    public MovimientoDiagonal(float direccionHorizontal) {
        this.direccionHorizontal = direccionHorizontal;
    }

    @Override
    public void mover(Rectangle area, float velocidad, float delta) {

        area.y -= velocidad * delta;

        // Movimiento lateral suave: 20% de la velocidad vertical
        area.x += velocidad * 0.2f * direccionHorizontal * delta;

        // Mantener el proyectil dentro de la pantalla
        if (area.x < 0) {
            area.x = 0;
        }

        if (area.x > 800 - area.width) {
            area.x = 800 - area.width;
        }
    }
}
