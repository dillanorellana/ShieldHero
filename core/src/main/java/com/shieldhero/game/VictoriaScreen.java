package com.shieldhero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class VictoriaScreen implements Screen {

    private final ShieldHeroGame game;
    private final GameScreen partida;
    private final int puntosFinales;

    private final SpriteBatch batch;
    private final BitmapFont font;
    private final OrthographicCamera camera;

    public VictoriaScreen(
            ShieldHeroGame game,
            GameScreen partida,
            int puntosFinales) {

        this.game = game;
        this.partida = partida;
        this.puntosFinales = puntosFinales;

        batch = game.getBatch();
        font = game.getFont();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
    }

    @Override
    public void render(float delta) {

        // ENTER: continuar la misma partida
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

            GestorPartida.getInstancia().iniciarSupervivencia();

            game.setScreen(partida);
            dispose();
            return;
        }

        // ESC: terminar y volver al menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {

            game.setScreen(new MainMenuScreen(game));

            // Ya no necesitamos la partida anterior
            partida.dispose();

            dispose();
            return;
        }

        ScreenUtils.clear(0.04f, 0.09f, 0.07f, 1f);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        float escalaX = font.getData().scaleX;
        float escalaY = font.getData().scaleY;

        font.setColor(Color.GOLD);
        font.getData().setScale(2.4f);

        font.draw(batch, "VICTORIA", 260, 405);

        font.getData().setScale(1.3f);
        font.setColor(Color.WHITE);

        font.draw(batch, "HAS DEFENDIDO EL CASTILLO", 185, 335);

        font.setColor(Color.GOLD);
        font.draw(batch, "5 RONDAS COMPLETADAS", 225, 290);

        font.setColor(Color.WHITE);
        font.draw(batch, "Puntuacion: " + puntosFinales, 295, 245);
        font.draw(batch, "Record: " + game.getHigherScore(), 295, 215);

        font.setColor(Color.CYAN);
        font.draw(batch, "MODO SUPERVIVENCIA DESBLOQUEADO", 165, 165);

        font.setColor(Color.GREEN);
        font.draw(batch, "[ENTER] SEGUIR DEFENDIENDO", 205, 110);

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "[ESC] TERMINAR PARTIDA", 245, 70);

        font.getData().setScale(escalaX, escalaY);
        font.setColor(Color.WHITE);

        batch.end();
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
    }

    @Override
    public void dispose() {
        // La fuente y el batch pertenecen a ShieldHeroGame.
    }
}