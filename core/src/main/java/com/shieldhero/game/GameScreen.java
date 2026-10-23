
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

    private Caballero tarro;
    private Lluvia lluvia;
    private Castillo castillo;

    public GameScreen(final ShieldHeroGame game) {
        this.game = game;

        batch = game.getBatch();
        font = game.getFont();

        // Crear el castillo con 5 vidas
        castillo = new Castillo();

        // Crear el caballero
        Texture imagenCaballero = new Texture(
            Gdx.files.internal("bucket.png")
        );

        Sound sonidoHerido = Gdx.audio.newSound(
            Gdx.files.internal("hurt.ogg")
        );

        tarro = new Caballero(imagenCaballero, sonidoHerido);

        // Crear los objetos que caen
        Texture gota = new Texture(
            Gdx.files.internal("drop.png")
        );

        Texture gotaMala = new Texture(
            Gdx.files.internal("dropBad.png")
        );

        Sound sonidoGota = Gdx.audio.newSound(
            Gdx.files.internal("drop.wav")
        );

        Music musica = Gdx.audio.newMusic(
            Gdx.files.internal("rain.mp3")
        );

        lluvia = new Lluvia(
            gota,
            gotaMala,
            sonidoGota,
            musica
        );

        // Configurar camara
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // Inicializar objetos
        tarro.crear();
        lluvia.crear();
    }

    @Override
    public void render(float delta) {

        // Actualizar la partida
        if (!tarro.estaHerido()) {
            tarro.actualizarMovimiento();

            boolean caballeroSigueVivo =
                lluvia.actualizarMovimiento(tarro, castillo);

            // Actualizar record
            if (tarro.getPuntos() > game.getHigherScore()) {
                game.setHigherScore(tarro.getPuntos());
            }

            // Comprobar derrota
            if (!caballeroSigueVivo || castillo.estaDestruido()) {
                game.setScreen(new GameOverScreen(game));
                dispose();
                return;
            }
        }

        // Limpiar pantalla
        ScreenUtils.clear(0, 0, 0.2f, 1);

        // Actualizar camara
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Dibujar partida
        batch.begin();

        font.draw(batch,
            "Puntos: " + tarro.getPuntos(),
            5, 475
        );

        font.draw(batch,
            "Record: " + game.getHigherScore(),
            330, 475
        );

        font.draw(batch,
            "Caballero: " + tarro.getVidas(),
            660, 475
        );

        font.draw(batch,
            "Castillo: " + castillo.getVidas(),
            660, 445
        );

        tarro.dibujar(batch);
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
    public void resize(int width, int height) {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        tarro.destruir();
        lluvia.destruir();
    }
}
