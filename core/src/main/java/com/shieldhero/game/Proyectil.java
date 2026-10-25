
package com.shieldhero.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Proyectil {

    private static final float ALTURA_SUELO = 120f;
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

        area = new Rectangle(
            x, y,
            TAMANO_PROYECTIL,
            TAMANO_PROYECTIL
        );

        this.textura = textura;
        this.velocidad = velocidad;
        this.daño = daño;

        estrategiaMovimiento =
            new MovimientoVertical();
    }

    // =====================================
    // TEMPLATE METHOD ORIGINAL
    // =====================================

    public final boolean procesar(
            float delta,
            Caballero caballero,
            Castillo castillo) {

        return procesar(
            delta,
            caballero,
            castillo,
            null,
            false
        );
    }

    // =====================================
    // VERSION CON GUARDIAN
    // =====================================

    public final boolean procesar(
            float delta,
            Caballero caballero,
            Castillo castillo,
            GuardianExtra guardian) {

        return procesar(
            delta,
            caballero,
            castillo,
            guardian,
            false
        );
    }

    // =====================================
    // VERSION CON HABILIDADES PODEROSAS
    // =====================================

    public final boolean procesar(
            float delta,
            Caballero caballero,
            Castillo castillo,
            GuardianExtra guardian,
            boolean escudoTemporalActivo) {

        // Mantener el movimiento Strategy original.
        actualizar(delta);

        // Escudo temporal Z:
        // protege al caballero y al castillo
        // de los proyectiles normales.
        if (escudoTemporalActivo) {
            return true;
        }

        // Guardia permanente con aura.
        if (guardian != null
                && guardian.estaColocado()
                && guardian.protege(area)) {

            guardian.registrarBloqueo();
            return true;
        }

        // Colision original con caballero.
        if (colisionaCon(caballero.getArea())) {

            alBloquear(caballero);
            return true;
        }

        // Impacto original con castillo.
        if (llegoAlSuelo()) {

            alImpactar(castillo);
            return true;
        }

        return false;
    }

    public void actualizar(float delta) {

        estrategiaMovimiento.mover(
            area,
            velocidad,
            delta
        );
    }

    protected abstract void alBloquear(
        Caballero caballero
    );

    protected abstract void alImpactar(
        Castillo castillo
    );

    public void setEstrategiaMovimiento(
            EstrategiaMovimiento nuevaEstrategia) {

        if (nuevaEstrategia != null) {
            estrategiaMovimiento = nuevaEstrategia;
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

    protected void dibujarRotado(
            SpriteBatch batch,
            float angulo) {

        batch.draw(
            textura,
            area.x,
            area.y,
            area.width / 2f,
            area.height / 2f,
            area.width,
            area.height,
            1f,
            1f,
            angulo,
            0,
            0,
            textura.getWidth(),
            textura.getHeight(),
            false,
            false
        );
    }

    public boolean colisionaCon(Rectangle otraArea) {
        return area.overlaps(otraArea);
    }

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
