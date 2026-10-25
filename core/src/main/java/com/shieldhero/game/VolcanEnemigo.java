package com.shieldhero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;

public class VolcanEnemigo {

    private float x;
    private float y;

    // Punto desde donde "sale" la bola de fuego
    private float craterOffsetX = 0f;
    private float craterOffsetY = 0f;

    // Efecto de erupción
    private static final float DURACION_ERUPCION = 0.45f;
    private float tiempoErupcion = 0f;

    private ShapeRenderer shapeRenderer;

    public VolcanEnemigo(float x, float y) {
        this.x = x;
        this.y = y;
        shapeRenderer = new ShapeRenderer();
    }

    public void erupcionar() {
        tiempoErupcion = DURACION_ERUPCION;
    }

    public void actualizar(float delta) {
        tiempoErupcion = Math.max(0f, tiempoErupcion - delta);
    }

    public void configurarCamara(Matrix4 matriz) {
        shapeRenderer.setProjectionMatrix(matriz);
    }

    public void dibujar(SpriteBatch batch) {
        // El volcán ya está en el fondo.
        // No dibujamos sprite adicional aquí.
    }

    public void dibujarEfectos() {

        if (tiempoErupcion <= 0f) {
            return;
        }

        float intensidad = tiempoErupcion / DURACION_ERUPCION;

        float bocaX = getCraterX();
        float bocaY = getCraterY();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(
            GL20.GL_SRC_ALPHA,
            GL20.GL_ONE_MINUS_SRC_ALPHA
        );

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Resplandor rojo exterior
        shapeRenderer.setColor(
            1f,
            0.18f,
            0.08f,
            0.50f * intensidad
        );
        shapeRenderer.circle(
            bocaX,
            bocaY,
            24f + 18f * intensidad
        );

        // Centro naranja
        shapeRenderer.setColor(
            1f,
            0.45f,
            0.10f,
            0.75f * intensidad
        );
        shapeRenderer.circle(
            bocaX,
            bocaY + 2f,
            16f + 12f * intensidad
        );

        // Núcleo amarillo
        shapeRenderer.setColor(
            1f,
            0.85f,
            0.20f,
            0.90f * intensidad
        );
        shapeRenderer.circle(
            bocaX,
            bocaY + 5f,
            9f + 7f * intensidad
        );

        // Chispas / lava expulsada
        shapeRenderer.setColor(
            1f,
            0.55f,
            0.12f,
            0.75f * intensidad
        );
        shapeRenderer.triangle(
            bocaX - 5f, bocaY + 8f,
            bocaX - 20f, bocaY + 28f,
            bocaX - 1f, bocaY + 18f
        );

        shapeRenderer.triangle(
            bocaX + 4f, bocaY + 8f,
            bocaX + 16f, bocaY + 24f,
            bocaX + 10f, bocaY + 14f
        );

        // Humo
        float progreso = 1f - intensidad;
        float humoY = bocaY + 12f + progreso * 26f;

        shapeRenderer.setColor(
            0.25f,
            0.25f,
            0.25f,
            0.35f * intensidad
        );
        shapeRenderer.circle(
            bocaX - 10f,
            humoY,
            10f + progreso * 10f
        );

        shapeRenderer.setColor(
            0.40f,
            0.40f,
            0.40f,
            0.25f * intensidad
        );
        shapeRenderer.circle(
            bocaX + 6f,
            humoY + 8f,
            8f + progreso * 8f
        );

        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public float getCraterX() {
        return x + craterOffsetX;
    }

    public float getCraterY() {
        return y + craterOffsetY;
    }

    public void setCraterOffset(float craterOffsetX, float craterOffsetY) {
        this.craterOffsetX = craterOffsetX;
        this.craterOffsetY = craterOffsetY;
    }

    public void destruir() {
        shapeRenderer.dispose();
    }
}