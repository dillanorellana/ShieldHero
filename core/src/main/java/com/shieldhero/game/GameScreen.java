
package com.shieldhero.game;

import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {

    private static final float DURACION_ESCUDO = 5f;
    private static final float RECARGA_ESCUDO = 25f;

    private static final float DURACION_RALENTIZACION = 8f;
    private static final float RECARGA_RALENTIZACION = 30f;

    private final ShieldHeroGame game;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont fontHabilidades;

    private Texture texturaFondo;
    private Texture texturaBarra;

    private MurallaDanioVisual murallaDanioVisual;

    private Texture texturaArquero;
    private ArqueroEnemigo arqueroEnemigo1;
    private ArqueroEnemigo arqueroEnemigo2;

    private Texture texturaCanon;
    private CanonEnemigo canonEnemigo;

    private VolcanEnemigo volcanEnemigo;

    private Caballero caballero;
    private Castillo castillo;
    private Lluvia lluvia;
    private GestorPartida gestorPartida;

    private boolean mejoraRonda3Mostrada;
    private boolean reparacionDescansoAplicada;
    private boolean seleccionMejoraAbierta;

    // =====================================
    // PODERES Z Y X
    // =====================================

    private boolean escudoDesbloqueado;
    private boolean ralentizacionDesbloqueada;

    private float tiempoEscudo;
    private float recargaEscudo;

    private float tiempoRalentizacion;
    private float recargaRalentizacion;

    // =====================================
    // GUARDIAN EXTRA
    // =====================================

    private GuardianExtra guardianExtra;
    private Texture texturaGuardian;

    private boolean colocandoGuardian;
    private float retrasoColocacion;

    // =====================================
    // GIGANTE DEL ASEDIO
    // =====================================

    private JefeGigante jefeGigante;

    private Texture texturaGigante;
    private Texture texturaPiedraGigante;

    private Array<PiedraGigante> piedrasGigantes;

    private boolean segundoLanzamientoPendiente;
    private float tiempoSegundoLanzamiento;

    private boolean destruida;

    private static final Color COLOR_DASH =
        new Color(0.22f, 0.74f, 0.97f, 1f);

    private static final Color COLOR_PARRY =
        new Color(1f, 0.75f, 0.18f, 1f);

    private static final Color COLOR_ESCUDO =
        new Color(0.22f, 0.86f, 1f, 1f);

    private static final Color COLOR_TIEMPO =
        new Color(0.70f, 0.48f, 1f, 1f);

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

        mejoraRonda3Mostrada = false;
        reparacionDescansoAplicada = false;
        seleccionMejoraAbierta = false;

        escudoDesbloqueado = false;
        ralentizacionDesbloqueada = false;

        tiempoEscudo = 0f;
        recargaEscudo = 0f;

        tiempoRalentizacion = 0f;
        recargaRalentizacion = 0f;

        colocandoGuardian = false;
        retrasoColocacion = 0f;

        destruida = false;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // =====================================
        // FONDO
        // =====================================

        String rutaFondo =
            Gdx.files.internal(
                "escenarios/fondo_asedio.png"
            ).exists()
                ? "escenarios/fondo_asedio.png"
                : "escenarios/fondo.png";

        texturaFondo = new Texture(
            Gdx.files.internal(rutaFondo)
        );

        murallaDanioVisual = new MurallaDanioVisual();

        // =====================================
        // TEXTURA PARA BARRAS
        // =====================================

        Pixmap pixmap = new Pixmap(
            1, 1, Pixmap.Format.RGBA8888
        );

        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        texturaBarra = new Texture(pixmap);
        pixmap.dispose();

        // =====================================
        // CASTILLO Y CABALLERO
        // =====================================

        castillo = new Castillo();

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
        // PNG DEFINITIVO DEL GUARDIAN
        // =====================================

        texturaGuardian = new Texture(
            Gdx.files.internal(
                "personajes/guardian_extra.png"
            )
        );

        texturaGuardian.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        guardianExtra = new GuardianExtra(
            texturaGuardian,
            texturaBarra
        );

        // =====================================
        // ARQUEROS
        // =====================================

        texturaArquero = new Texture(
            Gdx.files.internal(
                "personajes/arquero_enemigo.png"
            )
        );

        arqueroEnemigo1 = new ArqueroEnemigo(
            texturaArquero, 686f, 120f
        );

        arqueroEnemigo2 = new ArqueroEnemigo(
            texturaArquero, 748f, 120f
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
            texturaCanon, 590f, 120f
        );

        // =====================================
        // VOLCAN
        // =====================================

        volcanEnemigo = new VolcanEnemigo(
            640f, 365f
        );

        volcanEnemigo.setCraterOffset(0f, 0f);

        // =====================================
        // PROYECTILES NORMALES
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

        lluvia.setGuardianExtra(guardianExtra);

        lluvia.crear();

        // =====================================
        // GIGANTE
        // =====================================

        piedrasGigantes = new Array<PiedraGigante>();

        texturaPiedraGigante = new Texture(
            Gdx.files.internal(
                "proyectiles/piedra.png"
            )
        );

        texturaGigante = new Texture(
            Gdx.files.internal(
                "personajes/gigante_asedio.png"
            )
        );

        texturaGigante.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        jefeGigante = null;

        segundoLanzamientoPendiente = false;
        tiempoSegundoLanzamiento = 0f;
    }

    // =====================================
    // ACTIVAR PODERES CON TECLAS
    // =====================================

    private void actualizarHabilidades(float delta) {

        tiempoEscudo = Math.max(
            0f, tiempoEscudo - delta
        );

        recargaEscudo = Math.max(
            0f, recargaEscudo - delta
        );

        tiempoRalentizacion = Math.max(
            0f, tiempoRalentizacion - delta
        );

        recargaRalentizacion = Math.max(
            0f, recargaRalentizacion - delta
        );

        // Z: escudo temporal.
        if (escudoDesbloqueado
                && recargaEscudo <= 0f
                && tiempoEscudo <= 0f
                && Gdx.input.isKeyJustPressed(
                    Input.Keys.Z
                )) {

            tiempoEscudo = DURACION_ESCUDO;
            recargaEscudo = RECARGA_ESCUDO;

            System.out.println(
                "ESCUDO TEMPORAL ACTIVADO"
            );
        }

        // X: ralentizar proyectiles.
        if (ralentizacionDesbloqueada
                && recargaRalentizacion <= 0f
                && tiempoRalentizacion <= 0f
                && Gdx.input.isKeyJustPressed(
                    Input.Keys.X
                )) {

            tiempoRalentizacion =
                DURACION_RALENTIZACION;

            recargaRalentizacion =
                RECARGA_RALENTIZACION;

            System.out.println(
                "RALENTIZACION ACTIVADA"
            );
        }

        // Comunicar los efectos a los ataques.
        lluvia.setEscudoTemporalActivo(
            tiempoEscudo > 0f
        );

        lluvia.setRalentizacionActiva(
            tiempoRalentizacion > 0f
        );
    }

    // =====================================
    // PANTALLAS DE MEJORAS
    // =====================================

    private void mostrarPantallaMejoras(
            boolean especial,
            int ronda,
            boolean supervivencia) {

        if (seleccionMejoraAbierta) {
            return;
        }

        seleccionMejoraAbierta = true;

        PantallaMejoras pantalla = new PantallaMejoras(
            game,
            especial,
            ronda,
            mejora -> {

                aplicarMejora(mejora);

                if (supervivencia) {
                    gestorPartida.confirmarEventoMejora();
                }

                seleccionMejoraAbierta = false;

                lluvia.reiniciarTiempoAparicion();

                if (!colocandoGuardian
                        && gestorPartida.debeAparecerJefe()) {

                    iniciarJefe();
                }

                game.setScreen(this);
            }
        );

        game.setScreen(pantalla);
    }

    // =====================================
    // APLICAR MEJORAS
    // =====================================

    private void aplicarMejora(
            PantallaMejoras.TipoMejora mejora) {

        switch (mejora) {

            case PARRY:
                caballero.mejorarParry();
                break;

            case DASH:
                caballero.mejorarDash();
                break;

            case REPARACION:
                castillo.recuperarVida(4);
                break;

            case ESCUDO_TEMPORAL:
                escudoDesbloqueado = true;

                // Si se vuelve a elegir,
                // queda disponible otra vez.
                recargaEscudo = 0f;
                break;

            case RALENTIZACION:
                ralentizacionDesbloqueada = true;
                recargaRalentizacion = 0f;
                break;

            case GUARDIAN_EXTRA:

                if (guardianExtra.estaColocado()) {

                    guardianExtra.mejorarAura();

                } else {

                    colocandoGuardian = true;
                    retrasoColocacion = 0.20f;
                }

                break;

            default:
                break;
        }
    }

    // =====================================
    // COLOCACION DEL GUARDIAN
    // =====================================

    private void actualizarColocacion(float delta) {

        retrasoColocacion = Math.max(
            0f,
            retrasoColocacion - delta
        );

        if (retrasoColocacion > 0f
                || !Gdx.input.justTouched()) {

            return;
        }

        Vector3 posicion = new Vector3(
            Gdx.input.getX(),
            Gdx.input.getY(),
            0f
        );

        camera.update();
        camera.unproject(posicion);

        if (posicion.x < 30f
                || posicion.x > 415f
                || posicion.y < 115f
                || posicion.y > 200f) {

            return;
        }

        guardianExtra.colocar(posicion.x);

        colocandoGuardian = false;

        lluvia.reiniciarTiempoAparicion();

        if (gestorPartida.debeAparecerJefe()) {
            iniciarJefe();
        }
    }

    // =====================================
    // EVENTOS DE MEJORA
    // =====================================

    private boolean abrirMejoraPendiente() {

        if (!gestorPartida.estaEnSupervivencia()
                || !gestorPartida.hayEventoPendiente()) {

            return false;
        }

        boolean especial =
            gestorPartida.getEventoPendiente()
                == GestorPartida.TipoEvento.MEJORA_ESPECIAL;

        lluvia.limpiarProyectiles();

        mostrarPantallaMejoras(
            especial,
            gestorPartida.getRondaSupervivencia(),
            true
        );

        return true;
    }

    // =====================================
    // GIGANTE
    // =====================================

    private void iniciarJefe() {

        if (!gestorPartida.debeAparecerJefe()) {
            return;
        }

        gestorPartida.iniciarCombateJefe();

        jefeGigante = new JefeGigante(
            texturaGigante,
            gestorPartida.getNumeroJefeActual()
        );

        jefeGigante.aparecer();

        piedrasGigantes.clear();

        segundoLanzamientoPendiente = false;
        tiempoSegundoLanzamiento = 0f;

        lluvia.limpiarProyectiles();
        lluvia.reiniciarTiempoAparicion();
    }

    private void lanzarPiedraGigante() {

        if (jefeGigante == null
                || !jefeGigante.estaActivo()) {

            return;
        }

        float destinoX = MathUtils.random(
            42f, 373f
        );

        piedrasGigantes.add(
            new PiedraGigante(
                texturaPiedraGigante,
                jefeGigante,
                destinoX
            )
        );
    }

    private void actualizarJefe(float delta) {

        if (jefeGigante == null
                || !gestorPartida.hayJefeActivo()) {

            return;
        }

        // La X ralentiza tambien los ataques
        // especiales del gigante.
        float deltaAtaques =
            tiempoRalentizacion > 0f
                ? delta * 0.50f
                : delta;

        jefeGigante.actualizar(deltaAtaques);

        if (jefeGigante.debeAtacar()) {

            jefeGigante.registrarAtaque();
            lanzarPiedraGigante();

            if (jefeGigante.debeLanzarDoble()) {

                segundoLanzamientoPendiente = true;
                tiempoSegundoLanzamiento = 0.35f;
            }
        }

        if (segundoLanzamientoPendiente) {

            tiempoSegundoLanzamiento -= deltaAtaques;

            if (tiempoSegundoLanzamiento <= 0f) {

                lanzarPiedraGigante();

                segundoLanzamientoPendiente = false;
            }
        }

        // El escudo Z protege contra los
        // proyectiles normales.
        //
        // Las rocas del gigante necesitan parry
        // para devolverlas y causarle dano.
        for (int i = piedrasGigantes.size - 1;
                i >= 0; i--) {

            PiedraGigante piedra = piedrasGigantes.get(i);

            piedra.actualizar(
                deltaAtaques,
                caballero
            );

            if (piedra.estaTerminada()) {
                piedrasGigantes.removeIndex(i);
            }
        }

        if (jefeGigante.estaDerrotado()) {
            finalizarJefe();
        }
    }

    private void finalizarJefe() {

        gestorPartida.finalizarCombateJefe();

        segundoLanzamientoPendiente = false;
        tiempoSegundoLanzamiento = 0f;

        piedrasGigantes.clear();

        caballero.recuperarVida(1);
        castillo.recuperarVida(5);
        caballero.sumarPuntos(200);

        gestorPartida.actualizarRecord(
            caballero.getPuntos()
        );

        lluvia.limpiarProyectiles();
        lluvia.reiniciarTiempoAparicion();
    }

    // =====================================
    // RENDER PRINCIPAL
    // =====================================

    @Override
    public void render(float delta) {

        if (colocandoGuardian) {

            actualizarColocacion(delta);
            dibujarEscenario();

            return;
        }

        if (abrirMejoraPendiente()) {
            return;
        }

        if (gestorPartida.debeAparecerJefe()) {
            iniciarJefe();
        }

        caballero.actualizarMovimiento();

        guardianExtra.actualizar(delta);
        actualizarHabilidades(delta);

        if (gestorPartida.estaEnDescanso()) {

            gestorPartida.actualizarDescanso(delta);

            if (!gestorPartida.estaEnDescanso()) {

                reparacionDescansoAplicada = false;
                lluvia.reiniciarTiempoAparicion();
            }

        } else {

            if (gestorPartida.estaEnSupervivencia()) {

                gestorPartida.actualizarSupervivencia(delta);

                if (abrirMejoraPendiente()) {
                    return;
                }

                if (gestorPartida.debeAparecerJefe()) {
                    iniciarJefe();
                }

                actualizarJefe(delta);
            }

            boolean partidaActiva =
                lluvia.actualizarMovimiento(
                    caballero,
                    castillo
                );

            gestorPartida.actualizarRecord(
                caballero.getPuntos()
            );

            if (!partidaActiva
                    || castillo.estaDestruido()) {

                game.setScreen(
                    new GameOverScreen(game)
                );

                dispose();
                return;
            }

            gestorPartida.actualizar(
                caballero.getPuntos()
            );

            if (gestorPartida.hayVictoria()) {

                game.setScreen(
                    new VictoriaScreen(
                        game,
                        this,
                        caballero.getPuntos()
                    )
                );

                return;
            }

            if (gestorPartida.estaEnDescanso()) {

                lluvia.limpiarProyectiles();

                if (gestorPartida.getRondaSuperada() == 3
                        && !mejoraRonda3Mostrada) {

                    mejoraRonda3Mostrada = true;

                    mostrarPantallaMejoras(
                        false, 3, false
                    );

                    return;
                }

                if (gestorPartida.getRondaSuperada() != 3
                        && !reparacionDescansoAplicada) {

                    castillo.recuperarVida(4);
                    reparacionDescansoAplicada = true;
                }
            }
        }

        arqueroEnemigo1.actualizar(delta);
        arqueroEnemigo2.actualizar(delta);

        canonEnemigo.actualizar(delta);
        volcanEnemigo.actualizar(delta);

        dibujarEscenario();
    }

    // =====================================
    // DIBUJAR ESCENARIO
    // =====================================

    private void dibujarEscenario() {

        ScreenUtils.clear(0f, 0f, 0.2f, 1f);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.setColor(Color.WHITE);

        batch.draw(
            texturaFondo,
            0, 0, 800, 480
        );

        murallaDanioVisual.dibujar(
            batch,
            castillo.getVidas()
        );

        canonEnemigo.dibujar(batch);
        arqueroEnemigo1.dibujar(batch);
        arqueroEnemigo2.dibujar(batch);

        if (jefeGigante != null
                && jefeGigante.estaActivo()) {

            jefeGigante.dibujar(batch);
        }

        guardianExtra.dibujar(batch);
        caballero.dibujar(batch);

        lluvia.actualizarDibujoLluvia(batch);

        for (PiedraGigante piedra : piedrasGigantes) {
            piedra.dibujar(batch);
        }

        // Efecto visual del escudo temporal.
        if (tiempoEscudo > 0f) {

            batch.setColor(
                0.18f, 0.76f, 1f, 0.12f
            );

            batch.draw(
                texturaBarra,
                30f, 120f,
                385f, 85f
            );

            batch.setColor(Color.WHITE);
        }

        dibujarHUD();

        if (colocandoGuardian) {
            dibujarIndicacionColocacion();
        }

        if (gestorPartida.estaEnDescanso()) {
            dibujarDescanso();
        }

        batch.end();

        canonEnemigo.configurarCamara(
            camera.combined
        );

        canonEnemigo.dibujarEfectos();

        volcanEnemigo.configurarCamara(
            camera.combined
        );

        volcanEnemigo.dibujarEfectos();
    }

    // =====================================
    // HUD GENERAL
    // =====================================

    private void dibujarHUD() {

        batch.setColor(Color.WHITE);
        font.setColor(Color.WHITE);

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
            "Castillo: "
                + castillo.getVidas()
                + "/"
                + castillo.getVidasMaximas(),
            650, 470
        );

        if (gestorPartida.estaEnSupervivencia()) {

            font.setColor(Color.GOLD);

            font.draw(
                batch,
                "SUPERVIVENCIA - RONDA "
                    + gestorPartida.getRondaSupervivencia(),
                260, 440
            );

            font.setColor(Color.WHITE);

            font.draw(
                batch,
                "Tiempo: "
                    + (int) gestorPartida
                        .getTiempoSupervivencia()
                    + " s",
                285, 410
            );

            if (gestorPartida.hayJefeActivo()) {

                font.setColor(Color.RED);

                font.draw(
                    batch,
                    "TIEMPO DETENIDO - JEFE",
                    265, 385
                );

            } else {

                font.draw(
                    batch,
                    "Proxima ronda: "
                        + (int) Math.ceil(
                            gestorPartida
                                .getTiempoRestanteRondaSupervivencia()
                        )
                        + " s",
                    285, 385
                );
            }

            font.setColor(Color.WHITE);

        } else {

            font.draw(
                batch,
                "Ronda: "
                    + gestorPartida.getRondaActual()
                    + "/"
                    + gestorPartida.getTotalRondas(),
                310, 440
            );

            font.draw(
                batch,
                "Objetivo: "
                    + gestorPartida.getObjetivoActual(),
                310, 410
            );
        }

        dibujarHUDHabilidades();

        if (guardianExtra.estaColocado()) {

            font.setColor(Color.CYAN);

            font.draw(
                batch,
                "Guardian Nv."
                    + guardianExtra.getNivelAura()
                    + " - Aura: "
                    + (int) guardianExtra.getRadioAura(),
                10, 305
            );

            font.setColor(Color.WHITE);
        }

        if (caballero.estaRalentizado()) {

            font.setColor(Color.SKY);

            font.draw(
                batch,
                "RALENTIZADO: "
                    + String.format(
                        Locale.US,
                        "%.1f",
                        caballero.getTiempoLentitud()
                    )
                    + " s",
                15, 285
            );

            font.setColor(Color.WHITE);
        }

        if (jefeGigante != null
                && jefeGigante.estaActivo()) {

            dibujarHUDJefe();
        }
    }

    // =====================================
    // HUD DASH, PARRY, Z Y X
    // =====================================

    private void dibujarHUDHabilidades() {

        dibujarBarraHabilidad(
            "DASH [SHIFT]",
            10, 425,
            caballero.getTiempoRecargaDash(),
            caballero.getRecargaDashActual(),
            COLOR_DASH
        );

        dibujarBarraHabilidad(
            "PARRY [ESPACIO]",
            10, 385,
            caballero.getTiempoRecargaParry(),
            1f,
            COLOR_PARRY
        );

        // Las habilidades poderosas aparecen
        // unicamente cuando estan desbloqueadas.
        if (escudoDesbloqueado) {

            String titulo = tiempoEscudo > 0f
                ? "ESCUDO [Z] ACTIVO"
                : "ESCUDO [Z]";

            dibujarBarraHabilidad(
                titulo,
                10, 345,
                recargaEscudo,
                RECARGA_ESCUDO,
                COLOR_ESCUDO
            );
        }

        if (ralentizacionDesbloqueada) {

            String titulo = tiempoRalentizacion > 0f
                ? "TIEMPO [X] ACTIVO"
                : "TIEMPO [X]";

            dibujarBarraHabilidad(
                titulo,
                10, 305,
                recargaRalentizacion,
                RECARGA_RALENTIZACION,
                COLOR_TIEMPO
            );
        }
    }

    // =====================================
    // BARRA REUTILIZABLE
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
            1f - recargaRestante
                / Math.max(0.01f, recargaMaxima),
            0f, 1f
        );

        fontHabilidades.setColor(Color.WHITE);

        fontHabilidades.draw(
            batch,
            nombre,
            x, y + 15f
        );

        batch.setColor(COLOR_FONDO_BARRA);

        batch.draw(
            texturaBarra,
            x, y - 7f,
            ancho, alto
        );

        batch.setColor(color);

        batch.draw(
            texturaBarra,
            x, y - 7f,
            ancho * progreso,
            alto
        );

        batch.setColor(Color.WHITE);

        String estado = recargaRestante <= 0f
            ? "LISTO"
            : String.format(
                Locale.US,
                "%.1f s",
                recargaRestante
            );

        fontHabilidades.draw(
            batch,
            estado,
            x + ancho + 8f,
            y + 1f
        );
    }

    // =====================================
    // HUD DEL GIGANTE
    // =====================================

    private void dibujarHUDJefe() {

        float x = 240f;
        float y = 292f;
        float ancho = 320f;
        float alto = 15f;

        font.setColor(Color.GOLD);

        font.draw(
            batch,
            "GIGANTE DEL ASEDIO - "
                + jefeGigante.getVidaActual()
                + "/"
                + jefeGigante.getVidaMaxima(),
            260f, 323f
        );

        batch.setColor(
            0.12f, 0.09f, 0.09f, 0.90f
        );

        batch.draw(
            texturaBarra,
            x, y, ancho, alto
        );

        batch.setColor(
            jefeGigante.estaEnFaseDos()
                ? Color.ORANGE
                : Color.RED
        );

        batch.draw(
            texturaBarra,
            x, y,
            ancho * jefeGigante.getPorcentajeVida(),
            alto
        );

        batch.setColor(Color.WHITE);
        font.setColor(Color.WHITE);
    }

    // =====================================
    // COLOCACION DEL GUARDIAN
    // =====================================

    private void dibujarIndicacionColocacion() {

        batch.setColor(
            0f, 0f, 0f, 0.80f
        );

        batch.draw(
            texturaBarra,
            85f, 345f,
            630f, 67f
        );

        batch.setColor(Color.WHITE);
        font.setColor(Color.GOLD);

        font.draw(
            batch,
            "COLOCA TU GUARDIAN EXTRA",
            245f, 393f
        );

        font.setColor(Color.WHITE);

        font.draw(
            batch,
            "Haz clic dentro del patio para fijar su posicion",
            176f, 367f
        );
    }

    // =====================================
    // DESCANSOS DEL TUTORIAL
    // =====================================

    private void dibujarDescanso() {

        batch.setColor(0f, 0f, 0f, 0.75f);

        batch.draw(
            texturaBarra,
            0, 145,
            800, 190
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
            210, 295
        );

        font.getData().setScale(1.1f);

        if (gestorPartida.getRondaSuperada() == 3) {

            font.draw(
                batch,
                "Mejora seleccionada. Continua el asedio.",
                185, 255
            );

        } else {

            font.draw(
                batch,
                "Castillo: +4 vidas (maximo 5)",
                250, 255
            );
        }

        font.draw(
            batch,
            "Siguiente ronda: "
                + gestorPartida.getSiguienteRonda(),
            285, 220
        );

        font.draw(
            batch,
            "Comienza en "
                + gestorPartida.getTiempoDescanso()
                + "...",
            320, 185
        );

        font.getData().setScale(
            escalaX, escalaY
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
    public void hide() {
        lluvia.pausar();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void dispose() {

        if (destruida) {
            return;
        }

        destruida = true;

        texturaFondo.dispose();
        texturaBarra.dispose();

        fontHabilidades.dispose();
        murallaDanioVisual.destruir();

        texturaArquero.dispose();
        texturaCanon.dispose();

        canonEnemigo.destruir();
        volcanEnemigo.destruir();

        piedrasGigantes.clear();

        texturaPiedraGigante.dispose();
        texturaGigante.dispose();

        if (jefeGigante != null) {
            jefeGigante.destruir();
        }

        guardianExtra.destruir();
        texturaGuardian.dispose();

        caballero.destruir();
        lluvia.destruir();
    }
}
