
package com.shieldhero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Lluvia {

    private Array<Rectangle> rainDropsPos;
    private Array<Integer> rainDropsType;

    private long lastDropTime;

    private Texture gotaBuena;
    private Texture gotaMala;

    private Sound dropSound;
    private Music rainMusic;

    public Lluvia(Texture gotaBuena, Texture gotaMala,
                  Sound ss, Music mm) {

        this.gotaBuena = gotaBuena;
        this.gotaMala = gotaMala;
        this.dropSound = ss;
        this.rainMusic = mm;
    }

    public void crear() {

        rainDropsPos = new Array<Rectangle>();
        rainDropsType = new Array<Integer>();

        crearGotaDeLluvia();

        rainMusic.setLooping(true);
        rainMusic.play();
    }

    private void crearGotaDeLluvia() {

        Rectangle proyectil = new Rectangle();

        proyectil.x = MathUtils.random(0, 800 - 64);
        proyectil.y = 480;
        proyectil.width = 64;
        proyectil.height = 64;

        rainDropsPos.add(proyectil);

        // Tipos provisionales de ataques
        if (MathUtils.randomBoolean()) {
            rainDropsType.add(1);
        } else {
            rainDropsType.add(2);
        }

        lastDropTime = TimeUtils.nanoTime();
    }

    public boolean actualizarMovimiento(Caballero caballero,
                                         Castillo castillo) {

        // Generar nuevos proyectiles
        if (TimeUtils.nanoTime() - lastDropTime > 300000000L) {
            crearGotaDeLluvia();
        }

        for (int i = 0; i < rainDropsPos.size; i++) {

            Rectangle proyectil = rainDropsPos.get(i);

            // Movimiento hacia abajo
            proyectil.y -= 300 * Gdx.graphics.getDeltaTime();

            // El caballero bloquea el proyectil
            if (proyectil.overlaps(caballero.getArea())) {

                caballero.sumarPuntos(10);
                dropSound.play();

                rainDropsPos.removeIndex(i);
                rainDropsType.removeIndex(i);

                i--;
                continue;
            }

            // El proyectil llega al castillo
            if (proyectil.y + proyectil.height < 0) {

                castillo.recibirDaño(1);

                rainDropsPos.removeIndex(i);
                rainDropsType.removeIndex(i);

                i--;
                continue;
            }
        }

        return !castillo.estaDestruido();
    }

    public void actualizarDibujoLluvia(SpriteBatch batch) {

        for (int i = 0; i < rainDropsPos.size; i++) {

            Rectangle proyectil = rainDropsPos.get(i);

            if (rainDropsType.get(i) == 1) {
                batch.draw(gotaMala, proyectil.x, proyectil.y);
            } else {
                batch.draw(gotaBuena, proyectil.x, proyectil.y);
            }
        }
    }

    public void pausar() {
        rainMusic.stop();
    }

    public void continuar() {
        rainMusic.play();
    }

    public void destruir() {
        gotaBuena.dispose();
        gotaMala.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }
}
