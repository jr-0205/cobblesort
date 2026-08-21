# CobbleSort

Mod de cliente para **Minecraft 1.21.1 con Fabric** que ordena inventarios y contenedores con prioridad para los objetos de Cobblemon.

Pulsa una sola tecla y CobbleSort reorganizará las pilas de objetos sin modificar la barra rápida, sin requerir instalación en el servidor y sin añadir dependencias obligatorias de Cobblemon.

## Características

- Ordena las 27 casillas principales del inventario del jugador.
- Conserva intactas las nueve casillas de la barra rápida.
- Ordena cofres, cofres dobles, barriles y otros contenedores genéricos.
- Admite cajas shulker.
- Da prioridad a categorías útiles de Cobblemon.
- Mantiene juntos los objetos de una misma categoría mediante su identificador.
- Ejecuta los intercambios de forma gradual para evitar una ráfaga excesiva de paquetes.
- Muestra mensajes cuando el inventario fue ordenado, el proceso sigue ocupado o el contenedor no es compatible.
- Incluye traducciones en español de México e inglés.
- Permite cambiar la tecla desde la configuración de controles de Minecraft.

## Compatibilidad

| Componente | Versión |
|---|---|
| Minecraft | `1.21.1` |
| Fabric Loader | `0.19.2` o posterior |
| Fabric API | `0.115.1+1.21.1` |
| Java | `21` o posterior |
| Cobblemon | Recomendado, no obligatorio |

CobbleSort es un mod **exclusivamente de cliente**. No necesita instalarse en el servidor, aunque el servidor debe permitir las acciones normales de inventario del jugador.

## Instalación

1. Instala [Fabric Loader](https://fabricmc.net/use/installer/) para Minecraft 1.21.1.
2. Descarga e instala [Fabric API](https://modrinth.com/mod/fabric-api).
3. Descarga `cobblesort-1.0.1.jar` desde la sección de releases del repositorio.
4. Copia el archivo en la carpeta `mods` de tu instalación de Minecraft.
5. Inicia el juego con el perfil de Fabric.

Para aprovechar el orden especial de objetos Pokémon, instala también Cobblemon. Sin Cobblemon, el mod puede seguir ordenando objetos de Minecraft y de otros mods por su identificador.

## Uso

La tecla predeterminada es **R**.

- Con el inventario del jugador abierto, ordena únicamente las 27 casillas principales.
- Con un cofre, barril o shulker abierto, ordena únicamente el contenido del contenedor.
- La barra rápida no se modifica.

Puedes cambiar la tecla en:

```text
Opciones → Controles → Asignación de teclas → CobbleSort
```

Si el inventario ya está ordenado, CobbleSort mostrará igualmente la confirmación sin realizar intercambios innecesarios.

## Prioridad de orden

Los objetos se organizan con el siguiente criterio:

| Prioridad | Categoría |
|---:|---|
| 1 | Objetos evolutivos y piedras |
| 2 | Bayas |
| 3 | Poké Balls |
| 4 | Medicinas y objetos de recuperación |
| 5 | Objetos equipables |
| 6 | Objetos de combate |
| 7 | Comida, caramelos y mentas |
| 8 | Bonguris, minerales y bloques de Cobblemon |
| 9 | Otros objetos de Cobblemon |
| 10 | Objetos de Minecraft y otros mods |
| 11 | Casillas vacías |

Dentro de una misma categoría se utiliza el namespace y el identificador registrado del objeto para producir un resultado estable.

## Contenedores compatibles

CobbleSort trabaja actualmente con:

- Inventario principal del jugador.
- Contenedores que utilicen el manejador genérico de Minecraft, como cofres y barriles.
- Cajas shulker.

Las mesas de trabajo, hornos, máquinas de otros mods y pantallas con reglas especiales no se ordenan. Esta restricción evita mover objetos hacia ranuras que no aceptan cualquier tipo de pila.

## Compilar el proyecto

El repositorio incluye Gradle Wrapper, por lo que no es necesario instalar Gradle globalmente.

En Windows:

```powershell
.\gradlew.bat build
```

En Linux o macOS:

```bash
./gradlew build
```

El archivo generado estará en:

```text
build/libs/cobblesort-1.0.1.jar
```

Para iniciar un cliente de desarrollo:

```powershell
.\gradlew.bat runClient
```

La primera ejecución puede tardar mientras Gradle descarga Minecraft, Yarn, Fabric Loader y Fabric API.

## Estructura principal

```text
src/main/java/mx/carlosjr/cobblesort/
├── CobbleSortClient.java       # Registro de la tecla y eventos del cliente
├── SortController.java         # Selección de ranuras y ejecución del orden
└── CobblemonItemOrder.java     # Categorías y prioridad de objetos

src/main/resources/
├── fabric.mod.json
└── assets/cobblesort/lang/
    ├── es_mx.json
    └── en_us.json
```

## Desarrollo y contribuciones

Las mejoras y reportes de errores son bienvenidos. Al reportar un problema incluye:

- Versión de Minecraft, Fabric Loader y Fabric API.
- Versión de Cobblemon, si está instalado.
- Tipo de inventario o contenedor utilizado.
- Lista de otros mods relacionados con inventarios.
- Pasos concretos para reproducir el comportamiento.

Antes de enviar cambios, comprueba que el proyecto compile con Java 21:

```powershell
.\gradlew.bat build
```

## Licencia

Distribuido bajo la licencia [MIT](LICENSE).

Desarrollado por **Carlos Jr.**
