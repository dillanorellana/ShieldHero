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

    // LIMITES DEL PATIO
    private static final float LIMITE_IZQUIERDO = 30f;
    private static final float LIMITE_DERECHO = 427f;

    private static final float ANCHO_PROYECTIL = 32f;
    private static final float MARGEN_SEGURIDAD = 12f;

    private static final float ALTURA_APARICION = 480f;
    private static final float ALTURA_SUELO = 120f;

    // ORIGEN DE LAS FLECHAS
    private static final float FLECHA_ARQUERO_1_X = 690f;
    private static final float FLECHA_ARQUERO_2_X = 748f;
    private static final float ALTURA_ARCO = 165f;

    // BOCA DEL CANON
    private static final float CANON_ORIGEN_X = 594f;
    private static final float CANON_ORIGEN_Y = 176f;

    private Array<Proyectil> proyectiles;
    private long ultimoProyectil;

    private float ultimaPosicionX = -1f;

    private Texture texturaFlecha;
    private Texture texturaPiedra;
    private Texture texturaFuego;

    private Sound sonidoBloqueo;
    private Music musica;

    // ARQUEROS
    private ArqueroEnemigo arqueroEnemigo1;
    private ArqueroEnemigo arqueroEnemigo2;

    // NUEVO: CANON CON ANIMACION
    private CanonEnemigo canonEnemigo;

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

    // Conectar los arqueros.
    public void setArqueros(
            ArqueroEnemigo arquero1,
            ArqueroEnemigo arquero2) {

        this.arqueroEnemigo1 = arquero1;
        this.arqueroEnemigo2 = arquero2;
    }

    // NUEVO: conectar el canon.
    public void setCanon(CanonEnemigo canon) {
        this.canonEnemigo = canon;
    }

    public void crear() {

        proyectiles = new Array<Proyectil>();

        ultimaPosicionX = -1f;

        crearProyectil();

        musica.setLooping(true);
        musica.play();
    }

    // INTERVALO DE APARICION
    private float obtenerIntervaloAparicion() {

        return GestorPartida.getInstancia()
            .getIntervaloAparicionActual();
    }

    // POSICION DE IMPACTO EN EL PATIO
    private float calcularPosicionX(
            ConfiguracionRonda configuracion) {

        float minimoPermitido =
            LIMITE_IZQUIERDO + MARGEN_SEGURIDAD;

        float maximoPermitido =
            LIMITE_DERECHO
            - ANCHO_PROYECTIL
            - MARGEN_SEGURIDAD;

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

        float nuevaPosicion = MathUtils.random(
            minimo,
            maximo
        );

        ultimaPosicionX = nuevaPosicion;

        return nuevaPosicion;
    }

    // SELECCIONAR EL TIPO DE PROYECTIL
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

    // ELEGIR ARQUERO Y ACTIVAR ANIMACION
    private float seleccionarOrigenFlecha() {

        if (arqueroEnemigo1 == null
                && arqueroEnemigo2 == null) {

            return FLECHA_ARQUERO_1_X;
        }

        if (arqueroEnemigo1 == null) {

            arqueroEnemigo2.disparar();
            return FLECHA_ARQUERO_2_X;
        }

        if (arqueroEnemigo2 == null) {

            arqueroEnemigo1.disparar();
            return FLECHA_ARQUERO_1_X;
        }

        if (MathUtils.randomBoolean()) {

            arqueroEnemigo1.disparar();
            return FLECHA_ARQUERO_1_X;

        } else {

            arqueroEnemigo2.disparar();
            return FLECHA_ARQUERO_2_X;
        }
    }

    // CREAR PROYECTILES
    private void crearProyectil() {

        ConfiguracionRonda configuracion =
            GestorPartida.getInstancia()
                .getConfiguracionActual();

        float x = calcularPosicionX(configuracion);

        int tipo = seleccionarTipoProyectil(
            configuracion.getNumero()
        );

        Proyectil nuevo;

        switch (tipo) {

            case 1:

                // FLECHA PARABOLICA
                float origenFlechaX =
                    seleccionarOrigenFlecha();

                float origenFlechaY = ALTURA_ARCO;

                nuevo = new Flecha(
                    origenFlechaX,
                    origenFlechaY,
                    texturaFlecha
                );

                nuevo.setEstrategiaMovimiento(
                    new MovimientoParabolicoArquero(
                        origenFlechaX,
                        origenFlechaY,
                        x,
                        ALTURA_SUELO - 1f
                    )
                );

                break;

            case 2:

                // PIEDRA PARABOLICA DEL CANON
                nuevo = new Piedra(
                    CANON_ORIGEN_X,
                    CANON_ORIGEN_Y,
                    texturaPiedra
                );

                nuevo.setEstrategiaMovimiento(
                    new MovimientoParabolicoCanon(
                        CANON_ORIGEN_X,
                        CANON_ORIGEN_Y,
                        x,
                        ALTURA_SUELO - 1f
                    )
                );

                // NUEVO: activar retroceso al disparar.
                if (canonEnemigo != null) {
                    canonEnemigo.disparar();
                }

                break;

            default:

                // BOLA DE FUEGO: SIN CAMBIOS
                nuevo = new BolaFuego(
                    x,
                    ALTURA_APARICION,
                    texturaFuego
                );

                configurarMovimientoActual(
                    nuevo,
                    x,
                    configuracion
                );

                break;
        }

        proyectiles.add(nuevo);

        ultimoProyectil = TimeUtils.nanoTime();
    }

    // MOVIMIENTO ACTUAL DEL FUEGO
    private void configurarMovimientoActual(
            Proyectil proyectil,
            float x,
            ConfiguracionRonda configuracion) {

        float probabilidadDiagonal =
            configuracion.getProbabilidadDiagonal();

        if (MathUtils.randomBoolean(
                probabilidadDiagonal)) {

            float centro =
                (LIMITE_IZQUIERDO + LIMITE_DERECHO)
                / 2f;

            float direccion;

            if (x < centro) {
                direccion = 1f;
            } else {
                direccion = -1f;
            }

            proyectil.setEstrategiaMovimiento(
                new MovimientoDiagonal(direccion)
            );

        } else {

            proyectil.setEstrategiaMovimiento(
                new MovimientoVertical()
            );
        }
    }

    // ACTUALIZAR PROYECTILES Y COLISIONES
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

            // TEMPLATE METHOD
            boolean eliminar = proyectil.procesar(
                delta,
                caballero,
                castillo
            );

            if (eliminar) {

                // Sonido al bloquear.
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

    // DIBUJAR PROYECTILES
    public void actualizarDibujoLluvia(
            SpriteBatch batch) {

        for (Proyectil proyectil : proyectiles) {
            proyectil.dibujar(batch);
        }
    }

    // LIMPIAR PROYECTILES
    public void limpiarProyectiles() {

        if (proyectiles != null) {
            proyectiles.clear();
        }
    }

    // REINICIAR APARICION
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
