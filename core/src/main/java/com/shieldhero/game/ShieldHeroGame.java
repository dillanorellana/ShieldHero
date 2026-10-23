
package com.shieldhero.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ShieldHeroGame extends Game {

    private SpriteBatch batch;
    private BitmapFont font;

    @Override
    public void create() {

        batch = new SpriteBatch();
        font = new BitmapFont();

        // Reiniciar las rondas al iniciar el juego
        GestorPartida.getInstancia().reiniciarRondas();

        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {

        if (getScreen() != null) {
            getScreen().dispose();
        }

        batch.dispose();
        font.dispose();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public BitmapFont getFont() {
        return font;
    }

    // El record ahora pertenece al Singleton
    public int getHigherScore() {
        return GestorPartida.getInstancia().getRecord();
    }

    public void setHigherScore(int puntos) {
        GestorPartida.getInstancia().actualizarRecord(puntos);
    }
}
