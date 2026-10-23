
package com.shieldhero.game;

public class GestorPartida {

    // Unica instancia del gestor
    private static final GestorPartida instancia =
            new GestorPartida();

    private int record;
    private int rondaActual;

    // Constructor privado: impide crear objetos desde fuera
    private GestorPartida() {
        record = 0;
        rondaActual = 1;
    }

    // Punto de acceso global a la unica instancia
    public static GestorPartida getInstancia() {
        return instancia;
    }

    public int getRecord() {
        return record;
    }

    public void actualizarRecord(int puntos) {
        if (puntos > record) {
            record = puntos;
        }
    }

    public int getRondaActual() {
        return rondaActual;
    }

    public void avanzarRonda() {
        if (rondaActual < 5) {
            rondaActual++;
        }
    }

    public void reiniciarRondas() {
        rondaActual = 1;
    }
}
