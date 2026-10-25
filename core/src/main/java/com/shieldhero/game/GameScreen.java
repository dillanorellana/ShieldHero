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

    // =====================================
    // MURALLA CON DANO VISUAL
    // =====================================

    private MurallaDanioVisual murallaDanioVisual;

    // =====================================
    // ARQUEROS
    // =====================================

    private Texture texturaArquero;

    private ArqueroEnemigo arqueroEnemigo1;
    private ArqueroEnemigo arqueroEnemigo2;

    // =====================================
    // CANON
    // =====================================

    private Texture texturaCanon;
    private CanonEnemigo canonEnemigo;

    // =====================================
    // VOLCAN
    // =====================================

    private VolcanEnemigo volcanEnemigo;

    // =====================================
    // ELEMENTOS DEL JUEGO
    // =====================================

    private Caballero caballero;
    private Castillo castillo;
    private Lluvia lluvia;

    private GestorPartida gestorPartida;

    // =====================================
    // COLORES DEL HUD
    // =====================================

    private static final Color COLOR_DASH =
        new Color(0.22f, 0.74f, 0.97f, 1f);

    private static final Color COLOR_PARRY =
        new Color(1f, 0.75f, 0.18f, 1f);

    private static final Color COLOR_FONDO_BARRA =
        new Color(0.18f, 0.22f, 0.28f, 1f);

    // =====================================
    // CONSTRUCTOR
    // =====================================

    public GameScreen(final ShieldHeroGame game) {

        this.game = game;

        batch = game.getBatch();
        font = game.getFont();

        fontHabilidades = new BitmapFont();
        fontHabilidades.getData().setScale(0.75f);

        gestorPartida = GestorPartida.getInstancia();
        gestorPartida.reiniciarRondas();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // =====================================
        // FONDO
        // =====================================

        if (Gdx.files.internal(
                "escenarios/fondo_asedio.png"
            ).exists()) {

            texturaFondo = new Texture(
                Gdx.files.internal(
                    "escenarios/fondo_asedio.png"
                )
            );

        } else {

            texturaFondo = new Texture(
                Gdx.files.internal(
                    "escenarios/fondo.png"
                )
            );
        }

        // =====================================
        // NUEVO: MURALLA
        // =====================================

        murallaDanioVisual = new MurallaDanioVisual();

        // =====================================
        // TEXTURA DEL HUD
        // =====================================

        Pixmap pixmap = new Pixmap(
            1,
            1,
            Pixmap.Format.RGBA8888
        );

        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        texturaBarra = new Texture(pixmap);

        pixmap.dispose();

        // =====================================
        // CASTILLO
        // =====================================

        castillo = new Castillo();

        // =====================================
        // CABALLERO
        // =====================================

        Texture texturaCaballero = new Texture(
            Gdx.files.internal(
                "personajes/caballero.png"
            )
        );

        Sound sonidoHerido = Gdx.audio.newSound(
            Gdx.files.internal("hurt.ogg")
        );

        caballero = new Caballero(
            texturaCaballero,
            sonidoHerido
        );

        Texture texturaParry = new Texture(
            Gdx.files.internal(
                "personajes/caballero_parry.png"
            )
        );

        caballero.setImagenParry(texturaParry);
        caballero.crear();

        // =====================================
        // ARQUEROS
        // =====================================

        texturaArquero = new Texture(
            Gdx.files.internal(
                "personajes/arquero_enemigo.png"
            )
        );

        arqueroEnemigo1 = new ArqueroEnemigo(
            texturaArquero,
            686f,
            120f
        );

        arqueroEnemigo2 = new ArqueroEnemigo(
            texturaArquero,
            748f,
            120f
        );

        // =====================================
        // CANON
        // =====================================

        texturaCanon = new Texture(
            Gdx.files.internal(
                "personajes/canon_Enemigo.png"
            )
        );

        canonEnemigo = new CanonEnemigo(
            texturaCanon,
            590f,
            120f
        );

        // =====================================
        // VOLCAN
        // =====================================

        volcanEnemigo = new VolcanEnemigo(
            640f,
            365f
        );

        volcanEnemigo.setCraterOffset(
            0f,
            0f
        );

        // =====================================
        // PROYECTILES
        // =====================================

        Texture texturaFlecha = new Texture(
            Gdx.files.internal(
                "proyectiles/flecha.png"
            )
        );

        Texture texturaPiedra = new Texture(
            Gdx.files.internal(
                "proyectiles/piedra.png"
            )
        );

        Texture texturaFuego = new Texture(
            Gdx.files.internal(
                "proyectiles/bolafuego.png"
            )
        );

        Sound sonidoBloqueo = Gdx.audio.newSound(
            Gdx.files.internal("drop.wav")
        );

        Music musica = Gdx.audio.newMusic(
            Gdx.files.internal("rain.mp3")
        );

        // =====================================
        // SISTEMA DE ATAQUES
        // =====================================

        lluvia = new Lluvia(
            texturaFlecha,
            texturaPiedra,
            texturaFuego,
            sonidoBloqueo,
            musica
        );

        lluvia.setArqueros(
            arqueroEnemigo1,
            arqueroEnemigo2
        );

        lluvia.setCanon(canonEnemigo);
        lluvia.setVolcan(volcanEnemigo);

        lluvia.crear();
    }

    // =====================================
    // RENDER
    // =====================================

    @Override
    public void render(float delta) {

        caballero.actualizarMovimiento();

        // =====================================
        // SISTEMA DE RONDAS
        // =====================================

        if (gestorPartida.estaEnDescanso()) {

            gestorPartida.actualizarDescanso(delta);

            if (!gestorPartida.estaEnDescanso()) {
                lluvia.reiniciarTiempoAparicion();
            }

        } else {

            if (gestorPartida.estaEnSupervivencia()) {
                gestorPartida.actualizarSupervivencia(delta);
            }

            boolean partidaActiva =
                lluvia.actualizarMovimiento(
                    caballero,
                    castillo
                );

            gestorPartida.actualizarRecord(
                caballero.getPuntos()
            );

            // =====================================
            // DERROTA
            // =====================================

            if (!partidaActiva
                    || castillo.estaDestruido()) {

                game.setScreen(
                    new GameOverScreen(game)
                );

                dispose();
                return;
            }

            // =====================================
            // PROGRESO DE RONDAS
            // =====================================

            gestorPartida.actualizar(
                caballero.getPuntos()
            );

            // =====================================
            // VICTORIA
            // =====================================

            if (gestorPartida.hayVictoria()) {

                int puntosFinales =
                    caballero.getPuntos();

                game.setScreen(
                    new VictoriaScreen(
                        game,
                        this,
                        puntosFinales
                    )
                );

                return;
            }

            // =====================================
            // DESCANSO
            // =====================================

            if (gestorPartida.estaEnDescanso()) {

                lluvia.limpiarProyectiles();

                castillo.recuperarVida(4);
            }
        }

        // =====================================
        // ANIMACIONES ENEMIGAS
        // =====================================

        arqueroEnemigo1.actualizar(delta);
        arqueroEnemigo2.actualizar(delta);

        canonEnemigo.actualizar(delta);
        volcanEnemigo.actualizar(delta);

        // =====================================
        // PREPARAR DIBUJO
        // =====================================

        ScreenUtils.clear(0f, 0f, 0.2f, 1f);

        camera.update();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.setColor(Color.WHITE);

        // =====================================
        // 1. FONDO ORIGINAL
        // =====================================

        batch.draw(
            texturaFondo,
            0,
            0,
            800,
            480
        );

        // =====================================
        // 2. NUEVO: DANO DE LA MURALLA
        // =====================================

        // Usa las vidas actuales para elegir
        // automaticamente muralla_5.png,
        // muralla_4.png ... muralla_0.png.

        murallaDanioVisual.dibujar(
            batch,
            castillo.getVidas()
        );

        // =====================================
        // 3. ENEMIGOS
        // =====================================

        canonEnemigo.dibujar(batch);

        arqueroEnemigo1.dibujar(batch);
        arqueroEnemigo2.dibujar(batch);

        // =====================================
        // 4. CABALLERO Y PROYECTILES
        // =====================================

        caballero.dibujar(batch);

        lluvia.actualizarDibujoLluvia(batch);

        // =====================================
        // 5. HUD PRINCIPAL
        // =====================================

        batch.setColor(Color.WHITE);
        font.setColor(Color.WHITE);

        font.draw(
            batch,
            "Puntos: " + caballero.getPuntos(),
            10,
            470
        );

        font.draw(
            batch,
            "Record: "
                + gestorPartida.getRecord(),
            310,
            470
        );

        font.draw(
            batch,
            "Castillo: "
                + castillo.getVidas()
                + "/"
                + castillo.getVidasMaximas(),
            650,
            470
        );

        // =====================================
        // HUD DE RONDAS
        // =====================================

        if (gestorPartida.estaEnSupervivencia()) {

            font.setColor(Color.GOLD);

            font.draw(
                batch,
                "MODO SUPERVIVENCIA",
                290,
                440
            );

            font.setColor(Color.WHITE);

            font.draw(
                batch,
                "Tiempo: "
                    + (int) gestorPartida
                        .getTiempoSupervivencia()
                    + " s",
                310,
                410
            );

        } else {

            font.draw(
                batch,
                "Ronda: "
                    + gestorPartida.getRondaActual()
                    + "/"
                    + gestorPartida.getTotalRondas(),
                310,
                440
            );

            font.draw(
                batch,
                "Objetivo: "
                    + gestorPartida.getObjetivoActual(),
                310,
                410
            );
        }

        // =====================================
        // HUD DE HABILIDADES
        // =====================================

        dibujarHUDHabilidades();

        // =====================================
        // MENSAJE ENTRE RONDAS
        // =====================================

        if (gestorPartida.estaEnDescanso()) {
            dibujarDescanso();
        }

        batch.end();

        // =====================================
        // EFECTOS VISUALES DEL CANON
        // =====================================

        canonEnemigo.configurarCamara(
            camera.combined
        );

        canonEnemigo.dibujarEfectos();

        // =====================================
        // EFECTOS VISUALES DEL VOLCAN
        // =====================================

        volcanEnemigo.configurarCamara(
            camera.combined
        );

        volcanEnemigo.dibujarEfectos();
    }

    // =====================================
    // MENSAJE DE DESCANSO
    // =====================================

    private void dibujarDescanso() {

        batch.setColor(
            0f,
            0f,
            0f,
            0.75f
        );

        batch.draw(
            texturaBarra,
            0,
            145,
            800,
            190
        );

        batch.setColor(Color.WHITE);

        float escalaX = font.getData().scaleX;
        float escalaY = font.getData().scaleY;

        font.getData().setScale(1.8f);

        font.draw(
            batch,
            "RONDA "
                + gestorPartida.getRondaSuperada()
                + " SUPERADA!",
            210,
            295
        );

        font.getData().setScale(1.1f);

        font.draw(
            batch,
            "Castillo: +4 vidas (maximo 5)",
            250,
            255
        );

        font.draw(
            batch,
            "Siguiente ronda: "
                + gestorPartida.getSiguienteRonda(),
            285,
            220
        );

        font.draw(
            batch,
            "Comienza en "
                + gestorPartida.getTiempoDescanso()
                + "...",
            320,
            185
        );

        font.getData().setScale(
            escalaX,
            escalaY
        );
    }

    // =====================================
    // HUD DASH Y PARRY
    // =====================================

    private void dibujarHUDHabilidades() {

        dibujarBarraHabilidad(
            "DASH [SHIFT]",
            10,
            425,
            caballero.getTiempoRecargaDash(),
            1.5f,
            COLOR_DASH
        );

        dibujarBarraHabilidad(
            "PARRY [ESPACIO]",
            10,
            385,
            caballero.getTiempoRecargaParry(),
            1f,
            COLOR_PARRY
        );
    }

    // =====================================
    // BARRA DE HABILIDAD
    // =====================================

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

        fontHabilidades.setColor(Color.WHITE);

        fontHabilidades.draw(
            batch,
            nombre,
            x,
            y + 15f
        );

        // Fondo.
        batch.setColor(COLOR_FONDO_BARRA);

        batch.draw(
            texturaBarra,
            x,
            y - 7f,
            ancho,
            alto
        );

        // Progreso.
        batch.setColor(color);

        batch.draw(
            texturaBarra,
            x,
            y - 7f,
            ancho * progreso,
            alto
        );

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

    // =====================================
    // CICLO DE VIDA
    // =====================================

    @Override
    public void show() {
        lluvia.continuar();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        lluvia.pausar();
    }

    @Override
    public void resize(int width, int height) {
    }

    // =====================================
    // LIBERACION DE RECURSOS
    // =====================================

    @Override
    public void dispose() {

        texturaFondo.dispose();
        texturaBarra.dispose();

        fontHabilidades.dispose();

        // NUEVO: liberar imagenes de muralla.
        murallaDanioVisual.destruir();

        texturaArquero.dispose();
        texturaCanon.dispose();

        canonEnemigo.destruir();
        volcanEnemigo.destruir();

        caballero.destruir();
        lluvia.destruir();
    }
}
