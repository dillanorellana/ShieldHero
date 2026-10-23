
package com.shieldhero.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Proyectil {

    private Rectangle area;
    private Texture textura;
    private float velocidad;
    private int daño;

    // Patron Strategy
    private EstrategiaMovimiento estrategiaMovimiento;

    public Proyectil(float x, float y, Texture textura,
                     float velocidad, int daño) {

        this.area = new Rectangle(x, y, 64, 64);
        this.textura = textura;
        this.velocidad = velocidad;
        this.daño = daño;

        // Estrategia inicial: caida vertical
        this.estrategiaMovimiento = new MovimientoVertical();
    }

    public void actualizar(float delta) {

        // Delegar el movimiento a la estrategia
        estrategiaMovimiento.mover(area, velocidad, delta);
    }

    // Permite cambiar el comportamiento en ejecucion
    public void setEstrategiaMovimiento(
            EstrategiaMovimiento nuevaEstrategia) {

        if (nuevaEstrategia != null) {
            this.estrategiaMovimiento = nuevaEstrategia;
        }
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(textura, area.x, area.y,
                   area.width, area.height);
    }

    public boolean colisionaCon(Rectangle otraArea) {
        return area.overlaps(otraArea);
    }

    public boolean llegoAlSuelo() {
        return area.y + area.height < 0;
    }

    public Rectangle getArea() {
        return area;
    }

    public int getDaño() {
        return daño;
    }

    public float getVelocidad() {
        return velocidad;
    }

    public abstract int getPuntosBloqueo();
}
