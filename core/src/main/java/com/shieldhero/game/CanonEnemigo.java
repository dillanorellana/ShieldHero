
package com.shieldhero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;

public class CanonEnemigo {

    private Texture textura;

    private float x;
    private float y;

    private float ancho = 90f;
    private float alto = 70f;

    // =====================================
    // ANIMACION DE RETROCESO
    // =====================================

    private static final float DURACION_DISPARO = 0.35f;
    private static final float RETROCESO_MAXIMO = 8f;

    private float tiempoDisparo = 0f;

    // =====================================
    // FOGONAZO
    // =====================================

    private static final float DURACION_FOGONAZO = 0.15f;

    private float tiempoFogonazo = 0f;

    // =====================================
    // HUMO
    // =====================================

    private static final float DURACION_HUMO = 0.85f;

    private float tiempoHumo = 0f;

    // =====================================
    // BOCA DEL CANON
    // =====================================

    // Coordenadas relativas al sprite.
    // El canon apunta arriba a la izquierda.
    private static final float BOCA_OFFSET_X = 4f;
    private static final float BOCA_OFFSET_Y = 56f;

    private ShapeRenderer shapeRenderer;

    public CanonEnemigo(
            Texture textura,
            float x,
            float y) {

        this.textura = textura;
        this.x = x;
        this.y = y;

        shapeRenderer = new ShapeRenderer();
    }

    // =====================================
    // ACTIVAR EFECTOS AL DISPARAR
    // =====================================

    public void disparar() {

        tiempoDisparo = DURACION_DISPARO;

        tiempoFogonazo = DURACION_FOGONAZO;

        tiempoHumo = DURACION_HUMO;
    }

    // =====================================
    // ACTUALIZAR ANIMACIONES
    // =====================================

    public void actualizar(float delta) {

        tiempoDisparo = Math.max(
            0f,
            tiempoDisparo - delta
        );

        tiempoFogonazo = Math.max(
            0f,
            tiempoFogonazo - delta
        );

        tiempoHumo = Math.max(
            0f,
            tiempoHumo - delta
        );
    }

    // =====================================
    // CALCULAR RETROCESO
    // =====================================

    private float obtenerRetroceso() {

        if (tiempoDisparo <= 0f) {
            return 0f;
        }

        float progreso =
            tiempoDisparo / DURACION_DISPARO;

        float intensidad = MathUtils.sin(
            progreso * MathUtils.PI
        );

        return RETROCESO_MAXIMO * intensidad;
    }

    // =====================================
    // DIBUJAR CANON
    // =====================================

    public void dibujar(SpriteBatch batch) {

        float retroceso = obtenerRetroceso();

        batch.draw(
            textura,
            x + retroceso,
            y,
            ancho,
            alto
        );
    }

    // =====================================
    // CONFIGURAR CAMARA
    // =====================================

    // Este metodo es el que faltaba.
    // Permite dibujar los efectos utilizando
    // la misma proyeccion de GameScreen.
    public void configurarCamara(Matrix4 matriz) {

        shapeRenderer.setProjectionMatrix(matriz);
    }

    // =====================================
    // DIBUJAR FOGONAZO Y HUMO
    // =====================================

    // IMPORTANTE:
    // Llamar despues de batch.end().
    public void dibujarEfectos() {

        // No dibujar nada si no hay efectos.
        if (tiempoFogonazo <= 0f
                && tiempoHumo <= 0f) {

            return;
        }

        // Posicion actual de la boca del canon.
        float bocaX =
            x + BOCA_OFFSET_X + obtenerRetroceso();

        float bocaY =
            y + BOCA_OFFSET_Y;

        // Habilitar transparencia para el humo.
        Gdx.gl.glEnable(GL20.GL_BLEND);

        Gdx.gl.glBlendFunc(
            GL20.GL_SRC_ALPHA,
            GL20.GL_ONE_MINUS_SRC_ALPHA
        );

        shapeRenderer.begin(
            ShapeRenderer.ShapeType.Filled
        );

        // =====================================
        // FOGONAZO NARANJA Y AMARILLO
        // =====================================

        if (tiempoFogonazo > 0f) {

            float intensidad =
                tiempoFogonazo / DURACION_FOGONAZO;

            float radio =
                8f + 14f * intensidad;

            // Resplandor naranja exterior.
            shapeRenderer.setColor(
                1f,
                0.35f,
                0.05f,
                intensidad
            );

            shapeRenderer.circle(
                bocaX - 7f,
                bocaY + 4f,
                radio
            );

            // Centro amarillo brillante.
            shapeRenderer.setColor(
                1f,
                0.85f,
                0.15f,
                intensidad
            );

            shapeRenderer.circle(
                bocaX - 11f,
                bocaY + 7f,
                radio * 0.55f
            );

            // Chispa adicional hacia la izquierda.
            shapeRenderer.setColor(
                1f,
                0.95f,
                0.45f,
                intensidad
            );

            shapeRenderer.triangle(
                bocaX - 5f,
                bocaY + 1f,

                bocaX - 30f,
                bocaY + 17f,

                bocaX - 13f,
                bocaY + 13f
            );
        }

        // =====================================
        // NUBE DE HUMO
        // =====================================

        if (tiempoHumo > 0f) {

            float progreso =
                1f - tiempoHumo / DURACION_HUMO;

            float transparencia =
                0.75f * (1f - progreso);

            float desplazamientoX =
                progreso * 20f;

            float desplazamientoY =
                progreso * 25f;

            float radio =
                6f + progreso * 13f;

            // Nube principal gris oscura.
            shapeRenderer.setColor(
                0.35f,
                0.35f,
                0.35f,
                transparencia
            );

            shapeRenderer.circle(
                bocaX - desplazamientoX,
                bocaY + desplazamientoY,
                radio
            );

            // Segunda nube gris clara.
            shapeRenderer.setColor(
                0.65f,
                0.65f,
                0.65f,
                transparencia * 0.75f
            );

            shapeRenderer.circle(
                bocaX - desplazamientoX - 10f,
                bocaY + desplazamientoY + 7f,
                radio * 0.7f
            );

            // Tercera nube pequeña.
            shapeRenderer.setColor(
                0.45f,
                0.45f,
                0.45f,
                transparencia * 0.65f
            );

            shapeRenderer.circle(
                bocaX - desplazamientoX + 6f,
                bocaY + desplazamientoY + 12f,
                radio * 0.5f
            );
        }

        shapeRenderer.end();

        // Restaurar el estado de OpenGL.
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    // =====================================
    // METODOS DE CONSULTA
    // =====================================

    public boolean estaDisparando() {

        return tiempoDisparo > 0f;
    }

    public float getX() {

        return x;
    }

    public float getY() {

        return y;
    }

    public float getCentroX() {

        return x + ancho / 2f;
    }

    public float getCentroY() {

        return y + alto / 2f;
    }

    public float getAncho() {

        return ancho;
    }

    public float getAlto() {

        return alto;
    }

    public void setPosicion(
            float x,
            float y) {

        this.x = x;
        this.y = y;
    }

    // =====================================
    // LIBERAR RECURSOS
    // =====================================

    public void destruir() {

        shapeRenderer.dispose();

        // La textura del canon se libera
        // desde GameScreen.
    }
}
