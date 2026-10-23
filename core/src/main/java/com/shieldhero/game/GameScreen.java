package com.shieldhero.game;

import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {

    private final ShieldHeroGame game;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont fontHabilidades;

    private Texture texturaFondo;
    private Texture texturaBarra;

    private Caballero caballero;
    private Castillo castillo;
    private Lluvia lluvia;

    private GestorPartida gestorPartida;

    private static final Color COLOR_DASH =
        new Color(0.22f, 0.74f, 0.97f, 1f);

    private static final Color COLOR_PARRY =
        new Color(1f, 0.75f, 0.18f, 1f);

    private static final Color COLOR_FONDO_BARRA =
        new Color(0.18f, 0.22f, 0.28f, 1f);

    public GameScreen(final ShieldHeroGame game) {

        this.game = game;

        batch = game.getBatch();
        font = game.getFont();

        // Fuente pequeña para las habilidades
        fontHabilidades = new BitmapFont();
        fontHabilidades.getData().setScale(0.75f);

        gestorPartida = GestorPartida.getInstancia();
        gestorPartida.reiniciarRondas();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // Fondo medieval
        texturaFondo = new Texture(
            Gdx.files.internal("escenarios/fondo.png")
        );

        // Textura para las barras de recarga
        Pixmap pixmap = new Pixmap(
            1, 1, Pixmap.Format.RGBA8888
        );

        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        texturaBarra = new Texture(pixmap);
        pixmap.dispose();

        // Castillo
        castillo = new Castillo();

        // Sprite normal del caballero
        Texture texturaCaballero = new Texture(
            Gdx.files.internal("personajes/caballero.png")
        );

        // Sonido del caballero
        Sound sonidoHerido = Gdx.audio.newSound(
            Gdx.files.internal("hurt.ogg")
        );

        caballero = new Caballero(
            texturaCaballero,
            sonidoHerido
        );

        // NUEVO: sprite del Parry
        Texture texturaParry = new Texture(
            Gdx.files.internal("personajes/caballero_parry.png")
        );

        caballero.setImagenParry(texturaParry);
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

        // Victoria
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

        // Fondo primero
        batch.draw(texturaFondo, 0, 0, 800, 480);

        // Personaje y ataques
        caballero.dibujar(batch);
        lluvia.actualizarDibujoLluvia(batch);

        // HUD principal
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

        // Barras de Dash y Parry
        dibujarHUDHabilidades();

        batch.end();
    }

    private void dibujarHUDHabilidades() {

        // Barra azul de Dash
        dibujarBarraHabilidad(
            "DASH [SHIFT]",
            10, 425,
            caballero.getTiempoRecargaDash(),
            1.5f,
            COLOR_DASH
        );

        // Barra dorada de Parry
        dibujarBarraHabilidad(
            "PARRY [ESPACIO]",
            10, 385,
            caballero.getTiempoRecargaParry(),
            1.0f,
            COLOR_PARRY
        );
    }

    private void dibujarBarraHabilidad(
            String nombre,
            float x,
            float y,
            float recargaRestante,
            float recargaMaxima,
            Color color) {

        float ancho = 85f;
        float alto = 6f;

        float progreso = MathUtils.clamp(
            1f - recargaRestante / recargaMaxima,
            0f,
            1f
        );

        // Nombre pequeño encima de la barra
        fontHabilidades.setColor(Color.WHITE);

        fontHabilidades.draw(
            batch,
            nombre,
            x,
            y + 15f
        );

        // Fondo de la barra
        batch.setColor(COLOR_FONDO_BARRA);

        batch.draw(
            texturaBarra,
            x,
            y - 7f,
            ancho,
            alto
        );

        // Progreso
        batch.setColor(color);

        batch.draw(
            texturaBarra,
            x,
            y - 7f,
            ancho * progreso,
            alto
        );

        // Restaurar color
        batch.setColor(Color.WHITE);

        String estado;

        if (recargaRestante <= 0f) {

            estado = "LISTO";

        } else {

            estado = String.format(
                Locale.US,
                "%.1f s",
                recargaRestante
            );
        }

        fontHabilidades.draw(
            batch,
            estado,
            x + ancho + 8f,
            y + 1f
        );
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
        texturaBarra.dispose();
        fontHabilidades.dispose();

        caballero.destruir();
        lluvia.destruir();
    }
}
