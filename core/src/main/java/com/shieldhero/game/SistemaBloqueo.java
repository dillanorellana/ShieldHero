package com.shieldhero.game;

public class SistemaBloqueo {

    public static void procesarBloqueo(
            Bloqueable objeto,
            Caballero caballero) {

        objeto.alSerBloqueado(caballero);
    }
}
