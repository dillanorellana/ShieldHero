
package com.shieldhero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class VictoriaScreen implements Screen {

    private final ShieldHeroGame game;
    private final int puntosFinales;

    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;

    public VictoriaScreen(ShieldHeroGame game, int puntosFinales) {
        this.game = game;
        this.puntosFinales = puntosFinales;

        batch = game.getBatch();
        font = game.getFont();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(0.05f, 0.18f, 0.12f, 1);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        font.getData().setScale(2f);

        font.draw(batch, "VICTORIA", 290, 350);

        font.getData().setScale(1.3f);

        font.draw(batch, "Has defendido el castillo", 230, 270);
        font.draw(batch, "Puntos: " + puntosFinales, 320, 220);
        font.draw(batch, "Record: " + game.getHigherScore(), 320, 185);

        font.draw(batch, "ENTER o clic para jugar de nuevo", 200, 100);

        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
                || Gdx.input.justTouched()) {

            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
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
        font.getData().setScale(1f);
    }
}
