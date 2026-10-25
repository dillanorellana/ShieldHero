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

    private GuardianExtra guardianExtra;

    private boolean escudoTemporalActivo;
    private boolean ralentizacionActiva;

    private static final float FACTOR_RALENTIZACION = 0.50f;

    public Lluvia(
            Texture flecha,
            Texture piedra,
            Texture fuego,
            Sound sonido,
            Music musica) {

        texturaFlecha = flecha;
        texturaPiedra = piedra;
        texturaFuego = fuego;
        sonidoBloqueo = sonido;
        this.musica = musica;

        guardianExtra = null;
        escudoTemporalActivo = false;
        ralentizacionActiva = false;
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

        arqueroEnemigo1 = arquero1;
        arqueroEnemigo2 = arquero2;
    }

    public void setCanon(CanonEnemigo canon) {
        canonEnemigo = canon;
    }

    public void setVolcan(VolcanEnemigo volcan) {
        volcanEnemigo = volcan;
    }

    public void setGuardianExtra(GuardianExtra guardian) {
        guardianExtra = guardian;
    }

    // =====================================
    // HABILIDADES PODEROSAS
    // =====================================

    public void setEscudoTemporalActivo(boolean activo) {
        escudoTemporalActivo = activo;
    }

    public void setRalentizacionActiva(boolean activa) {
        ralentizacionActiva = activa;
    }

    private float obtenerIntervaloAparicion() {

        return GestorPartida.getInstancia()
            .getIntervaloAparicionActual();
    }

    // =====================================
    // POSICION DEL ATAQUE
    // =====================================

    private float calcularPosicionX(
            ConfiguracionRonda configuracion) {

        float minimoPermitido = 42f;
        float maximoPermitido = 383f;

        if (ultimaPosicionX < 0f) {

            ultimaPosicionX = MathUtils.random(
                minimoPermitido,
                maximoPermitido
            );

            return ultimaPosicionX;
        }

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

    // =====================================
    // PROBABILIDADES ORIGINALES
    // =====================================

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

    // =====================================
    // CREAR PROYECTIL
    // =====================================

    private void crearProyectil() {

        ConfiguracionRonda configuracion =
            GestorPartida.getInstancia()
                .getConfiguracionActual();

        int tipo = seleccionarTipoProyectil(
            configuracion.getNumero()
        );

        float destinoX = calcularPosicionX(
            configuracion
        );

        Proyectil nuevo;

        if (tipo == 1) {

            ArqueroEnemigo arqueroSeleccionado =
                MathUtils.randomBoolean()
                    ? arqueroEnemigo1
                    : arqueroEnemigo2;

            float origenX = 720f;
            float origenY = 170f;

            if (arqueroSeleccionado != null) {

                origenX =
                    arqueroSeleccionado == arqueroEnemigo1
                        ? 690f : 748f;

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

            float origenX = 620f;
            float origenY = 165f;

            if (canonEnemigo != null) {

                origenX =
                    canonEnemigo.getCentroX() - 18f;

                origenY =
                    canonEnemigo.getCentroY() + 8f;

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

    // =====================================
    // ACTUALIZAR MOVIMIENTO
    // =====================================

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

        // La ralentizacion reduce la velocidad
        // de avance del proyectil, no la frecuencia
        // de aparicion.
        float deltaMovimiento = ralentizacionActiva
            ? delta * FACTOR_RALENTIZACION
            : delta;

        for (int i = 0; i < proyectiles.size; i++) {

            Proyectil proyectil = proyectiles.get(i);

            boolean golpeaCaballeroAntes =
                proyectil.colisionaCon(
                    caballero.getArea()
                );

            boolean eliminar = proyectil.procesar(
                deltaMovimiento,
                caballero,
                castillo,
                guardianExtra,
                escudoTemporalActivo
            );

            if (eliminar) {

                boolean bloqueoGuardian =
                    guardianExtra != null
                    && guardianExtra.estaColocado()
                    && guardianExtra.protege(
                        proyectil.getArea()
                    );

                if (escudoTemporalActivo
                        || bloqueoGuardian
                        || golpeaCaballeroAntes
                        || proyectil.colisionaCon(
                            caballero.getArea()
                        )) {

                    sonidoBloqueo.play();
                }

                proyectiles.removeIndex(i);
                i--;
            }
        }

        return !castillo.estaDestruido();
    }

    // =====================================
    // DIBUJAR PROYECTILES
    // =====================================

    public void actualizarDibujoLluvia(
            SpriteBatch batch) {

        for (Proyectil proyectil : proyectiles) {
            proyectil.dibujar(batch);
        }
    }

    // =====================================
    // LIMPIEZA DE ATAQUES
    // =====================================

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
