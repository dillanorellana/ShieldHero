
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

    private static final float DURACION_DASH = 0.15f;
    private static final float DISTANCIA_DASH = 150f;

    private static final float RECARGA_DASH_BASE = 1.5f;
    private static final float RECARGA_DASH_MINIMA = 0.5f;

    private static final float DURACION_PARRY_BASE = 0.20f;
    private static final float DURACION_PARRY_MAXIMA = 0.50f;

    private static final float RECARGA_PARRY = 1f;
    private static final float DURACION_FURIA = 45f;

    private static final float DURACION_LENTITUD = 1.5f;
    private static final float MULTIPLICADOR_LENTITUD = 0.70f;

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

    private float tiempoDash;
    private float tiempoRecargaDash;
    private int direccionDash;

    private float tiempoParry;
    private float tiempoRecargaParry;

    private float recargaDashActual;
    private float duracionParryActual;

    private int nivelesParry;
    private int nivelesDash;

    private boolean parrySupremo;
    private boolean dashSupremo;

    private float tiempoFuria;
    private float tiempoLentitud;

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

        tiempoDash = 0f;
        tiempoRecargaDash = 0f;
        direccionDash = 0;

        tiempoParry = 0f;
        tiempoRecargaParry = 0f;

        recargaDashActual = RECARGA_DASH_BASE;
        duracionParryActual = DURACION_PARRY_BASE;

        nivelesParry = 0;
        nivelesDash = 0;

        parrySupremo = false;
        dashSupremo = false;

        tiempoFuria = 0f;
        tiempoLentitud = 0f;
    }

    // =====================================
    // MOVIMIENTO
    // =====================================

    public void actualizarMovimiento() {

        float delta = Gdx.graphics.getDeltaTime();

        tiempoRecargaDash = Math.max(
            0f,
            tiempoRecargaDash - delta
        );

        tiempoRecargaParry = Math.max(
            0f,
            tiempoRecargaParry - delta
        );

        tiempoFuria = Math.max(
            0f,
            tiempoFuria - delta
        );

        tiempoLentitud = Math.max(
            0f,
            tiempoLentitud - delta
        );

        // PARRY
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                && tiempoRecargaParry <= 0f
                && tiempoParry <= 0f) {

            tiempoParry = duracionParryActual;
            tiempoRecargaParry = RECARGA_PARRY;

        } else {

            tiempoParry = Math.max(
                0f,
                tiempoParry - delta
            );
        }

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

        if (shift
                && direccion != 0
                && tiempoRecargaDash <= 0f
                && tiempoDash <= 0f) {

            direccionDash = direccion;
            tiempoDash = DURACION_DASH;
            tiempoRecargaDash = recargaDashActual;
        }

        float multiplicador = estaRalentizado()
            ? MULTIPLICADOR_LENTITUD : 1f;

        if (tiempoDash > 0f) {

            float tiempoAplicado = Math.min(
                delta,
                tiempoDash
            );

            float velocidadDash =
                DISTANCIA_DASH / DURACION_DASH;

            bucket.x += direccionDash
                * velocidadDash
                * tiempoAplicado
                * multiplicador;

            tiempoDash = Math.max(
                0f,
                tiempoDash - delta
            );

        } else {

            bucket.x += direccion
                * velx
                * delta
                * multiplicador;
        }

        bucket.x = MathUtils.clamp(
            bucket.x,
            LIMITE_IZQUIERDO,
            LIMITE_DERECHO - bucket.width
        );

        bucket.y = SUELO;
    }

    // =====================================
    // RALENTIZACION DEL JEFE
    // =====================================

    public void aplicarRalentizacion() {

        tiempoLentitud = Math.max(
            tiempoLentitud,
            DURACION_LENTITUD
        );

        System.out.println(
            "CABALLERO RALENTIZADO POR EL GIGANTE"
        );
    }

    public boolean estaRalentizado() {
        return tiempoLentitud > 0f;
    }

    public float getTiempoLentitud() {
        return tiempoLentitud;
    }

    // =====================================
    // MEJORAS NORMALES
    // =====================================

    public void mejorarParry() {

        if (parryAlMaximo()) {
            return;
        }

        duracionParryActual = Math.min(
            DURACION_PARRY_MAXIMA,
            duracionParryActual + 0.10f
        );

        nivelesParry++;
    }

    public void mejorarDash() {

        if (dashAlMaximo()) {
            return;
        }

        recargaDashActual = Math.max(
            RECARGA_DASH_MINIMA,
            recargaDashActual - 0.25f
        );

        nivelesDash++;

        tiempoRecargaDash = Math.min(
            tiempoRecargaDash,
            recargaDashActual
        );
    }

    // =====================================
    // MEJORAS ESPECIALES
    // =====================================

    public void activarParrySupremo() {

        parrySupremo = true;

        duracionParryActual = DURACION_PARRY_MAXIMA;
        nivelesParry = Math.max(nivelesParry, 3);
    }

    public void activarDashSupremo() {

        dashSupremo = true;

        recargaDashActual = RECARGA_DASH_MINIMA;
        nivelesDash = Math.max(nivelesDash, 4);

        tiempoRecargaDash = Math.min(
            tiempoRecargaDash,
            recargaDashActual
        );
    }

    public void activarFuriaPuntos() {
        tiempoFuria = DURACION_FURIA;
    }

    public boolean tieneParryMejorado() {
        return nivelesParry > 0;
    }

    public boolean tieneDashMejorado() {
        return nivelesDash > 0;
    }

    public boolean tieneParrySupremo() {
        return parrySupremo;
    }

    public boolean tieneDashSupremo() {
        return dashSupremo;
    }

    public boolean tieneFuriaPuntos() {
        return tiempoFuria > 0f;
    }

    public boolean parryAlMaximo() {
        return duracionParryActual >= DURACION_PARRY_MAXIMA;
    }

    public boolean dashAlMaximo() {
        return recargaDashActual <= RECARGA_DASH_MINIMA;
    }

    public int getNivelParry() {
        return nivelesParry;
    }

    public int getNivelDash() {
        return nivelesDash;
    }

    public float getDuracionParryActual() {
        return duracionParryActual;
    }

    public float getRecargaDashActual() {
        return recargaDashActual;
    }

    public float getTiempoFuriaPuntos() {
        return tiempoFuria;
    }

    // =====================================
    // DIBUJADO
    // =====================================

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

    // =====================================
    // DANO Y PUNTOS
    // =====================================

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

    // =====================================
    // CONSULTAS
    // =====================================

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
