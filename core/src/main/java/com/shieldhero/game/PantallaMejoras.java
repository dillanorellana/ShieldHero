
package com.shieldhero.game;

import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class PantallaMejoras implements Screen {

    public enum TipoMejora {
        PARRY,
        DASH,
        REPARACION,
        ESCUDO_TEMPORAL,
        RALENTIZACION,
        GUARDIAN_EXTRA
    }

    private final ShieldHeroGame game;
    private final Consumer<TipoMejora> alElegir;
    private final boolean especial;
    private final int numeroRonda;

    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final BitmapFont fuenteGrande;

    private Texture pixel;
    private boolean elegida;
    private boolean destruida;
    private TipoMejora seleccion;

    private static final float[] CARTA_X = {
        75f, 302f, 529f
    };

    private static final float CARTA_Y = 125f;
    private static final float CARTA_ANCHO = 195f;
    private static final float CARTA_ALTO = 225f;

    public PantallaMejoras(
            ShieldHeroGame game,
            Consumer<TipoMejora> alElegir) {

        this(game, false, 3, alElegir);
    }

    public PantallaMejoras(
            ShieldHeroGame game,
            boolean especial,
            int numeroRonda,
            Consumer<TipoMejora> alElegir) {

        this.game = game;
        this.especial = especial;
        this.numeroRonda = numeroRonda;
        this.alElegir = alElegir;

        batch = game.getBatch();
        font = game.getFont();
        fuenteGrande = new BitmapFont();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        Pixmap pixmap = new Pixmap(
            1, 1, Pixmap.Format.RGBA8888
        );
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        pixel = new Texture(pixmap);
        pixmap.dispose();

        elegida = false;
        destruida = false;
        seleccion = null;
    }

    @Override
    public void render(float delta) {

        procesarEntrada();

        if (destruida) {
            return;
        }

        ScreenUtils.clear(
            0.05f, 0.07f, 0.11f, 1f
        );

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (elegida) {
            dibujarConfirmacion();
        } else {
            dibujarSeleccion();
        }

        batch.end();
    }

    private void dibujarSeleccion() {

        texto(
            especial
                ? "ELIGE UNA MEJORA PODEROSA"
                : "ELIGE UNA MEJORA",
            especial ? 205f : 273f,
            427f,
            Color.GOLD,
            1.35f
        );

        texto(
            "RONDA " + numeroRonda,
            349f, 389f,
            Color.WHITE, 1f
        );

        for (int i = 0; i < 3; i++) {
            dibujarCarta(i);
        }

        texto(
            "Presiona 1, 2 o 3 / Haz clic en una carta",
            210f, 67f,
            Color.LIGHT_GRAY, 0.90f
        );
    }

    private void dibujarCarta(int indice) {

        float x = CARTA_X[indice];

        batch.setColor(
            especial
                ? new Color(0.24f, 0.18f, 0.11f, 1f)
                : new Color(0.12f, 0.20f, 0.28f, 1f)
        );

        batch.draw(
            pixel, x, CARTA_Y,
            CARTA_ANCHO, CARTA_ALTO
        );

        batch.setColor(
            especial ? Color.GOLD : Color.SKY
        );

        borde(
            x, CARTA_Y,
            CARTA_ANCHO, CARTA_ALTO,
            3f
        );

        texto(
            "[" + (indice + 1) + "]",
            x + 84f,
            CARTA_Y + 196f,
            Color.WHITE,
            1.15f
        );

        if (especial) {

            if (indice == 0) {

                texto(
                    "ESCUDO TEMPORAL",
                    x + 14f, CARTA_Y + 155f,
                    Color.GOLD, 0.83f
                );

                texto(
                    "TECLA [Z]",
                    x + 45f, CARTA_Y + 120f,
                    Color.CYAN, 1.04f
                );

                texto(
                    "Protege por 5 s",
                    x + 19f, CARTA_Y + 85f,
                    Color.WHITE, 0.89f
                );

                texto(
                    "Recarga: 25 s",
                    x + 27f, CARTA_Y + 55f,
                    Color.WHITE, 0.87f
                );

            } else if (indice == 1) {

                texto(
                    "RALENTIZACION",
                    x + 20f, CARTA_Y + 155f,
                    Color.GOLD, 0.87f
                );

                texto(
                    "TECLA [X]",
                    x + 45f, CARTA_Y + 120f,
                    Color.CYAN, 1.04f
                );

                texto(
                    "Proyectiles lentos",
                    x + 13f, CARTA_Y + 85f,
                    Color.WHITE, 0.85f
                );

                texto(
                    "8 s / Recarga 30 s",
                    x + 11f, CARTA_Y + 55f,
                    Color.WHITE, 0.81f
                );

            } else {

                texto(
                    "GUARDIAN EXTRA",
                    x + 21f, CARTA_Y + 155f,
                    Color.GOLD, 0.84f
                );

                texto(
                    "SIN TECLA",
                    x + 42f, CARTA_Y + 120f,
                    Color.CYAN, 1.02f
                );

                texto(
                    "Coloca con clic",
                    x + 25f, CARTA_Y + 85f,
                    Color.WHITE, 0.88f
                );

                texto(
                    "Mejora su aura",
                    x + 23f, CARTA_Y + 55f,
                    Color.WHITE, 0.87f
                );
            }

        } else {

            if (indice == 0) {

                texto(
                    "PARRY",
                    x + 59f, CARTA_Y + 156f,
                    Color.SKY, 1.05f
                );

                texto(
                    "TECLA [ESPACIO]",
                    x + 14f, CARTA_Y + 120f,
                    Color.WHITE, 0.87f
                );

                texto(
                    "+0.10 s de duracion",
                    x + 13f, CARTA_Y + 80f,
                    Color.WHITE, 0.84f
                );

            } else if (indice == 1) {

                texto(
                    "DASH",
                    x + 65f, CARTA_Y + 156f,
                    Color.SKY, 1.05f
                );

                texto(
                    "SHIFT + A / D",
                    x + 27f, CARTA_Y + 120f,
                    Color.WHITE, 0.90f
                );

                texto(
                    "-0.25 s de recarga",
                    x + 14f, CARTA_Y + 80f,
                    Color.WHITE, 0.84f
                );

            } else {

                texto(
                    "REPARACION",
                    x + 28f, CARTA_Y + 156f,
                    Color.SKY, 0.97f
                );

                texto(
                    "AUTOMATICA",
                    x + 39f, CARTA_Y + 120f,
                    Color.WHITE, 0.88f
                );

                texto(
                    "+4 vidas al castillo",
                    x + 14f, CARTA_Y + 80f,
                    Color.WHITE, 0.84f
                );
            }
        }
    }

    private void dibujarConfirmacion() {

        String titulo = "";
        String instruccion = "";
        String descripcion = "";

        switch (seleccion) {

            case ESCUDO_TEMPORAL:
                titulo = "ESCUDO DESBLOQUEADO";
                instruccion = "PRESIONA Z";
                descripcion = "5 segundos de proteccion";
                break;

            case RALENTIZACION:
                titulo = "RALENTIZACION DESBLOQUEADA";
                instruccion = "PRESIONA X";
                descripcion = "Ralentiza proyectiles durante 8 s";
                break;

            case GUARDIAN_EXTRA:
                titulo = "GUARDIAN EXTRA";
                instruccion = "SIN TECLA";
                descripcion = "Colocalo con un clic o mejora su aura";
                break;

            case PARRY:
                titulo = "PARRY MEJORADO";
                instruccion = "PRESIONA ESPACIO";
                descripcion = "Mayor duracion del parry";
                break;

            case DASH:
                titulo = "DASH MEJORADO";
                instruccion = "SHIFT + A / D";
                descripcion = "Menor tiempo de recarga";
                break;

            case REPARACION:
                titulo = "CASTILLO REPARADO";
                instruccion = "AUTOMATICO";
                descripcion = "Se recuperan hasta 4 vidas";
                break;

            default:
                break;
        }

        texto(
            titulo,
            185f, 367f,
            Color.GOLD, 1.22f
        );

        texto(
            instruccion,
            269f, 268f,
            Color.CYAN, 1.45f
        );

        texto(
            descripcion,
            194f, 206f,
            Color.WHITE, 0.96f
        );

        texto(
            "Presiona ENTER o haz clic para continuar",
            194f, 105f,
            Color.LIGHT_GRAY, 0.90f
        );
    }

    private void procesarEntrada() {

        if (elegida) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
                    || Gdx.input.isKeyJustPressed(
                        Input.Keys.SPACE
                    )
                    || Gdx.input.justTouched()) {

                alElegir.accept(seleccion);
            }

            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)
                || Gdx.input.isKeyJustPressed(
                    Input.Keys.NUMPAD_1
                )) {

            elegir(0);
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)
                || Gdx.input.isKeyJustPressed(
                    Input.Keys.NUMPAD_2
                )) {

            elegir(1);
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)
                || Gdx.input.isKeyJustPressed(
                    Input.Keys.NUMPAD_3
                )) {

            elegir(2);
            return;
        }

        if (Gdx.input.justTouched()) {

            Vector3 posicion = new Vector3(
                Gdx.input.getX(),
                Gdx.input.getY(),
                0f
            );

            camera.unproject(posicion);

            for (int i = 0; i < 3; i++) {

                if (posicion.x >= CARTA_X[i]
                        && posicion.x <=
                            CARTA_X[i] + CARTA_ANCHO
                        && posicion.y >= CARTA_Y
                        && posicion.y <=
                            CARTA_Y + CARTA_ALTO) {

                    elegir(i);
                    return;
                }
            }
        }
    }

    private void elegir(int indice) {

        if (elegida) {
            return;
        }

        if (especial) {

            seleccion = indice == 0
                ? TipoMejora.ESCUDO_TEMPORAL
                : indice == 1
                    ? TipoMejora.RALENTIZACION
                    : TipoMejora.GUARDIAN_EXTRA;

        } else {

            seleccion = indice == 0
                ? TipoMejora.PARRY
                : indice == 1
                    ? TipoMejora.DASH
                    : TipoMejora.REPARACION;
        }

        elegida = true;
    }

    private void borde(
            float x, float y,
            float ancho, float alto,
            float grosor) {

        batch.draw(pixel, x, y, ancho, grosor);

        batch.draw(
            pixel, x, y + alto - grosor,
            ancho, grosor
        );

        batch.draw(pixel, x, y, grosor, alto);

        batch.draw(
            pixel, x + ancho - grosor, y,
            grosor, alto
        );
    }

    private void texto(
            String mensaje,
            float x,
            float y,
            Color color,
            float escala) {

        BitmapFont fuente = fuenteGrande;

        fuente.getData().setScale(escala);
        fuente.setColor(color);
        fuente.draw(batch, mensaje, x, y);

        fuente.getData().setScale(1f);
        fuente.setColor(Color.WHITE);
    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {

        if (destruida) {
            return;
        }

        destruida = true;

        pixel.dispose();
        fuenteGrande.dispose();
    }
}
