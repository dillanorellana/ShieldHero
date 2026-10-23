
package com.shieldhero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Lluvia {

    private Array<Proyectil> proyectiles;
    private long ultimoProyectil;

    private Texture texturaFlecha;
    private Texture texturaPiedra;
    private Texture texturaFuego;

    private Sound sonidoBloqueo;
    private Music musica;

    public Lluvia(Texture flecha, Texture piedra,
                  Texture fuego, Sound sonido, Music musica) {

        this.texturaFlecha = flecha;
        this.texturaPiedra = piedra;
        this.texturaFuego = fuego;
        this.sonidoBloqueo = sonido;
        this.musica = musica;
    }

    public void crear() {
        proyectiles = new Array<Proyectil>();
        crearProyectil();

        musica.setLooping(true);
        musica.play();
    }

    private void crearProyectil() {

        float x = MathUtils.random(0, 800 - 64);
        float y = 480;

        int tipo = MathUtils.random(1, 3);
        Proyectil nuevo;

        switch (tipo) {
            case 1:
                nuevo = new Flecha(x, y, texturaFlecha);
                break;

            case 2:
                nuevo = new Piedra(x, y, texturaPiedra);
                break;

            default:
                nuevo = new BolaFuego(x, y, texturaFuego);
                break;
        }

        proyectiles.add(nuevo);
        ultimoProyectil = TimeUtils.nanoTime();
    }

    public boolean actualizarMovimiento(Caballero caballero,
                                         Castillo castillo) {

        if (TimeUtils.nanoTime() - ultimoProyectil > 800000000L) {
            crearProyectil();
        }

        float delta = Gdx.graphics.getDeltaTime();

        for (int i = 0; i < proyectiles.size; i++) {

            Proyectil proyectil = proyectiles.get(i);
            proyectil.actualizar(delta);

            // Bloqueo utilizando la interfaz
            if (proyectil.colisionaCon(caballero.getArea())) {

                if (proyectil instanceof Bloqueable) {
                    Bloqueable bloqueable = (Bloqueable) proyectil;
                    bloqueable.alSerBloqueado(caballero);
                }

                sonidoBloqueo.play();
                proyectiles.removeIndex(i);
                i--;
                continue;
            }

            // Daño al castillo
            if (proyectil.llegoAlSuelo()) {

                castillo.recibirDaño(proyectil.getDaño());

                proyectiles.removeIndex(i);
                i--;
            }
        }

        return !castillo.estaDestruido();
    }

    public void actualizarDibujoLluvia(SpriteBatch batch) {
        for (Proyectil proyectil : proyectiles) {
            proyectil.dibujar(batch);
        }
    }

    public void pausar() {
        musica.pause();
    }

    public void continuar() {
        musica.play();
    }

    public void destruir() {
        texturaFlecha.dispose();
        texturaPiedra.dispose();
        texturaFuego.dispose();
        sonidoBloqueo.dispose();
        musica.dispose();
    }
}
