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

    // NUEVO ESCENARIO DE ASEDIO
    private static final float LIMITE_IZQUIERDO = 30f;
    private static final float LIMITE_DERECHO = 427f;

    private static final float ANCHO_PROYECTIL = 32f;

    // Margen para mantener los ataques dentro del patio.
    private static final float MARGEN_SEGURIDAD = 12f;

    private static final float ALTURA_APARICION = 480f;

    private Array<Proyectil> proyectiles;
    private long ultimoProyectil;

    // Posicion horizontal del ultimo proyectil generado.
    private float ultimaPosicionX = -1f;

    private Texture texturaFlecha;
    private Texture texturaPiedra;
    private Texture texturaFuego;

    private Sound sonidoBloqueo;
    private Music musica;

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

    // Intervalo configurado para la ronda actual.
    private float obtenerIntervaloAparicion() {

        return GestorPartida.getInstancia()
                .getIntervaloAparicionActual();
    }

    // Elegir una posicion dentro del patio defendible.
    private float calcularPosicionX(
            ConfiguracionRonda configuracion) {

        float minimoPermitido =
            LIMITE_IZQUIERDO + MARGEN_SEGURIDAD;

        float maximoPermitido =
            LIMITE_DERECHO
            - ANCHO_PROYECTIL
            - MARGEN_SEGURIDAD;

        // Primer proyectil: posicion aleatoria.
        if (ultimaPosicionX < 0f) {

            ultimaPosicionX = MathUtils.random(
                minimoPermitido,
                maximoPermitido
            );

            return ultimaPosicionX;
        }

        // Distancia maxima definida por Builder.
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

        // Elegir dentro del rango alcanzable.
        float nuevaPosicion = MathUtils.random(
            minimo,
            maximo
        );

        ultimaPosicionX = nuevaPosicion;

        return nuevaPosicion;
    }

    // Seleccionar proyectil segun la ronda.
    private int seleccionarTipoProyectil(int ronda) {

        int probabilidad = MathUtils.random(0, 99);

        switch (ronda) {

            case 1:
                // 100% flechas.
                return 1;

            case 2:
                // 65% flechas, 35% piedras.
                return probabilidad < 65 ? 1 : 2;

            case 3:
                // 45% flechas, 35% piedras, 20% fuego.
                if (probabilidad < 45) {
                    return 1;
                } else if (probabilidad < 80) {
                    return 2;
                }
                return 3;

            case 4:
                // 35% flechas, 35% piedras, 30% fuego.
                if (probabilidad < 35) {
                    return 1;
                } else if (probabilidad < 70) {
                    return 2;
                }
                return 3;

            case 5:
            default:
                // 30% flechas, 35% piedras, 35% fuego.
                if (probabilidad < 30) {
                    return 1;
                } else if (probabilidad < 65) {
                    return 2;
                }
                return 3;
        }
    }

    // Generar proyectiles segun la ronda.
    private void crearProyectil() {

        ConfiguracionRonda configuracion =
            GestorPartida.getInstancia()
                .getConfiguracionActual();

        float x = calcularPosicionX(configuracion);
        float y = ALTURA_APARICION;

        int tipo = seleccionarTipoProyectil(
            configuracion.getNumero()
        );

        Proyectil nuevo;

        switch (tipo) {

            case 1:
                nuevo = new Flecha(
                    x,
                    y,
                    texturaFlecha
                );
                break;

            case 2:
                nuevo = new Piedra(
                    x,
                    y,
                    texturaPiedra
                );
                break;

            default:
                nuevo = new BolaFuego(
                    x,
                    y,
                    texturaFuego
                );
                break;
        }

        // STRATEGY: movimiento segun la ronda.
        float probabilidadDiagonal =
            configuracion.getProbabilidadDiagonal();

        if (MathUtils.randomBoolean(
                probabilidadDiagonal)) {

            // Seleccionar direccion considerando
            // el espacio disponible en el patio.
            float centro =
                (LIMITE_IZQUIERDO + LIMITE_DERECHO) / 2f;

            float direccion;

            if (x < centro) {

                // Desde la izquierda, dirigir hacia el centro.
                direccion = 1f;

            } else {

                // Desde la derecha, dirigir hacia el centro.
                direccion = -1f;
            }

            nuevo.setEstrategiaMovimiento(
                new MovimientoDiagonal(direccion)
            );

        } else {

            nuevo.setEstrategiaMovimiento(
                new MovimientoVertical()
            );
        }

        proyectiles.add(nuevo);

        ultimoProyectil = TimeUtils.nanoTime();
    }

    // Actualizar proyectiles y colisiones.
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

            // TEMPLATE METHOD.
            boolean eliminar = proyectil.procesar(
                delta,
                caballero,
                castillo
            );

            if (eliminar) {

                // Sonido solo al bloquear.
                if (proyectil.colisionaCon(
                        caballero.getArea())) {

                    sonidoBloqueo.play();
                }

                proyectiles.removeIndex(i);
                i--;
            }
        }

        return !castillo.estaDestruido();
    }

    // Dibujar proyectiles.
    public void actualizarDibujoLluvia(
            SpriteBatch batch) {

        for (Proyectil proyectil : proyectiles) {
            proyectil.dibujar(batch);
        }
    }

    // Limpiar proyectiles durante descansos.
    public void limpiarProyectiles() {

        if (proyectiles != null) {
            proyectiles.clear();
        }
    }

    // Reiniciar tiempo al comenzar otra ronda.
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
