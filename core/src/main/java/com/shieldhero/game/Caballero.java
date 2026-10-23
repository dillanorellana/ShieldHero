
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
    private Sound sonidoHerido;

    private int vidas = 3;
    private int puntos = 0;
    private int velx = 400;

    private boolean herido = false;
    private int tiempoHeridoMax = 50;
    private int tiempoHerido;

    public Caballero(Texture tex, Sound ss) {
        bucketImage = tex;
        sonidoHerido = ss;
    }

    public void crear() {
        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;
    }

    public void actualizarMovimiento() {

        float delta = Gdx.graphics.getDeltaTime();

        // Mover a la izquierda: A o flecha izquierda
        if (Gdx.input.isKeyPressed(Input.Keys.A)
                || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucket.x -= velx * delta;
        }

        // Mover a la derecha: D o flecha derecha
        if (Gdx.input.isKeyPressed(Input.Keys.D)
                || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucket.x += velx * delta;
        }

        // Limites de la pantalla
        if (bucket.x < 0) {
            bucket.x = 0;
        }

        if (bucket.x > 800 - bucket.width) {
            bucket.x = 800 - bucket.width;
        }
    }

    public void dibujar(SpriteBatch batch) {
        if (!herido) {
            batch.draw(bucketImage, bucket.x, bucket.y);
        } else {
            batch.draw(bucketImage, bucket.x,
                    bucket.y + MathUtils.random(-5, 5));

            tiempoHerido--;

            if (tiempoHerido <= 0) {
                herido = false;
            }
        }
    }

    public void dañar() {
        vidas--;
        herido = true;
        tiempoHerido = tiempoHeridoMax;
        sonidoHerido.play();
    }

    public void sumarPuntos(int pp) {
        puntos += pp;
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
    }
}
