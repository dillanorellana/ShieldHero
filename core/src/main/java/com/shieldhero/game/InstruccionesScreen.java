package com.shieldhero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class InstruccionesScreen implements Screen {

    private final ShieldHeroGame game;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final OrthographicCamera camera;

    public InstruccionesScreen(ShieldHeroGame game) {
        this.game = game;
        this.batch = game.getBatch();
        this.font = game.getFont();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
    }

    @Override
    public void render(float delta) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game));
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
        font.getData().setScale(2f);
        font.draw(batch, "INSTRUCCIONES", 180, 435);

        font.getData().setScale(1.15f);
        font.setColor(Color.WHITE);

        font.draw(batch, "OBJETIVO", 65, 365);
        font.draw(batch, "Defiende el castillo de los ataques enemigos.", 65, 340);
        font.draw(batch, "Supera las 5 rondas para conseguir la victoria.", 65, 315);

        font.setColor(Color.GOLD);
        font.draw(batch, "CONTROLES", 65, 270);

        font.setColor(Color.WHITE);
        font.draw(batch, "A / D o FLECHAS: Mover al caballero", 65, 245);
        font.draw(batch, "ESPACIO: Parry (duplica los puntos)", 65, 220);
        font.draw(batch, "SHIFT + DIRECCION: Dash", 65, 195);
        font.draw(batch, "Bloquea los proyectiles para ganar puntos.", 65, 170);

        font.setColor(Color.GOLD);
        font.draw(batch, "PROYECTILES", 65, 130);

        font.setColor(Color.WHITE);
        font.draw(batch, "Flecha: 1 dano | Piedra: 2 danos", 65, 105);
        font.draw(batch, "Bola de fuego: 2 danos", 65, 80);

        font.setColor(Color.CYAN);
        font.draw(batch, "ENTER: JUGAR     |     ESC: VOLVER", 200, 35);

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
        // El batch y la fuente pertenecen a ShieldHeroGame.
    }
}