package com.shieldhero.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Proyectil {

    // Altura del suelo del nuevo escenario.
    private static final float ALTURA_SUELO = 120f;

    // Dimension visual y de colision de proyectiles.
    private static final float TAMANO_PROYECTIL = 32f;

    private Rectangle area;
    private Texture textura;
    private float velocidad;
    private int daño;

    private EstrategiaMovimiento estrategiaMovimiento;

    public Proyectil(
            float x,
            float y,
            Texture textura,
            float velocidad,
            int daño) {

        this.area = new Rectangle(
            x,
            y,
            TAMANO_PROYECTIL,
            TAMANO_PROYECTIL
        );

        this.textura = textura;
        this.velocidad = velocidad;
        this.daño = daño;

        // Movimiento vertical por defecto.
        this.estrategiaMovimiento =
            new MovimientoVertical();
    }

    // TEMPLATE METHOD
    // Define el orden de procesamiento de cada proyectil.
    // Devuelve true cuando debe eliminarse.
    public final boolean procesar(
            float delta,
            Caballero caballero,
            Castillo castillo) {

        // 1. Actualizar posicion con Strategy.
        actualizar(delta);

        // 2. Comprobar bloqueo del caballero.
        if (colisionaCon(caballero.getArea())) {

            alBloquear(caballero);
            return true;
        }

        // 3. Comprobar impacto en el suelo.
        if (llegoAlSuelo()) {

            alImpactar(castillo);
            return true;
        }

        // 4. El proyectil sigue activo.
        return false;
    }

    public void actualizar(float delta) {

        estrategiaMovimiento.mover(
            area,
            velocidad,
            delta
        );
    }

    // Metodos personalizables por las subclases.
    protected abstract void alBloquear(
        Caballero caballero
    );

    protected abstract void alImpactar(
        Castillo castillo
    );

    public void setEstrategiaMovimiento(
            EstrategiaMovimiento nuevaEstrategia) {

        if (nuevaEstrategia != null) {

            this.estrategiaMovimiento =
                nuevaEstrategia;
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

    // La parte inferior del proyectil toca el suelo.
    public boolean llegoAlSuelo() {

        return area.y <= ALTURA_SUELO;
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
