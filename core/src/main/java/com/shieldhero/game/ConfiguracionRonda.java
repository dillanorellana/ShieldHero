package com.shieldhero.game;

/**
 * Configuracion de una ronda.
 *
 * Permite construir una ronda indicando sus parametros.
 */
public class ConfiguracionRonda {

    private final int numero;
    private final int puntosObjetivo;
    private final float intervaloAparicion;

    private ConfiguracionRonda(Builder builder) {
        this.numero = builder.numero;
        this.puntosObjetivo = builder.puntosObjetivo;
        this.intervaloAparicion = builder.intervaloAparicion;
    }

    public int getNumero() {
        return numero;
    }

    public int getPuntosObjetivo() {
        return puntosObjetivo;
    }

    public float getIntervaloAparicion() {
        return intervaloAparicion;
    }

    public static class Builder {

        private int numero;
        private int puntosObjetivo;
        private float intervaloAparicion;

        public Builder numero(int numero) {
            this.numero = numero;
            return this;
        }

        public Builder puntosObjetivo(int puntosObjetivo) {
            this.puntosObjetivo = puntosObjetivo;
            return this;
        }

        public Builder intervaloAparicion(float intervaloAparicion) {
            this.intervaloAparicion = intervaloAparicion;
            return this;
        }

        public ConfiguracionRonda construir() {

            if (numero < 1) {
                throw new IllegalArgumentException(
                    "El numero de ronda debe ser positivo"
                );
            }

            if (puntosObjetivo <= 0) {
                throw new IllegalArgumentException(
                    "El objetivo de puntos debe ser positivo"
                );
            }

            if (intervaloAparicion <= 0) {
                throw new IllegalArgumentException(
                    "El intervalo debe ser mayor que cero"
                );
            }

            return new ConfiguracionRonda(this);
        }
    }
}
