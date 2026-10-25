
package com.shieldhero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class MurallaDanioVisual {

    private static final int ANCHO_ESCENARIO = 800;
    private static final int ALTO_ESCENARIO = 480;

    private final Texture[] estadosDanio;

    public MurallaDanioVisual() {

        estadosDanio = new Texture[6];

        for (int vidas = 0; vidas <= 5; vidas++) {

            String ruta = "escenarios/muralla_"
                + vidas + ".png";

            if (!Gdx.files.internal(ruta).exists()) {
                throw new IllegalStateException(
                    "Falta la imagen de la muralla: " + ruta
                );
            }

            estadosDanio[vidas] = new Texture(
                Gdx.files.internal(ruta)
            );

            estadosDanio[vidas].setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
            );
        }
    }

    public void dibujar(
            SpriteBatch batch,
            int vidasCastillo) {

        int estado = MathUtils.clamp(
            vidasCastillo,
            0,
            5
        );

        batch.setColor(1f, 1f, 1f, 1f);

        batch.draw(
            estadosDanio[estado],
            0f,
            0f,
            ANCHO_ESCENARIO,
            ALTO_ESCENARIO
        );
    }

    public void destruir() {

        for (Texture textura : estadosDanio) {

            if (textura != null) {
                textura.dispose();
            }
        }
    }
}
