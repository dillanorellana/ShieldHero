
package com.shieldhero.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class GuardianExtra {

    // =====================================
    // CONFIGURACION
    // =====================================

    private static final float ANCHO = 32f;
    private static final float ALTO = 40f;

    // Limites del patio del castillo.
    private static final float LIMITE_IZQUIERDO = 30f;
    private static final float LIMITE_DERECHO = 415f;

    private static final float POSICION_SUELO = 120f;

    // Valores iniciales del aura.
    private static final float RADIO_INICIAL = 55f;
    private static final float AUMENTO_RADIO = 20f;
    private static final float RADIO_MAXIMO = 135f;

    // =====================================
    // RECURSOS
    // =====================================

    private final Texture texturaGuardian;

    // Pixel blanco compartido, usado para
    // dibujar el aura de proteccion.
    private final Texture texturaPixel;

    // =====================================
    // ESTADO DEL GUARDIAN
    // =====================================

    private final Rectangle area;

    private boolean colocado;
    private int nivelAura;
    private float radioAura;

    // Indicador visual del ultimo bloqueo.
    private float tiempoEfectoBloqueo;

    private static final float DURACION_EFECTO = 0.20f;

    // =====================================
    // CONSTRUCTOR
    // =====================================

    public GuardianExtra(
            Texture texturaGuardian,
            Texture texturaPixel) {

        this.texturaGuardian = texturaGuardian;
        this.texturaPixel = texturaPixel;

        area = new Rectangle(
            LIMITE_IZQUIERDO,
            POSICION_SUELO,
            ANCHO,
            ALTO
        );

        colocado = false;

        nivelAura = 0;
        radioAura = 0f;

        tiempoEfectoBloqueo = 0f;
    }

    // =====================================
    // COLOCAR GUARDIAN
    // =====================================

    public void colocar(float posicionX) {

        // El guardian solo se coloca una vez.
        // Una vez elegido el lugar, es permanente.
        if (colocado) {
            return;
        }

        float x = MathUtils.clamp(
            posicionX - ANCHO / 2f,
            LIMITE_IZQUIERDO,
            LIMITE_DERECHO - ANCHO
        );

        area.setPosition(
            x,
            POSICION_SUELO
        );

        colocado = true;

        nivelAura = 1;
        radioAura = RADIO_INICIAL;

        System.out.println(
            "GUARDIAN EXTRA COLOCADO EN X = " + x
        );

        System.out.println(
            "AURA NIVEL 1 - RADIO " + radioAura
        );
    }

    // =====================================
    // MEJORAR AURA
    // =====================================

    public void mejorarAura() {

        if (!colocado) {
            return;
        }

        if (auraAlMaximo()) {

            System.out.println(
                "EL AURA DEL GUARDIAN YA ESTA AL MAXIMO"
            );

            return;
        }

        nivelAura++;

        radioAura = Math.min(
            RADIO_MAXIMO,
            RADIO_INICIAL
                + (nivelAura - 1) * AUMENTO_RADIO
        );

        System.out.println(
            "AURA DEL GUARDIAN MEJORADA"
        );

        System.out.println(
            "NIVEL: " + nivelAura
                + " | RADIO: " + radioAura
        );
    }

    // =====================================
    // CONSULTAR PROTECCION
    // =====================================

    public boolean protege(Rectangle proyectil) {

        if (!colocado || proyectil == null) {
            return false;
        }

        // Centro del aura.
        float centroX = getCentroAuraX();
        float centroY = getCentroAuraY();

        // Punto del rectangulo mas cercano
        // al centro del aura.
        float puntoX = MathUtils.clamp(
            centroX,
            proyectil.x,
            proyectil.x + proyectil.width
        );

        float puntoY = MathUtils.clamp(
            centroY,
            proyectil.y,
            proyectil.y + proyectil.height
        );

        float distanciaX = centroX - puntoX;
        float distanciaY = centroY - puntoY;

        float distanciaCuadrada =
            distanciaX * distanciaX
            + distanciaY * distanciaY;

        return distanciaCuadrada
            <= radioAura * radioAura;
    }

    // =====================================
    // REGISTRAR BLOQUEO
    // =====================================

    public void registrarBloqueo() {

        if (!colocado) {
            return;
        }

        tiempoEfectoBloqueo = DURACION_EFECTO;
    }

    // =====================================
    // ACTUALIZAR
    // =====================================

    public void actualizar(float delta) {

        if (!colocado) {
            return;
        }

        tiempoEfectoBloqueo = Math.max(
            0f,
            tiempoEfectoBloqueo - Math.max(0f, delta)
        );
    }

    // =====================================
    // DIBUJAR AURA
    // =====================================

    public void dibujarAura(SpriteBatch batch) {

        if (!colocado) {
            return;
        }

        Color colorAnterior =
            new Color(batch.getColor());

        float centroX = getCentroAuraX();
        float centroY = getCentroAuraY();

        // Aproximamos el circulo mediante
        // franjas horizontales transparentes.
        int franjas = 48;

        float diametro = radioAura * 2f;
        float altoFranja = diametro / franjas;

        if (tiempoEfectoBloqueo > 0f) {

            batch.setColor(
                0.45f, 0.95f, 1f, 0.32f
            );

        } else {

            batch.setColor(
                0.20f, 0.75f, 1f, 0.13f
            );
        }

        for (int i = 0; i < franjas; i++) {

            float desplazamientoY =
                -radioAura
                + (i + 0.5f) * altoFranja;

            float anchoMedio = (float) Math.sqrt(
                Math.max(
                    0f,
                    radioAura * radioAura
                        - desplazamientoY * desplazamientoY
                )
            );

            batch.draw(
                texturaPixel,
                centroX - anchoMedio,
                centroY + desplazamientoY
                    - altoFranja / 2f,
                anchoMedio * 2f,
                altoFranja + 0.5f
            );
        }

        batch.setColor(colorAnterior);
    }

    // =====================================
    // DIBUJAR GUARDIAN
    // =====================================

    public void dibujar(SpriteBatch batch) {

        if (!colocado) {
            return;
        }

        dibujarAura(batch);

        Color colorAnterior =
            new Color(batch.getColor());

        batch.setColor(Color.WHITE);

        batch.draw(
            texturaGuardian,
            area.x,
            area.y,
            area.width,
            area.height
        );

        batch.setColor(colorAnterior);
    }

    // =====================================
    // CENTRO DE PROTECCION
    // =====================================

    public float getCentroAuraX() {

        return area.x + area.width / 2f;
    }

    public float getCentroAuraY() {

        return area.y + area.height / 2f;
    }

    // =====================================
    // CONSULTAS GENERALES
    // =====================================

    public boolean estaColocado() {
        return colocado;
    }

    public boolean tieneVidaInfinita() {
        return true;
    }

    public boolean auraAlMaximo() {

        return colocado
            && radioAura >= RADIO_MAXIMO;
    }

    public int getNivelAura() {
        return nivelAura;
    }

    public float getRadioAura() {
        return radioAura;
    }

    public float getPosicionX() {
        return area.x;
    }

    public float getPosicionY() {
        return area.y;
    }

    public Rectangle getArea() {
        return area;
    }

    // =====================================
    // LIBERAR RECURSOS
    // =====================================

    public void destruir() {

        // Las texturas se reciben desde
        // GameScreen y pueden ser compartidas.
        //
        // GameScreen se encarga de liberarlas.
    }
}
