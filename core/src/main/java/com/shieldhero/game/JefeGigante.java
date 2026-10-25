package com.shieldhero.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class JefeGigante {

    // =====================================
    // CONFIGURACION DEL JEFE
    // =====================================

    private static final float ANCHO = 125f;
    private static final float ALTO = 155f;

    // Aparece al exterior de la muralla.
    private static final float POSICION_X = 650f;
    private static final float POSICION_Y = 120f;

    // Intervalos de ataque.
    private static final float INTERVALO_FASE_1 = 2.4f;
    private static final float INTERVALO_FASE_2 = 1.8f;

    // Duracion de la animacion al atacar.
    private static final float DURACION_ATAQUE = 0.35f;

    // =====================================
    // ESTADO
    // =====================================

    private Texture textura;

    private Rectangle area;

    private int vidaMaxima;
    private int vidaActual;

    private int numeroJefe;

    private boolean activo;
    private boolean derrotado;

    private float temporizadorAtaque;
    private float tiempoAnimacionAtaque;

    private int ataquesRealizados;

    // =====================================
    // CONSTRUCTOR
    // =====================================

    public JefeGigante(
            Texture textura,
            int numeroJefe) {

        this.textura = textura;
        this.numeroJefe = Math.max(1, numeroJefe);

        area = new Rectangle(
            POSICION_X,
            POSICION_Y,
            ANCHO,
            ALTO
        );

        // Primer jefe: 12 vidas.
        // Segundo jefe: 16 vidas.
        // Tercer jefe: 20 vidas.
        vidaMaxima = 12 + (this.numeroJefe - 1) * 4;

        vidaActual = vidaMaxima;

        activo = false;
        derrotado = false;

        temporizadorAtaque = INTERVALO_FASE_1;
        tiempoAnimacionAtaque = 0f;

        ataquesRealizados = 0;
    }

    // =====================================
    // ACTIVAR JEFE
    // =====================================

    public void aparecer() {

        activo = true;
        derrotado = false;

        vidaActual = vidaMaxima;

        temporizadorAtaque = INTERVALO_FASE_1;
        tiempoAnimacionAtaque = 0f;

        ataquesRealizados = 0;

        System.out.println(
            "JEFE GIGANTE NUMERO "
            + numeroJefe
            + " HA APARECIDO"
        );
    }

    // =====================================
    // ACTUALIZAR JEFE
    // =====================================

    public void actualizar(float delta) {

        if (!activo || derrotado) {
            return;
        }

        temporizadorAtaque -= delta;

        tiempoAnimacionAtaque = Math.max(
            0f,
            tiempoAnimacionAtaque - delta
        );
    }

    // =====================================
    // CONSULTAR SI DEBE ATACAR
    // =====================================

    public boolean debeAtacar() {

        if (!activo || derrotado) {
            return false;
        }

        return temporizadorAtaque <= 0f;
    }

    // =====================================
    // REGISTRAR ATAQUE
    // =====================================

    public void registrarAtaque() {

        if (!activo || derrotado) {
            return;
        }

        ataquesRealizados++;

        tiempoAnimacionAtaque = DURACION_ATAQUE;

        // Elegir intervalo segun fase.
        if (estaEnFaseDos()) {

            temporizadorAtaque = INTERVALO_FASE_2;

        } else {

            temporizadorAtaque = INTERVALO_FASE_1;
        }
    }

    // =====================================
    // LANZAMIENTO DOBLE
    // =====================================

    public boolean debeLanzarDoble() {

        // En fase 2, cada tercer ataque
        // sera un lanzamiento doble.
        return estaEnFaseDos()
            && ataquesRealizados > 0
            && ataquesRealizados % 3 == 0;
    }

    // =====================================
    // RECIBIR DANO
    // =====================================

    public void recibirDanio(int cantidad) {

        if (!activo || derrotado) {
            return;
        }

        vidaActual = Math.max(
            0,
            vidaActual - Math.max(0, cantidad)
        );

        System.out.println(
            "GIGANTE: "
            + vidaActual
            + "/"
            + vidaMaxima
            + " VIDAS"
        );

        if (vidaActual <= 0) {
            derrotar();
        }
    }

    // =====================================
    // DERROTAR AL JEFE
    // =====================================

    private void derrotar() {

        derrotado = true;
        activo = false;

        temporizadorAtaque = 0f;
        tiempoAnimacionAtaque = 0f;

        System.out.println(
            "GIGANTE DEL ASEDIO DERROTADO"
        );
    }

    // =====================================
    // SEGUNDA FASE
    // =====================================

    public boolean estaEnFaseDos() {

        return vidaActual <= vidaMaxima / 2;
    }

    // =====================================
    // DIBUJAR JEFE
    // =====================================

    public void dibujar(SpriteBatch batch) {

        if (!activo || derrotado) {
            return;
        }

        float desplazamientoX = 0f;
        float desplazamientoY = 0f;

        // Retroceso al lanzar piedras.
        if (tiempoAnimacionAtaque > 0f) {

            float progreso =
                tiempoAnimacionAtaque
                / DURACION_ATAQUE;

            desplazamientoX = 7f * progreso;
        }

        // En fase 2, una vibracion leve
        // indica que el gigante esta furioso.
        if (estaEnFaseDos()) {

            desplazamientoY = MathUtils.sin(
                temporizadorAtaque * 15f
            ) * 2f;
        }

        batch.draw(
            textura,
            area.x + desplazamientoX,
            area.y + desplazamientoY,
            area.width,
            area.height
        );
    }

    // =====================================
    // POSICION DE LANZAMIENTO
    // =====================================

    public float getOrigenAtaqueX() {

        // Brazo del gigante orientado
        // hacia el interior del castillo.
        return area.x + 18f;
    }

    public float getOrigenAtaqueY() {

        return area.y + area.height - 32f;
    }

    // =====================================
    // CONSULTAS GENERALES
    // =====================================

    public boolean estaActivo() {
        return activo;
    }

    public boolean estaDerrotado() {
        return derrotado;
    }

    public int getVidaActual() {
        return vidaActual;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public int getNumeroJefe() {
        return numeroJefe;
    }

    public Rectangle getArea() {
        return area;
    }

    public float getPorcentajeVida() {

        if (vidaMaxima <= 0) {
            return 0f;
        }

        return (float) vidaActual / vidaMaxima;
    }

    // =====================================
    // LIMPIAR RECURSOS
    // =====================================

    public void destruir() {

        // La textura se administra desde
        // GameScreen, igual que los arqueros
        // y el canon.
        //
        // No hacemos textura.dispose()
        // aqui para evitar liberarla dos veces.
    }
}
