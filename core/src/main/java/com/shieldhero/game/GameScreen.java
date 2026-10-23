
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

    public GameScreen(final ShieldHeroGame game) {
        this.game = game;

        // Recursos compartidos del juego
        batch = game.getBatch();
        font = game.getFont();

        // Camara
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // Castillo con 5 vidas
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

        // Texturas provisionales de los proyectiles
        Texture texturaFlecha = new Texture(
            Gdx.files.internal("drop.png")
        );

        Texture texturaPiedra = new Texture(
            Gdx.files.internal("dropBad.png")
        );

        Texture texturaFuego = new Texture(
            Gdx.files.internal("drop.png")
        );

        // Sonido de bloqueo
        Sound sonidoBloqueo = Gdx.audio.newSound(
            Gdx.files.internal("drop.wav")
        );

        // Musica de fondo
        Music musica = Gdx.audio.newMusic(
            Gdx.files.internal("rain.mp3")
        );

        // Sistema de proyectiles
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

        // 1. Actualizar movimiento del caballero
        caballero.actualizarMovimiento();

        // 2. Actualizar proyectiles y colisiones
        boolean partidaActiva = lluvia.actualizarMovimiento(
            caballero,
            castillo
        );

        // 3. Actualizar record de puntuacion
        if (caballero.getPuntos() > game.getHigherScore()) {
            game.setHigherScore(caballero.getPuntos());
        }

        // 4. Comprobar derrota
        if (!partidaActiva || castillo.estaDestruido()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
            return;
        }

        // 5. Limpiar pantalla
        ScreenUtils.clear(0, 0, 0.2f, 1);

        // 6. Actualizar camara
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // 7. Dibujar partida
        batch.begin();

        font.draw(
            batch,
            "Puntos: " + caballero.getPuntos(),
            10, 470
        );

        font.draw(
            batch,
            "Record: " + game.getHigherScore(),
            310, 470
        );

        font.draw(
            batch,
            "Castillo: " + castillo.getVidas() + "/"
                + castillo.getVidasMaximas(),
            650, 470
        );

        // Dibujar personaje y ataques
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
