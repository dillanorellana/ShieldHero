package com.shieldhero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Caballero {

    private Rectangle bucket;

    private Texture bucketImage;
    private Texture imagenParry;

    private Sound sonidoHerido;

    private int vidas = 3;
    private int puntos = 0;
    private int velx = 400;

    private boolean herido = false;
    private int tiempoHeridoMax = 50;
    private int tiempoHerido;

    // DASH
    private static final float DURACION_DASH = 0.15f;
    private static final float RECARGA_DASH = 1.5f;
    private static final float DISTANCIA_DASH = 150f;

    private float tiempoDash = 0f;
    private float tiempoRecargaDash = 0f;
    private int direccionDash = 0;

    // PARRY
    private static final float DURACION_PARRY = 0.20f;
    private static final float RECARGA_PARRY = 1.0f;

    private float tiempoParry = 0f;
    private float tiempoRecargaParry = 0f;

    public Caballero(Texture tex, Sound ss) {
        bucketImage = tex;
        sonidoHerido = ss;
    }

    // Asignar el sprite del caballero levantando el escudo
    public void setImagenParry(Texture imagenParry) {
        this.imagenParry = imagenParry;
    }

    public void crear() {

        bucket = new Rectangle();
        bucket.set(368, 20, 64, 64);

        tiempoDash = 0f;
        tiempoRecargaDash = 0f;
        direccionDash = 0;

        tiempoParry = 0f;
        tiempoRecargaParry = 0f;
    }

    public void actualizarMovimiento() {

        float delta = Gdx.graphics.getDeltaTime();

        // Actualizar tiempos de recarga
        tiempoRecargaDash = Math.max(
            0f,
            tiempoRecargaDash - delta
        );

        tiempoRecargaParry = Math.max(
            0f,
            tiempoRecargaParry - delta
        );

        // PARRY: barra espaciadora
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                && tiempoRecargaParry <= 0f
                && tiempoParry <= 0f) {

            tiempoParry = DURACION_PARRY;
            tiempoRecargaParry = RECARGA_PARRY;

        } else {

            tiempoParry = Math.max(
                0f,
                tiempoParry - delta
            );
        }

        // Movimiento izquierda/derecha
        boolean izquierda =
            Gdx.input.isKeyPressed(Input.Keys.A)
            || Gdx.input.isKeyPressed(Input.Keys.LEFT);

        boolean derecha =
            Gdx.input.isKeyPressed(Input.Keys.D)
            || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        int direccion = 0;

        if (izquierda && !derecha) {
            direccion = -1;
        } else if (derecha && !izquierda) {
            direccion = 1;
        }

        // DASH: Shift + direccion
        boolean pulsarShift =
            Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)
            || Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT);

        if (pulsarShift
                && direccion != 0
                && tiempoRecargaDash <= 0f
                && tiempoDash <= 0f) {

            direccionDash = direccion;
            tiempoDash = DURACION_DASH;
            tiempoRecargaDash = RECARGA_DASH;
        }

        // Ejecutar Dash o movimiento normal
        if (tiempoDash > 0f) {

            float tiempoAplicado = Math.min(
                delta,
                tiempoDash
            );

            float velocidadDash =
                DISTANCIA_DASH / DURACION_DASH;

            bucket.x += direccionDash
                * velocidadDash
                * tiempoAplicado;

            tiempoDash = Math.max(
                0f,
                tiempoDash - delta
            );

        } else {

            bucket.x += direccion * velx * delta;
        }

        // Limites de pantalla
        bucket.x = MathUtils.clamp(
            bucket.x,
            0f,
            800f - bucket.width
        );
    }

    public void dibujar(SpriteBatch batch) {

        if (estaEnParry()) {

            // Durante el Parry aparece el escudo levantado
            Texture texturaActual =
                imagenParry != null ? imagenParry : bucketImage;

            batch.draw(
                texturaActual,
                bucket.x,
                bucket.y,
                bucket.width,
                bucket.height
            );

        } else if (herido) {

            batch.draw(
                bucketImage,
                bucket.x,
                bucket.y + MathUtils.random(-5, 5)
            );

            tiempoHerido--;

            if (tiempoHerido <= 0) {
                herido = false;
            }

        } else {

            // Postura normal
            batch.draw(
                bucketImage,
                bucket.x,
                bucket.y,
                bucket.width,
                bucket.height
            );
        }
    }

    public void dañar() {

        vidas = Math.max(0, vidas - 1);

        herido = true;
        tiempoHerido = tiempoHeridoMax;

        sonidoHerido.play();
    }

    public void sumarPuntos(int pp) {
        puntos += pp;
    }

    // Puntos dobles cuando el bloqueo ocurre durante el Parry
    public void sumarPuntosBloqueo(int puntosBase) {

        if (estaEnParry()) {
            sumarPuntos(puntosBase * 2);
        } else {
            sumarPuntos(puntosBase);
        }
    }

    public boolean estaEnParry() {
        return tiempoParry > 0f;
    }

    public boolean puedeHacerParry() {
        return tiempoParry <= 0f
            && tiempoRecargaParry <= 0f;
    }

    public float getTiempoRecargaParry() {
        return tiempoRecargaParry;
    }

    public boolean puedeHacerDash() {
        return tiempoDash <= 0f
            && tiempoRecargaDash <= 0f;
    }

    public boolean estaEnDash() {
        return tiempoDash > 0f;
    }

    public float getTiempoRecargaDash() {
        return tiempoRecargaDash;
    }

    public boolean estaHerido() {
        return herido;
    }

    public int getVidas() {
        return vidas;
    }

    public int getPuntos() {
        return puntos;
    }

    public Rectangle getArea() {
        return bucket;
    }

    public void destruir() {

        bucketImage.dispose();
        sonidoHerido.dispose();

        if (imagenParry != null) {
            imagenParry.dispose();
        }
    }
}
