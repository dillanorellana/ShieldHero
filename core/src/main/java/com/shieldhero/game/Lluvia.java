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

    // Posicion horizontal del ultimo proyectil generado
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

    // Obtener el intervalo de aparicion de la ronda actual
    private float obtenerIntervaloAparicion() {

        return GestorPartida.getInstancia()
                .getIntervaloAparicionActual();
    }

    // Calcular una posicion horizontal segun la dificultad
    private float calcularPosicionX(
            ConfiguracionRonda configuracion) {

        float anchoEscenario = 800f;
        float anchoProyectil = 64f;

        float limiteDerecho =
            anchoEscenario - anchoProyectil;

        // Primer proyectil: posicion completamente aleatoria
        if (ultimaPosicionX < 0f) {

            ultimaPosicionX = MathUtils.random(
                0f,
                limiteDerecho
            );

            return ultimaPosicionX;
        }

        // Distancia maxima definida por el Builder
        float distancia =
            configuracion.getDistanciaMaxima();

        // Calcular los limites respecto al ultimo proyectil
        float minimo = Math.max(
            0f,
            ultimaPosicionX - distancia
        );

        float maximo = Math.min(
            limiteDerecho,
            ultimaPosicionX + distancia
        );

        // Elegir una posicion dentro del rango permitido
        float nuevaPosicion = MathUtils.random(
            minimo,
            maximo
        );

        ultimaPosicionX = nuevaPosicion;

        return nuevaPosicion;
    }
    private int seleccionarTipoProyectil(int ronda) {

        // Numero aleatorio entre 0 y 99
        int probabilidad = MathUtils.random(0, 99);

        switch (ronda) {

            case 1:
                // 100% flechas
                return 1;

            case 2:
                // 65% flechas, 35% piedras
                return probabilidad < 65 ? 1 : 2;

            case 3:
                // 45% flechas, 35% piedras, 20% fuego
                if (probabilidad < 45) {
                    return 1;
                } else if (probabilidad < 80) {
                    return 2;
                }
                return 3;

            case 4:
                // 35% flechas, 35% piedras, 30% fuego
                if (probabilidad < 35) {
                    return 1;
                } else if (probabilidad < 70) {
                    return 2;
                }
                return 3;

            case 5:
                // 30% flechas, 35% piedras, 35% fuego
                if (probabilidad < 30) {
                    return 1;
                } else if (probabilidad < 65) {
                    return 2;
                }
                return 3;

            default:
                return 1;
        }
    }

    // Generar proyectiles segun la configuracion de la ronda
    private void crearProyectil() {

        ConfiguracionRonda configuracion =
            GestorPartida.getInstancia()
                .getConfiguracionActual();

        // Posicion de aparicion controlada
        float x = calcularPosicionX(configuracion);
        float y = 480f;

        // Tipos disponibles segun la ronda
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

        // Patron Strategy: movimiento segun la ronda
        float probabilidadDiagonal =
            configuracion.getProbabilidadDiagonal();

        if (MathUtils.randomBoolean(probabilidadDiagonal)) {

            float direccion = MathUtils.randomBoolean()
                ? 1f
                : -1f;

            nuevo.setEstrategiaMovimiento(
                new MovimientoDiagonal(direccion)
            );

        } else {

            nuevo.setEstrategiaMovimiento(
                new MovimientoVertical()
            );
        }

        // Agregar el proyectil
        proyectiles.add(nuevo);

        // Registrar el momento de aparicion
        ultimoProyectil = TimeUtils.nanoTime();
    }

    // Actualizar proyectiles, movimientos y colisiones
    public boolean actualizarMovimiento(
            Caballero caballero,
            Castillo castillo) {

        float intervalo = obtenerIntervaloAparicion();

        long intervaloNanos =
            (long) (intervalo * 1000000000L);

        // Generar proyectil cuando corresponda
        if (TimeUtils.nanoTime() - ultimoProyectil
                > intervaloNanos) {

            crearProyectil();
        }

        float delta = Gdx.graphics.getDeltaTime();

        for (int i = 0; i < proyectiles.size; i++) {

            Proyectil proyectil = proyectiles.get(i);

            // Patron Template Method
            boolean eliminar = proyectil.procesar(
                delta,
                caballero,
                castillo
            );

            if (eliminar) {

                // Sonido solamente al bloquear
                if (proyectil.colisionaCon(caballero.getArea())) {
                    sonidoBloqueo.play();
                }

                proyectiles.removeIndex(i);
                i--;
            }
        }

        return !castillo.estaDestruido();
    }

    // Dibujar todos los proyectiles
    public void actualizarDibujoLluvia(SpriteBatch batch) {

        for (Proyectil proyectil : proyectiles) {
            proyectil.dibujar(batch);
        }
    }

    // Eliminar proyectiles al superar una ronda
    public void limpiarProyectiles() {

        if (proyectiles != null) {
            proyectiles.clear();
        }
    }

    // Reiniciar la aparicion al comenzar una nueva ronda
    public void reiniciarTiempoAparicion() {

        ultimoProyectil = TimeUtils.nanoTime();

        // La nueva ronda empieza sin restricciones
        // respecto a la posicion de la ronda anterior
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