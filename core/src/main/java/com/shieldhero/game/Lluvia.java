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

    // GM-9: La dificultad se obtiene de la ronda
    // configurada mediante el patron Builder.
    private float obtenerIntervaloAparicion() {

        return GestorPartida.getInstancia()
                .getIntervaloAparicionActual();
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

        // GM-7: Strategy
        int ronda = GestorPartida.getInstancia().getRondaActual();

        if (ronda == 5 && MathUtils.randomBoolean(0.25f)) {

            float direccion =
                    MathUtils.randomBoolean() ? 1f : -1f;

            nuevo.setEstrategiaMovimiento(
                new MovimientoDiagonal(direccion)
            );

        } else {

            nuevo.setEstrategiaMovimiento(
                new MovimientoVertical()
            );
        }

        proyectiles.add(nuevo);
        ultimoProyectil = TimeUtils.nanoTime();
    }

    public boolean actualizarMovimiento(Caballero caballero,
                                         Castillo castillo) {

        // Intervalo definido por ConfiguracionRonda.Builder
        float intervalo = obtenerIntervaloAparicion();

        long intervaloNanos = (long) (intervalo * 1000000000L);

        if (TimeUtils.nanoTime() - ultimoProyectil > intervaloNanos) {
            crearProyectil();
        }

        float delta = Gdx.graphics.getDeltaTime();

        for (int i = 0; i < proyectiles.size; i++) {

            Proyectil proyectil = proyectiles.get(i);

            // GM-8: Template Method
            boolean eliminar = proyectil.procesar(
                delta,
                caballero,
                castillo
            );

            if (eliminar) {

                // Reproducir sonido solamente al bloquear.
                if (proyectil.colisionaCon(caballero.getArea())) {
                    sonidoBloqueo.play();
                }

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
