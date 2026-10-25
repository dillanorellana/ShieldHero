
# 🛡️ ShieldHero

**ShieldHero** es un videojuego de defensa de un castillo desarrollado en **Java** utilizando el framework **LibGDX**.

El jugador controla a un caballero cuya misión es proteger el castillo de los proyectiles enemigos, utilizando movimientos defensivos y habilidades especiales.

El objetivo es sobrevivir y superar las cinco rondas del juego.

---

## 1. Requisitos previos

Para ejecutar ShieldHero desde su código fuente, es necesario contar con:

- **Eclipse IDE**, con soporte para Java.
- **JDK 17**, versión recomendada para el proyecto.
- **Gradle Buildship**, integración de Gradle para Eclipse.
- **Conexión a Internet**, necesaria durante la primera configuración.

**Nota:** No es necesario instalar Gradle por separado, ya que el proyecto incluye Gradle Wrapper.

---

## 2. Descargar el proyecto

1. Acceder al repositorio de ShieldHero en GitHub.
2. Presionar el botón **Code**.
3. Seleccionar **Download ZIP**.
4. Extraer el archivo descargado en una carpeta del computador.
5. Localizar la carpeta raíz del proyecto.

La carpeta raíz contiene archivos y directorios como:

- `core/`
- `lwjgl3/`
- `assets/`
- `gradle/`
- `build.gradle`
- `settings.gradle`
- `gradlew.bat`

---

## 3. Importar el proyecto en Eclipse

1. Abrir **Eclipse IDE**.
2. En la barra superior, seleccionar **File → Import**.
3. Buscar y desplegar la categoría **Gradle**.
4. Seleccionar **Existing Gradle Project**.
5. Presionar **Next**.
6. En **Project root directory**, seleccionar la carpeta raíz de ShieldHero.
7. Presionar **Finish** para comenzar la importación.
8. Esperar a que Gradle descargue las dependencias y configure el proyecto.

Una vez finalizada la importación, Eclipse mostrará los módulos correspondientes al videojuego.

**Importante:** Si Eclipse solicita seleccionar una versión de Java, utilizar JDK 17.

---

## 4. Ejecutar ShieldHero

### Método recomendado: Gradle

1. En Eclipse, seleccionar **Window → Show View → Other**.
2. Buscar la categoría **Gradle**.
3. Seleccionar **Gradle Tasks**.
4. Localizar el módulo `lwjgl3`.
5. Buscar la tarea `run`.
6. Ejecutar dicha tarea.

Al finalizar la inicialización, se abrirá la ventana del videojuego.

### Método alternativo: terminal

También es posible ejecutar el juego mediante Gradle Wrapper.

1. Abrir una terminal en la carpeta raíz del proyecto.
2. En Windows, ejecutar el siguiente comando:

    .\gradlew.bat lwjgl3:run

3. Esperar a que Gradle compile e inicialice el videojuego.

**Nota:** La primera ejecución puede tardar más debido a la descarga de dependencias.

### Método alternativo: clase principal

El proyecto contiene una clase encargada de iniciar la aplicación de escritorio:

`lwjgl3/src/main/java/com/shieldhero/game/lwjgl3/Lwjgl3Launcher.java`

También puede ejecutarse mediante **Run As → Java Application**.

En este caso, es necesario configurar correctamente el directorio de trabajo (`Working Directory`) para que el programa encuentre los recursos de `assets/`.

Por este motivo, se recomienda utilizar la tarea Gradle `run`.

---

## 5. Controles del videojuego

| Tecla | Acción |
|---|---|
| A / D | Mover al caballero |
| Flechas izquierda / derecha | Movimiento horizontal |
| Espacio | Realizar Parry |
| Shift + dirección | Realizar Dash |
| Enter | Comenzar desde la pantalla de instrucciones |
| Esc | Regresar desde la pantalla de instrucciones |

### Objetivo

El jugador debe defender el castillo utilizando las habilidades del caballero.

Durante la partida se presentan proyectiles enemigos que deben bloquearse o evitarse.

El juego dispone de cinco rondas que deben superarse para conseguir la victoria.

---

## 6. Estructura del proyecto

El videojuego se organiza en diferentes módulos:

### core/

Contiene la lógica principal del videojuego:

- Mecánicas de movimiento.
- Gestión de personajes.
- Colisiones.
- Control de proyectiles.
- Pantallas y estados del juego.
- Reglas de funcionamiento.

### lwjgl3/

Contiene los componentes necesarios para iniciar el videojuego como aplicación de escritorio.

Incluye la clase `Lwjgl3Launcher.java` y las configuraciones de ejecución.

### assets/

Almacena los recursos utilizados por el videojuego:

- Texturas e imágenes.
- Archivos de audio.
- Recursos gráficos adicionales.

### gradle/

Contiene los archivos necesarios para utilizar Gradle Wrapper y administrar la compilación del proyecto.

---

## 7. Solución de problemas frecuentes

### Eclipse no reconoce Gradle

Comprobar que Eclipse tenga instalada la integración **Buildship Gradle Integration**.

Si no está disponible, instalarla desde Eclipse Marketplace.

### Error al importar el proyecto

Verificar que se haya seleccionado la carpeta raíz correcta.

Esta debe contener el archivo `settings.gradle`.

### Problemas con las dependencias

1. Comprobar la conexión a Internet.
2. Hacer clic derecho sobre el proyecto en Eclipse.
3. Seleccionar **Gradle → Refresh Gradle Project**.
4. Esperar a que termine la sincronización.

### El juego no encuentra imágenes o sonidos

Se recomienda iniciar el videojuego mediante la tarea Gradle `lwjgl3:run`, ya que esta configura el directorio de trabajo necesario para acceder a `assets/`.

### Error de versión de Java

1. Abrir **Window → Preferences**.
2. Seleccionar **Java → Installed JREs**.
3. Comprobar que JDK 17 esté instalado y configurado.
4. Verificar también que Gradle utilice una versión compatible de Java.

---

## 8. Tecnologías utilizadas

El videojuego fue desarrollado utilizando:

- **Java:** lenguaje de programación.
- **LibGDX 1.12.1:** framework de desarrollo de videojuegos.
- **LWJGL3:** backend de ejecución de escritorio.
- **Gradle:** sistema de automatización y gestión de dependencias.
- **Eclipse IDE:** entorno de desarrollo.

---

## 9. Consideraciones finales

ShieldHero puede ejecutarse directamente desde Eclipse mediante la importación de su proyecto Gradle.

Una vez completada la configuración inicial, no es necesario repetir el proceso de importación para iniciar el videojuego nuevamente.

Para garantizar el correcto funcionamiento del programa, se recomienda conservar la estructura original de carpetas y utilizar la tarea Gradle `run` para su ejecución.
