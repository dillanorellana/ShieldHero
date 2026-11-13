
package com.shieldhero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Caballero {

    private static final float ANCHO = 32f;
    private static final float ALTO = 40f;
    private static final float SUELO = 120f;
    private static final float LIMITE_IZQUIERDO = 30f;
    private static final float LIMITE_DERECHO = 415f;
    private static final float INICIO_X = 250f;

    private final HabilidadesCaballero habilidades =
        new HabilidadesCaballero();

    private Rectangle bucket;

    private Texture bucketImage;
    private Texture imagenParry;
    private Sound sonidoHerido;

    private int vidas;
    private int puntos;
    private int velx = 400;

    private boolean herido;
    private int tiempoHeridoMax = 50;
    private int tiempoHerido;

    public Caballero(Texture tex, Sound ss) {
        bucketImage = tex;
        sonidoHerido = ss;
    }

    public void setImagenParry(Texture imagenParry) {
        this.imagenParry = imagenParry;
    }

    public void crear() {

        bucket = new Rectangle(
            INICIO_X,
            SUELO,
            ANCHO,
            ALTO
        );

        vidas = 3;
        puntos = 0;

        herido = false;
        tiempoHerido = 0;

        habilidades.crear();
    }

    // MOVIMIENTO

    public void actualizarMovimiento() {

        float delta = Gdx.graphics.getDeltaTime();

        habilidades.actualizarTemporizadores(delta);

        // PARRY
        habilidades.procesarParry(
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE),
            delta
        );

        // DIRECCION
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

        // DASH
        boolean shift =
            Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)
            || Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT);

        habilidades.iniciarDash(shift, direccion);

        bucket.x += habilidades.calcularDesplazamientoX(
            direccion,
            velx,
            delta
        );

        bucket.x = MathUtils.clamp(
            bucket.x,
            LIMITE_IZQUIERDO,
            LIMITE_DERECHO - bucket.width
        );

        bucket.y = SUELO;
    }

    // DELEGACION DE HABILIDADES

    public void aplicarRalentizacion() {
        habilidades.aplicarRalentizacion();
    }

    public boolean estaRalentizado() {
        return habilidades.estaRalentizado();
    }

    public float getTiempoLentitud() {
        return habilidades.getTiempoLentitud();
    }

    public void mejorarParry() {
        habilidades.mejorarParry();
    }

    public void mejorarDash() {
        habilidades.mejorarDash();
    }

    public void activarParrySupremo() {
        habilidades.activarParrySupremo();
    }

    public void activarDashSupremo() {
        habilidades.activarDashSupremo();
    }

    public void activarFuriaPuntos() {
        habilidades.activarFuriaPuntos();
    }

    public boolean tieneParryMejorado() {
        return habilidades.tieneParryMejorado();
    }

    public boolean tieneDashMejorado() {
        return habilidades.tieneDashMejorado();
    }

    public boolean tieneParrySupremo() {
        return habilidades.tieneParrySupremo();
    }

    public boolean tieneDashSupremo() {
        return habilidades.tieneDashSupremo();
    }

    public boolean tieneFuriaPuntos() {
        return habilidades.tieneFuriaPuntos();
    }

    public boolean parryAlMaximo() {
        return habilidades.parryAlMaximo();
    }

    public boolean dashAlMaximo() {
        return habilidades.dashAlMaximo();
    }

    public int getNivelParry() {
        return habilidades.getNivelParry();
    }

    public int getNivelDash() {
        return habilidades.getNivelDash();
    }

    public float getDuracionParryActual() {
        return habilidades.getDuracionParryActual();
    }

    public float getRecargaDashActual() {
        return habilidades.getRecargaDashActual();
    }

    public float getTiempoFuriaPuntos() {
        return habilidades.getTiempoFuriaPuntos();
    }

    public boolean estaEnParry() {
        return habilidades.estaEnParry();
    }

    public boolean puedeHacerParry() {
        return habilidades.puedeHacerParry();
    }

    public float getTiempoRecargaParry() {
        return habilidades.getTiempoRecargaParry();
    }

    public boolean puedeHacerDash() {
        return habilidades.puedeHacerDash();
    }

    public boolean estaEnDash() {
        return habilidades.estaEnDash();
    }

    public float getTiempoRecargaDash() {
        return habilidades.getTiempoRecargaDash();
    }

    // DIBUJADO

    public void dibujar(SpriteBatch batch) {

        Texture actual = bucketImage;

        if (estaEnParry() && imagenParry != null) {
            actual = imagenParry;
        }

        float desplazamientoY = 0f;

        if (herido) {

            desplazamientoY = MathUtils.random(-3f, 3f);
            tiempoHerido--;

            if (tiempoHerido <= 0) {
                herido = false;
            }
        }

        batch.draw(
            actual,
            bucket.x,
            bucket.y + desplazamientoY,
            bucket.width,
            bucket.height
        );
    }

    // VIDA Y PUNTUACION

    public void dañar() {

        vidas = Math.max(0, vidas - 1);

        herido = true;
        tiempoHerido = tiempoHeridoMax;

        sonidoHerido.play();
    }

    public void recuperarVida(int cantidad) {

        vidas = Math.min(
            3,
            vidas + Math.max(0, cantidad)
        );
    }

    public void sumarPuntos(int cantidad) {

        if (tieneFuriaPuntos()) {
            cantidad *= 2;
        }

        puntos += cantidad;
    }

    public void sumarPuntosBloqueo(int puntosBase) {

        if (estaEnParry()) {
            sumarPuntos(puntosBase * 2);
        } else {
            sumarPuntos(puntosBase);
        }
    }

    // CONSULTAS DEL PERSONAJE

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

    // LIBERAR RECURSOS

    public void destruir() {

        bucketImage.dispose();
        sonidoHerido.dispose();

        if (imagenParry != null) {
            imagenParry.dispose();
        }
    }
}
