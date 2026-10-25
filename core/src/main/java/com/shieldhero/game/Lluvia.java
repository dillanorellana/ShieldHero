package com.shieldhero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Lluvia {

    private Array<Proyectil> proyectiles;
    private long ultimoProyectil;

    private float ultimaPosicionX = -1f;

    private Texture texturaFlecha;
    private Texture texturaPiedra;
    private Texture texturaFuego;

    private Sound sonidoBloqueo;
    private Music musica;

    private ArqueroEnemigo arqueroEnemigo1;
    private ArqueroEnemigo arqueroEnemigo2;
    private CanonEnemigo canonEnemigo;
    private VolcanEnemigo volcanEnemigo;

    public Lluvia(
            Texture flecha,
            Texture piedra,
            Texture fuego,
            Sound sonido,
            Music musica) {

        this.texturaFlecha = flecha;
        this.texturaPiedra = piedra;
        this.texturaFuego = fuego;
        this.sonidoBloqueo = sonido;
        this.musica = musica;
    }

    public void crear() {

        proyectiles = new Array<Proyectil>();
        ultimaPosicionX = -1f;

        crearProyectil();

        musica.setLooping(true);
        musica.play();
    }

    public void setArqueros(
            ArqueroEnemigo arquero1,
            ArqueroEnemigo arquero2) {

        this.arqueroEnemigo1 = arquero1;
        this.arqueroEnemigo2 = arquero2;
    }

    public void setCanon(CanonEnemigo canonEnemigo) {
        this.canonEnemigo = canonEnemigo;
    }

    public void setVolcan(VolcanEnemigo volcanEnemigo) {
        this.volcanEnemigo = volcanEnemigo;
    }

    private float obtenerIntervaloAparicion() {
        return GestorPartida.getInstancia()
                .getIntervaloAparicionActual();
    }


private float calcularPosicionX(
        ConfiguracionRonda configuracion) {

    // Zona del patio que el caballero puede defender.
    float minimoPermitido = 42f;
    float maximoPermitido = 383f;

    // Primer ataque de la ronda.
    if (ultimaPosicionX < 0f) {

        ultimaPosicionX = MathUtils.random(
            minimoPermitido,
            maximoPermitido
        );

        return ultimaPosicionX;
    }

    // Respetar la distancia maxima del Builder.
    float distancia =
        configuracion.getDistanciaMaxima();

    float minimo = Math.max(
        minimoPermitido,
        ultimaPosicionX - distancia
    );

    float maximo = Math.min(
        maximoPermitido,
        ultimaPosicionX + distancia
    );

    float nuevaPosicion = MathUtils.random(
        minimo,
        maximo
    );

    ultimaPosicionX = nuevaPosicion;

    return nuevaPosicion;
}


    private int seleccionarTipoProyectil(int ronda) {

        int probabilidad = MathUtils.random(0, 99);

        switch (ronda) {

            case 1:
                return 1;

            case 2:
                return probabilidad < 65 ? 1 : 2;

            case 3:
                if (probabilidad < 45) {
                    return 1;
                } else if (probabilidad < 80) {
                    return 2;
                }
                return 3;

            case 4:
                if (probabilidad < 35) {
                    return 1;
                } else if (probabilidad < 70) {
                    return 2;
                }
                return 3;

            case 5:
                if (probabilidad < 30) {
                    return 1;
                } else if (probabilidad < 65) {
                    return 2;
                }
                return 3;

            default:
                if (probabilidad < 30) {
                    return 1;
                } else if (probabilidad < 60) {
                    return 2;
                }
                return 3;
        }
    }

    private void crearProyectil() {

        ConfiguracionRonda configuracion =
            GestorPartida.getInstancia()
                .getConfiguracionActual();

        int tipo = seleccionarTipoProyectil(
            configuracion.getNumero()
        );

        Proyectil nuevo;

        // Punto donde caerá o intentará llegar
        float destinoX = calcularPosicionX(configuracion);

        if (tipo == 1) {
            // FLECHAS: salen desde uno de los arqueros
            ArqueroEnemigo arqueroSeleccionado =
                MathUtils.randomBoolean()
                    ? arqueroEnemigo1
                    : arqueroEnemigo2;

            float origenX = 720f;
            float origenY = 170f;

            if (arqueroSeleccionado != null) {

                // Mantener los puntos de disparo originales.
                if (arqueroSeleccionado == arqueroEnemigo1) {
                    origenX = 690f;
                } else {
                    origenX = 748f;
                }

                origenY = 165f;

                arqueroSeleccionado.disparar();
            }

            nuevo = new Flecha(
                origenX,
                origenY,
                texturaFlecha
            );

            nuevo.setEstrategiaMovimiento(
                new MovimientoParabolicoArquero(
                    origenX,
                    origenY,
                    destinoX,
                    0f
                )
            );

        } else if (tipo == 2) {
            // PIEDRAS: salen desde el cañón
            float origenX = 620f;
            float origenY = 165f;

            if (canonEnemigo != null) {
                origenX = canonEnemigo.getCentroX() - 18f;
                origenY = canonEnemigo.getCentroY() + 8f;
                canonEnemigo.disparar();
            }

            nuevo = new Piedra(
                origenX,
                origenY,
                texturaPiedra
            );

            nuevo.setEstrategiaMovimiento(
                new MovimientoParabolicoCanon(
                    origenX,
                    origenY,
                    destinoX,
                    0f
                )
            );

        } else {
            // BOLAS DE FUEGO: salen desde el volcán
            float origenX = 635f;
            float origenY = 340f;

            if (volcanEnemigo != null) {
                origenX = volcanEnemigo.getCraterX();
                origenY = volcanEnemigo.getCraterY();
                volcanEnemigo.erupcionar();
            }

            nuevo = new BolaFuego(
                origenX,
                origenY,
                texturaFuego
            );

            nuevo.setEstrategiaMovimiento(
                new MovimientoParabolicoVolcan(
                    origenX,
                    origenY,
                    destinoX,
                    0f
                )
            );
        }

        proyectiles.add(nuevo);
        ultimoProyectil = TimeUtils.nanoTime();
    }

    public boolean actualizarMovimiento(
            Caballero caballero,
            Castillo castillo) {

        float intervalo = obtenerIntervaloAparicion();
        long intervaloNanos =
            (long) (intervalo * 1000000000L);

        if (TimeUtils.nanoTime() - ultimoProyectil
                > intervaloNanos) {

            crearProyectil();
        }

        float delta = Gdx.graphics.getDeltaTime();

        for (int i = 0; i < proyectiles.size; i++) {

            Proyectil proyectil = proyectiles.get(i);

            boolean golpeaCaballeroAntes =
                proyectil.colisionaCon(caballero.getArea());

            boolean eliminar = proyectil.procesar(
                delta,
                caballero,
                castillo
            );

            if (eliminar) {

                if (golpeaCaballeroAntes
                        || proyectil.colisionaCon(
                            caballero.getArea())) {
                    sonidoBloqueo.play();
                }

                proyectiles.removeIndex(i);
                i--;
            }
        }

        return !castillo.estaDestruido();
    }

    public void actualizarDibujoLluvia(SpriteBatch batch) {

        for (Proyectil proyectil : proyectiles) {
            proyectil.dibujar(batch);
        }
    }

    public void limpiarProyectiles() {

        if (proyectiles != null) {
            proyectiles.clear();
        }
    }

    public void reiniciarTiempoAparicion() {
        ultimoProyectil = TimeUtils.nanoTime();
        ultimaPosicionX = -1f;
    }

    public void pausar() {
        musica.pause();
    }

    public void continuar() {
        musica.play();
    }

    public void destruir() {

        texturaFlecha.dispose();
        texturaPiedra.dispose();
        texturaFuego.dispose();

        sonidoBloqueo.dispose();
        musica.dispose();
    }
}