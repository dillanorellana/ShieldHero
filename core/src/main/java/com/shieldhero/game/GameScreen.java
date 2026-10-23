
package com.shieldhero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {

    private final ShieldHeroGame game;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private BitmapFont font;

    private Caballero caballero;
    private Castillo castillo;
    private Lluvia lluvia;

    private GestorPartida gestorPartida;

    public GameScreen(final ShieldHeroGame game) {

        this.game = game;

        batch = game.getBatch();
        font = game.getFont();

        // Singleton
        gestorPartida = GestorPartida.getInstancia();
        gestorPartida.reiniciarRondas();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // Castillo
        castillo = new Castillo();

        // Caballero
        Texture texturaCaballero = new Texture(
            Gdx.files.internal("bucket.png")
        );

        Sound sonidoHerido = Gdx.audio.newSound(
            Gdx.files.internal("hurt.ogg")
        );

        caballero = new Caballero(
            texturaCaballero,
            sonidoHerido
        );

        caballero.crear();

        // Proyectiles
        Texture texturaFlecha = new Texture(
            Gdx.files.internal("drop.png")
        );

        Texture texturaPiedra = new Texture(
            Gdx.files.internal("dropBad.png")
        );

        Texture texturaFuego = new Texture(
            Gdx.files.internal("drop.png")
        );

        Sound sonidoBloqueo = Gdx.audio.newSound(
            Gdx.files.internal("drop.wav")
        );

        Music musica = Gdx.audio.newMusic(
            Gdx.files.internal("rain.mp3")
        );

        lluvia = new Lluvia(
            texturaFlecha,
            texturaPiedra,
            texturaFuego,
            sonidoBloqueo,
            musica
        );

        lluvia.crear();
    }

    @Override
    public void render(float delta) {

        // Movimiento del caballero
        caballero.actualizarMovimiento();

        // Actualizar proyectiles y colisiones
        boolean partidaActiva = lluvia.actualizarMovimiento(
            caballero,
            castillo
        );

        // Actualizar record
        gestorPartida.actualizarRecord(
            caballero.getPuntos()
        );

        // Avanzar rondas segun la puntuacion
        gestorPartida.actualizar(
            caballero.getPuntos()
        );
        
     // Comprobar victoria al superar la ronda 5
        if (gestorPartida.hayVictoria()) {

            int puntosFinales = caballero.getPuntos();

            game.setScreen(
                new VictoriaScreen(game, puntosFinales)
            );

            dispose();
            return;
        }

        // Derrota
        if (!partidaActiva || castillo.estaDestruido()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
            return;
        }

        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        font.draw(
            batch,
            "Puntos: " + caballero.getPuntos(),
            10, 470
        );

        font.draw(
            batch,
            "Record: " + gestorPartida.getRecord(),
            310, 470
        );

        font.draw(
            batch,
            "Castillo: " + castillo.getVidas() + "/"
                + castillo.getVidasMaximas(),
            650, 470
        );

        font.draw(
            batch,
            "Ronda: " + gestorPartida.getRondaActual()
                + "/" + gestorPartida.getTotalRondas(),
            310, 440
        );

        font.draw(
            batch,
            "Objetivo: " + gestorPartida.getObjetivoActual(),
            310, 410
        );

        caballero.dibujar(batch);
        lluvia.actualizarDibujoLluvia(batch);

        batch.end();
    }

    @Override
    public void show() {
        lluvia.continuar();
    }

    @Override
    public void pause() {
        lluvia.pausar();
        game.setScreen(new PausaScreen(game, this));
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void dispose() {
        caballero.destruir();
        lluvia.destruir();
    }
}
