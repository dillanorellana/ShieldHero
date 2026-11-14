# 🛡️ ShieldHero

**ShieldHero** es un videojuego de defensa de un castillo desarrollado en Java utilizando el framework **LibGDX** y su backend de escritorio **LWJGL3**.

El jugador controla a un caballero cuya misión es proteger el castillo de diferentes amenazas y proyectiles enemigos mediante movimientos defensivos y habilidades especiales.

El objetivo es sobrevivir a las cinco rondas del juego y conseguir la victoria.

---

# 1. Requisitos previos

Antes de ejecutar el proyecto, es necesario instalar y configurar las siguientes herramientas.

| Herramienta | Versión requerida |
|---|---|
| Java Development Kit (JDK) | **Java 11** |
| Gradle | 8.10.1 (incluido mediante Gradle Wrapper) |
| Eclipse IDE | Eclipse IDE for Java Developers |
| Gradle Buildship | Integración de Gradle para Eclipse |
| Sistema operativo | Windows, Linux o macOS |
| Conexión a Internet | Necesaria para descargar dependencias inicialmente |

### 1.1. Instalación de Java 11

ShieldHero utiliza Java 11 como versión de compilación y ejecución.

Se recomienda instalar una distribución de OpenJDK 11 de 64 bits, como Eclipse Temurin 11.

Descarga:

https://adoptium.net/temurin/releases/?version=11

Durante la instalación, se recomienda habilitar las opciones de configuración de `JAVA_HOME` y actualización del `PATH`, si el instalador las ofrece.

Para comprobar que Java está correctamente instalado, abrir una terminal y ejecutar:

```bash
java -version
```

También comprobar el compilador:

```bash
javac -version
```

Ambos comandos deben indicar la versión 11.

**Importante:** Tener instalado únicamente Java 17, 21 o una versión superior no garantiza el funcionamiento del proyecto. La configuración de Gradle utiliza explícitamente un Java Toolchain de versión 11.

### 1.2. Instalación de Eclipse

Descargar e instalar:

https://www.eclipse.org/downloads/

Se recomienda utilizar la distribución **Eclipse IDE for Java Developers**.

Además, es necesario contar con la integración Gradle Buildship.

Si no está instalada:

1. Abrir Eclipse.
2. Acceder a `Help → Eclipse Marketplace`.
3. Buscar `Buildship Gradle Integration`.
4. Instalar el complemento.
5. Reiniciar Eclipse si es necesario.

### 1.3. Gradle

No es necesario instalar Gradle manualmente.

El proyecto incluye Gradle Wrapper, que descarga y utiliza automáticamente Gradle 8.10.1.

Esto permite que todos los usuarios trabajen con la misma versión de Gradle, evitando problemas de compatibilidad.

---

# 2. Descargar y preparar el proyecto

1. Descargar el proyecto desde el repositorio de GitHub mediante la opción `Code → Download ZIP`, o clonarlo utilizando Git.
2. Extraer el archivo ZIP en una carpeta del computador.
3. Localizar la carpeta raíz del proyecto, denominada `ShieldHero-main` o similar.
4. Comprobar que dentro de esta carpeta se encuentren los siguientes elementos:

```text
ShieldHero/
│
├── assets/
│
├── core/
│
├── lwjgl3/
│
├── gradle/
│
├── build.gradle
├── settings.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
│
└── README.md
```

**Importante:** Para importar o ejecutar el proyecto se debe seleccionar la carpeta raíz, donde se encuentra `settings.gradle`, y no las carpetas `core` o `lwjgl3` individualmente.

---

# 3. Configurar y ejecutar desde Eclipse

Este es el procedimiento recomendado para trabajar con el código fuente del videojuego.

### 3.1. Configurar el JDK en Eclipse

Antes de importar el proyecto, comprobar que Eclipse reconoce Java 11.

1. Abrir Eclipse.
2. Acceder a `Window → Preferences`.
3. Entrar en `Java → Installed JREs`.
4. Presionar `Add`.
5. Seleccionar `Standard VM`.
6. Buscar la carpeta donde se encuentra instalado el JDK 11.
7. Agregarlo y marcarlo como JDK disponible.

También es importante configurar Java 11 para Gradle:

1. Acceder a `Window → Preferences → Gradle`.
2. Comprobar la configuración de Java Home o JVM de Gradle, según la versión de Buildship.
3. Seleccionar el JDK 11 instalado.

Esto permite que Gradle utilice la versión de Java esperada por el proyecto.

### 3.2. Importar el proyecto

1. Abrir Eclipse.
2. Seleccionar `File → Import`.
3. Buscar la categoría `Gradle`.
4. Seleccionar `Existing Gradle Project`.
5. Presionar `Next`.
6. En `Project root directory`, seleccionar la carpeta raíz de ShieldHero.
7. Presionar `Finish`.

Eclipse reconocerá automáticamente los módulos del proyecto:

- `core`: contiene la lógica principal del videojuego.
- `lwjgl3`: contiene la configuración y el punto de entrada de la aplicación de escritorio.

Esperar a que Gradle termine de sincronizar y descargar las dependencias.

La primera importación puede tardar algunos minutos.

### 3.3. Ejecutar el videojuego

La forma recomendada de iniciar ShieldHero desde Eclipse es mediante Gradle.

1. Acceder a `Window → Show View → Other`.
2. Buscar la categoría `Gradle`.
3. Abrir la vista `Gradle Tasks`.
4. Localizar el proyecto `lwjgl3`.
5. Buscar la tarea `run`.
6. Hacer doble clic sobre ella.

Gradle se encargará de compilar el proyecto, configurar las dependencias y ejecutar la aplicación.

Una vez finalizado el proceso, se abrirá la ventana del videojuego.

**Nota:** Se recomienda utilizar esta modalidad en lugar de ejecutar directamente `Lwjgl3Launcher.java`, ya que la tarea Gradle configura correctamente el directorio de trabajo y los recursos necesarios para el videojuego.

---

# 4. Ejecutar desde consola

También es posible ejecutar ShieldHero sin abrir Eclipse.

Para ello, se utiliza Gradle Wrapper, incluido en el proyecto.

### 4.1. Abrir la terminal

1. Abrir la carpeta raíz de ShieldHero.
2. Hacer clic en la barra de direcciones del explorador de archivos.
3. Escribir `cmd` o abrir PowerShell.
4. Comprobar que la terminal se encuentre en la carpeta donde está ubicado `gradlew.bat`.

### 4.2. Ejecutar el videojuego en Windows

Ejecutar:

```powershell
.\gradlew.bat lwjgl3:run
```

El comando realizará automáticamente las siguientes operaciones:

1. Inicializar Gradle 8.10.1.
2. Verificar el JDK 11 necesario para el proyecto.
3. Descargar las dependencias de LibGDX y LWJGL3, si todavía no están disponibles.
4. Compilar los módulos `core` y `lwjgl3`.
5. Preparar los recursos gráficos y de audio.
6. Iniciar el videojuego.

### 4.3. Ejecutar en Linux o macOS

Desde la carpeta raíz del proyecto, ejecutar:

```bash
./gradlew lwjgl3:run
```

Si es necesario otorgar permisos de ejecución al archivo:

```bash
chmod +x gradlew
```

Después, volver a ejecutar el comando anterior.

### 4.4. Compilar el proyecto sin iniciar el videojuego

Si solamente se desea comprobar que el proyecto compila correctamente, ejecutar en Windows:

```powershell
.\gradlew.bat build
```

En Linux o macOS:

```bash
./gradlew build
```

### 4.5. Generar un archivo JAR ejecutable

El proyecto también permite generar un archivo JAR que contiene la aplicación y sus dependencias.

En Windows:

```powershell
.\gradlew.bat :lwjgl3:jar
```

El archivo generado se encontrará en:

```text
lwjgl3/build/libs/ShieldHero-1.0.0.jar
```

Una vez generado, puede ejecutarse mediante:

```powershell
java -jar lwjgl3/build/libs/ShieldHero-1.0.0.jar
```

Este método permite iniciar el videojuego sin necesidad de abrir Eclipse ni ejecutar Gradle nuevamente, siempre que se cuente con un entorno Java compatible.

---

# 5. Dependencias utilizadas

ShieldHero utiliza las siguientes tecnologías y librerías:

| Tecnología | Versión | Función |
|---|---|---|
| Java | 11 | Lenguaje de programación y plataforma de ejecución |
| LibGDX | 1.12.1 | Framework principal del videojuego |
| LWJGL3 | 1.12.1 (backend LibGDX) | Ejecución gráfica de escritorio |
| Gradle | 8.10.1 | Compilación y administración de dependencias |
| Gradle Wrapper | 8.10.1 | Administración automática de Gradle |

Las dependencias externas se descargan automáticamente desde los repositorios configurados en los archivos Gradle del proyecto.

No es necesario descargar manualmente los archivos JAR de LibGDX o LWJGL3.

---

# 6. Estructura del proyecto

El proyecto está organizado en módulos y directorios que separan la lógica del videojuego, la ejecución de escritorio y los recursos.

### core/

Contiene la lógica principal de ShieldHero.

Incluye:

- Control del caballero.
- Gestión de enemigos.
- Sistema de proyectiles.
- Mecánicas de movimiento.
- Colisiones.
- Habilidades defensivas.
- Gestión de rondas.
- Pantallas y estados del videojuego.

### lwjgl3/

Contiene los componentes necesarios para ejecutar ShieldHero como una aplicación de escritorio.

Incluye:

- `Lwjgl3Launcher.java`: punto de entrada de la aplicación.
- `StartupHelper.java`: asistencia para la inicialización de la aplicación.
- Configuración de LWJGL3.
- Recursos necesarios para la ventana del videojuego.

### assets/

Contiene los recursos utilizados durante la ejecución:

- Imágenes.
- Texturas.
- Personajes.
- Proyectiles.
- Efectos de sonido.
- Música.
- Recursos gráficos adicionales.

**Importante:** No eliminar ni cambiar la ubicación de esta carpeta, ya que el videojuego depende de sus recursos para funcionar correctamente.

### gradle/

Contiene los archivos utilizados por Gradle Wrapper para descargar y ejecutar la versión configurada de Gradle.

---

# 7. Solución de problemas frecuentes

### Error: No se encuentra Java 11

**Posible causa:** El JDK 11 no está instalado o Gradle no puede localizarlo.

**Solución:**

1. Comprobar que JDK 11 esté instalado.
2. Verificar `JAVA_HOME`.
3. Comprobar que Eclipse tenga registrado el JDK 11.
4. Configurar Gradle para utilizar ese JDK.
5. Volver a ejecutar el proyecto.

### Error: Gradle no puede descargar dependencias

**Posible causa:** No existe conexión a Internet o los repositorios no están disponibles.

**Solución:**

1. Comprobar la conexión a Internet.
2. Verificar que no exista un bloqueo del firewall o proxy.
3. Volver a ejecutar el comando de Gradle.

### Eclipse no reconoce el proyecto

**Posible causa:** Se seleccionó una carpeta incorrecta durante la importación.

**Solución:**

Comprobar que la carpeta seleccionada contenga:

```text
settings.gradle
build.gradle
gradlew.bat
```

Después, volver a importar utilizando `Existing Gradle Project`.

### Error de sincronización de Gradle en Eclipse

**Solución:**

1. Hacer clic derecho sobre el proyecto.
2. Seleccionar `Gradle → Refresh Gradle Project`.
3. Esperar a que termine la sincronización.
4. Intentar ejecutar nuevamente la tarea `run`.

### El videojuego no encuentra imágenes o sonidos

**Posible causa:** El directorio de trabajo no está configurado correctamente.

**Solución:**

Se recomienda ejecutar el videojuego mediante:

```powershell
.\gradlew.bat lwjgl3:run
```

Esta tarea configura el directorio de trabajo para utilizar correctamente los recursos ubicados en `assets/`.

### El videojuego no inicia después de compilar

Comprobar que:

- El JDK 11 esté disponible.
- Las dependencias se hayan descargado correctamente.
- La estructura original del proyecto se haya conservado.
- La carpeta `assets/` se encuentre en la raíz.
- La ejecución se realice mediante la tarea Gradle correspondiente.

---

# 8. Controles del videojuego

| Tecla | Acción |
|---|---|
| A / Flecha izquierda | Movimiento hacia la izquierda |
| D / Flecha derecha | Movimiento hacia la derecha |
| Espacio | Parry |
| Shift + dirección | Dash |
| Enter | Confirmar e iniciar |
| I | Acceder a las instrucciones |
| Esc | Volver o salir de determinadas pantallas |

---

# 9. Consideraciones finales

ShieldHero utiliza Gradle como sistema de compilación y administración de dependencias, permitiendo ejecutar el proyecto tanto desde Eclipse como desde una terminal.

Para garantizar el correcto funcionamiento del videojuego, se recomienda:

- Utilizar JDK 11.
- Mantener la estructura original de directorios.
- No eliminar los recursos de `assets/`.
- Utilizar Gradle Wrapper en lugar de instalar versiones externas de Gradle.
- Ejecutar el videojuego mediante la tarea `lwjgl3:run`.

Una vez realizada la configuración inicial, no será necesario repetir el proceso de importación ni descargar manualmente las dependencias cada vez que se desee iniciar ShieldHero.

**¡Gracias por jugar ShieldHero!**
