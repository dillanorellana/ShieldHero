package com.shieldhero.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Proyectil {

    private Rectangle area;
    private Texture textura;
    private float velocidad;
    private int daño;

    private EstrategiaMovimiento estrategiaMovimiento;

    public Proyectil(float x, float y, Texture textura,
                     float velocidad, int daño) {

        this.area = new Rectangle(x, y, 64, 64);
        this.textura = textura;
        this.velocidad = velocidad;
        this.daño = daño;

        this.estrategiaMovimiento = new MovimientoVertical();
    }

    // GM-8: TEMPLATE METHOD
    // Define el orden comun del procesamiento.
    // Devuelve true si el proyectil debe eliminarse.
    public final boolean procesar(
            float delta, Caballero caballero, Castillo castillo) {

        // 1. Mover utilizando Strategy
        actualizar(delta);

        // 2. Comprobar si el caballero lo bloqueo
        if (colisionaCon(caballero.getArea())) {
            alBloquear(caballero);
            return true;
        }

        // 3. Comprobar si impacto el castillo
        if (llegoAlSuelo()) {
            alImpactar(castillo);
            return true;
        }

        // El proyectil continua en pantalla
        return false;
    }

    public void actualizar(float delta) {
        estrategiaMovimiento.mover(area, velocidad, delta);
    }

    // Pasos personalizables por las subclases
    protected abstract void alBloquear(Caballero caballero);

    protected abstract void alImpactar(Castillo castillo);

    public void setEstrategiaMovimiento(
            EstrategiaMovimiento nuevaEstrategia) {

        if (nuevaEstrategia != null) {
            this.estrategiaMovimiento = nuevaEstrategia;
        }
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(
            textura,
            area.x,
            area.y,
            area.width,
            area.height
        );
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
