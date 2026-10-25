package com.shieldhero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Caballero {

    // DIMENSIONES DEL NUEVO ESCENARIO
    private static final float ANCHO_CABALLERO = 32f;
    private static final float ALTO_CABALLERO = 40f;

    // Posicion del suelo del patio.
    private static final float SUELO_PATIO = 120f;

    // Limites horizontales del patio defendido.
    private static final float LIMITE_IZQUIERDO = 30f;
    private static final float LIMITE_DERECHO = 415f;

    // Posicion inicial del caballero.
    private static final float POSICION_INICIAL_X = 250f;

    private Rectangle bucket;

    private Texture bucketImage;
    private Texture imagenParry;

    private Sound sonidoHerido;

    private int vidas = 3;
    private int puntos = 0;

    // Velocidad de movimiento normal.
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

    // Asignar sprite del parry.
    public void setImagenParry(Texture imagenParry) {
        this.imagenParry = imagenParry;
    }

    public void crear() {

        bucket = new Rectangle();

        // Posicion y dimensiones del nuevo escenario.
        bucket.set(
            POSICION_INICIAL_X,
            SUELO_PATIO,
            ANCHO_CABALLERO,
            ALTO_CABALLERO
        );

        tiempoDash = 0f;
        tiempoRecargaDash = 0f;
        direccionDash = 0;

        tiempoParry = 0f;
        tiempoRecargaParry = 0f;

        herido = false;
        tiempoHerido = 0;

        vidas = 3;
        puntos = 0;
    }

    public void actualizarMovimiento() {

        float delta = Gdx.graphics.getDeltaTime();

        // Actualizar tiempos de recarga.
        tiempoRecargaDash = Math.max(
            0f,
            tiempoRecargaDash - delta
        );

        tiempoRecargaParry = Math.max(
            0f,
            tiempoRecargaParry - delta
        );

        // PARRY: barra espaciadora.
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

        // Movimiento izquierda/derecha.
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

        // DASH: Shift + direccion.
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

        // Ejecutar dash o movimiento normal.
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

        // Limitar desplazamiento al patio.
        // Se aplica tambien al dash.
        bucket.x = MathUtils.clamp(
            bucket.x,
            LIMITE_IZQUIERDO,
            LIMITE_DERECHO - bucket.width
        );

        // Mantener al caballero sobre el suelo.
        bucket.y = SUELO_PATIO;
    }

    public void dibujar(SpriteBatch batch) {

        // Elegir la textura segun el estado.
        Texture texturaActual = bucketImage;

        if (estaEnParry() && imagenParry != null) {
            texturaActual = imagenParry;
        }

        float desplazamientoY = 0f;

        // Efecto visual de daño.
        if (herido) {

            desplazamientoY = MathUtils.random(-3f, 3f);

            tiempoHerido--;

            if (tiempoHerido <= 0) {
                herido = false;
            }
        }

        // Siempre dibujar con las mismas dimensiones.
        batch.draw(
            texturaActual,
            bucket.x,
            bucket.y + desplazamientoY,
            bucket.width,
            bucket.height
        );
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

    // Puntos dobles durante el parry.
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
