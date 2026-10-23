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

    private Texture texturaFondo;

    private Caballero caballero;
    private Castillo castillo;
    private Lluvia lluvia;

    private GestorPartida gestorPartida;

    public GameScreen(final ShieldHeroGame game) {

        this.game = game;

        batch = game.getBatch();
        font = game.getFont();

        gestorPartida = GestorPartida.getInstancia();
        gestorPartida.reiniciarRondas();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // Escenario medieval
        texturaFondo = new Texture(
            Gdx.files.internal("escenarios/fondo.png")
        );

        // Castillo
        castillo = new Castillo();

        // Caballero
        Texture texturaCaballero = new Texture(
            Gdx.files.internal("personajes/caballero.png")
        );

        Sound sonidoHerido = Gdx.audio.newSound(
            Gdx.files.internal("hurt.ogg")
        );

        caballero = new Caballero(
            texturaCaballero,
            sonidoHerido
        );

        caballero.crear();

        // Proyectiles medievales
        Texture texturaFlecha = new Texture(
            Gdx.files.internal("proyectiles/flecha.png")
        );

        Texture texturaPiedra = new Texture(
            Gdx.files.internal("proyectiles/piedra.png")
        );

        Texture texturaFuego = new Texture(
            Gdx.files.internal("proyectiles/bolafuego.png")
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

        caballero.actualizarMovimiento();

        boolean partidaActiva = lluvia.actualizarMovimiento(
            caballero,
            castillo
        );

        gestorPartida.actualizarRecord(caballero.getPuntos());
        gestorPartida.actualizar(caballero.getPuntos());

        if (gestorPartida.hayVictoria()) {

            int puntosFinales = caballero.getPuntos();

            game.setScreen(
                new VictoriaScreen(game, puntosFinales)
            );

            dispose();
            return;
        }

        if (!partidaActiva || castillo.estaDestruido()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
            return;
        }

        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // El fondo debe dibujarse primero
        batch.draw(texturaFondo, 0, 0, 800, 480);

        // Personaje y ataques sobre el fondo
        caballero.dibujar(batch);
        lluvia.actualizarDibujoLluvia(batch);

        // Interfaz sobre todos los elementos
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

        texturaFondo.dispose();

        caballero.destruir();
        lluvia.destruir();
    }
}
