package com.shieldhero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {

    private final ShieldHeroGame game;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final OrthographicCamera camera;

    public MainMenuScreen(final ShieldHeroGame game) {

        this.game = game;
        this.batch = game.getBatch();
        this.font = game.getFont();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
    }

    @Override
    public void render(float delta) {

        // Iniciar partida
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game));
            dispose();
            return;
        }

        // Abrir instrucciones
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            game.setScreen(new InstruccionesScreen(game));
            dispose();
            return;
        }

        ScreenUtils.clear(0.05f, 0.07f, 0.13f, 1);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        float escalaX = font.getData().scaleX;
        float escalaY = font.getData().scaleY;

        font.setColor(Color.GOLD);
        font.getData().setScale(2.5f);
        font.draw(batch, "SHIELD HERO", 180, 345);

        font.setColor(Color.WHITE);
        font.getData().setScale(1.6f);
        font.draw(batch, "CASTLE DEFENSE", 205, 285);

        font.setColor(Color.GREEN);
        font.getData().setScale(1.4f);
        font.draw(batch, "[ENTER] JUGAR", 260, 190);

        font.setColor(Color.CYAN);
        font.draw(batch, "[I] INSTRUCCIONES", 220, 135);

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
        // Los recursos compartidos pertenecen a ShieldHeroGame.
    }
}