package com.shieldhero.game;

public class ConfiguracionRonda {

    private final int numero;
    private final int puntosObjetivo;
    private final float intervaloAparicion;
    private final int tiposProyectiles;
    private final float probabilidadDiagonal;
    private final float distanciaMaxima;

    private ConfiguracionRonda(Builder builder) {
        this.numero = builder.numero;
        this.puntosObjetivo = builder.puntosObjetivo;
        this.intervaloAparicion = builder.intervaloAparicion;
        this.tiposProyectiles = builder.tiposProyectiles;
        this.probabilidadDiagonal = builder.probabilidadDiagonal;
        this.distanciaMaxima = builder.distanciaMaxima;
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

    public int getTiposProyectiles() {
        return tiposProyectiles;
    }

    public float getProbabilidadDiagonal() {
        return probabilidadDiagonal;
    }

    public float getDistanciaMaxima() {
        return distanciaMaxima;
    }

    // Patron Builder
    public static class Builder {

        private int numero;
        private int puntosObjetivo;
        private float intervaloAparicion;

        private int tiposProyectiles = 1;
        private float probabilidadDiagonal = 0f;
        private float distanciaMaxima = 736f;

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

        public Builder tiposProyectiles(int tiposProyectiles) {
            this.tiposProyectiles = tiposProyectiles;
            return this;
        }

        public Builder probabilidadDiagonal(float probabilidadDiagonal) {
            this.probabilidadDiagonal = probabilidadDiagonal;
            return this;
        }

        public Builder distanciaMaxima(float distanciaMaxima) {
            this.distanciaMaxima = distanciaMaxima;
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

            if (intervaloAparicion <= 0f) {
                throw new IllegalArgumentException(
                    "El intervalo debe ser mayor que cero"
                );
            }

            if (tiposProyectiles < 1 || tiposProyectiles > 3) {
                throw new IllegalArgumentException(
                    "Los tipos de proyectiles deben estar entre 1 y 3"
                );
            }

            if (probabilidadDiagonal < 0f
                    || probabilidadDiagonal > 1f) {

                throw new IllegalArgumentException(
                    "La probabilidad diagonal debe estar entre 0 y 1"
                );
            }

            if (distanciaMaxima <= 0f || distanciaMaxima > 736f) {
                throw new IllegalArgumentException(
                    "La distancia maxima debe estar entre 0 y 736"
                );
            }

            return new ConfiguracionRonda(this);
        }
    }
}