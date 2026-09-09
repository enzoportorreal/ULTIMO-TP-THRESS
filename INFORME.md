# Threes! — Informe de implementación

## 1. Arquitectura elegida: Forms and Controls

Se eligió **forms and controls**, la primera arquitectura vista en la
teoría. La regla que la define es esta:

> "El código de interfaz realiza llamados al código de negocio, para
> ejecutar acciones solicitadas por el usuario, y actualizar los
> controles visuales cuando cambia el estado del sistema. En ningún
> momento el código de negocio debe llamar al código de la interfaz."

Es decir: la **vista conoce y llama directamente al modelo**, pero el
**modelo nunca conoce ni llama a la vista**. La dependencia va en un
solo sentido.

### Estructura de paquetes

```
threes/
├── modelo/            -> Lógica de negocio y datos (no depende de Swing,
│   ├── Direccion.java     y nunca importa nada de threes.vista)
│   ├── Tablero.java
│   ├── Juego.java
│   └── HistorialDePuntajes.java
├── vista/              -> Interfaz gráfica (importa y llama al modelo
│   ├── VentanaJuego.java   directamente)
│   └── DialogoPuntajes.java
└── Main.java             -> Punto de entrada
```

### Cómo se ve esto en el código

`VentanaJuego` recibe `Juego` y `HistorialDePuntajes` por constructor, y
los guarda como atributos:

```java
public VentanaJuego(Juego juego, HistorialDePuntajes historialDePuntajes) {
    this.juego = juego;
    this.historialDePuntajes = historialDePuntajes;
    ...
}
```

Cuando el usuario aprieta una flecha, la vista llama directamente al
modelo para ejecutar la acción, y después se actualiza a sí misma leyendo
el nuevo estado:

```java
private void moverFicha(Direccion direccion) {
    boolean seMovioAlgunaFicha = juego.moverFicha(direccion);

    if (seMovioAlgunaFicha) {
        actualizarTableroYPuntaje();   // la vista se actualiza sola,
                                        // leyendo el estado del modelo
        if (juego.estaTerminado()) {
            historialDePuntajes.agregarPuntaje(juego.obtenerPuntaje());
            mostrarFinDeJuego(juego.obtenerPuntaje());
        }
    }
}
```

`Juego`, `Tablero`, `Direccion` y `HistorialDePuntajes` (el modelo) nunca
importan nada del paquete `vista`, ni reciben una referencia hacia
`VentanaJuego`. Se puede verificar buscando imports:

```
grep -rn "import threes.vista" src/threes/modelo/    -> no encuentra nada
```

### Por qué se descartó Model-View-Presenter

En una primera versión se había armado con **MVP**, agregando dos
interfaces (`IVistaJuego` y `EscuchaDeJuego`) para que la vista no
conociera ningún tipo del modelo directamente, solo primitivos (`int[][]`,
`int`). Esto está pensado para poder reemplazar la tecnología de la
interfaz sin tocar el resto del sistema, pero agrega una capa de
indirección (dos interfaces y una clase presentador de más) que no aporta
nada para un trabajo de este tamaño, con una sola vista y sin necesidad de
esa flexibilidad. Se simplificó a forms and controls, que cumple igual el
requisito central (separar el código de negocio del código de interfaz)
con menos clases.

## 2. Reglas del juego implementadas

- Grilla de 4×4, movimiento en las 4 direcciones con fusión 1+2→3 y n+n→2n
  para múltiplos de 3, aparición de nueva ficha en el borde opuesto, y
  detección de fin de juego cuando no hay celdas vacías ni fusiones
  posibles.
- Puntaje: `3^k` por cada ficha, donde `k` es la cantidad de duplicaciones
  desde el 3 original. Las fichas 1 y 2 no suman puntos.

### Objetivos opcionales implementados

1. **Mostrar la próxima ficha:** panel "Siguiente" en el encabezado.
2. **Registrar los mejores puntajes y consultar la tabla histórica:**
   ver sección 3.

## 3. Aplicación de las clases de teoría sobre WindowBuilder

- **Look and feel del sistema:** `VentanaJuego` llama a
  `UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())`
  al principio de su constructor, dentro de un `try/catch` (obligatorio,
  ya que el método puede fallar).

- **JTable + JDialog para la tabla de posiciones (objetivo opcional #2):**
  `DialogoPuntajes` es un `JDialog` **modal** (`super(padre, true)`) que
  muestra los puntajes históricos en una `JTable` con
  `DefaultTableModel`, centrando las celdas con
  `DefaultTableCellRenderer` y fijando el ancho de columnas. Se usa un
  diálogo modal y no un segundo `JFrame` porque no tendría sentido dejar
  que el usuario siga jugando (y que el tablero cambie) mientras está
  consultando el historial.

- **JMenuBar / JMenu / JMenuItem:** menú "Juego" con Nuevo Juego, Ver
  Puntajes y Salir.

- **Cuadro de diálogo con opciones:** el mensaje de fin de juego usa
  `JOptionPane.showOptionDialog` con dos opciones ("Ver puntajes" /
  "Nuevo juego").

- **Tooltips:** agregados a los botones principales.

- **Absolute layout:** toda la interfaz usa `setLayout(null)` con
  `setBounds()` en cada componente (lo que genera WindowBuilder al elegir
  *Absolute Layout* desde el *Design view*).

### Qué NO se aplicó, y por qué

No se agregaron `JTextField`, `JComboBox`, `JRadioButton` ni `JCheckBox`
porque el juego no tiene ningún dato que el usuario deba tipear ni ninguna
opción que deba elegir antes o durante la partida (se juega únicamente con
las flechas del teclado).

## 4. Por qué `Direccion` es una clase aparte

`Direccion` es un enum de 4 valores usado tanto por `Juego` (para saber
hacia dónde mover las fichas) como por `VentanaJuego` (para traducir la
tecla que apretó el usuario). Al ser un concepto que comparten ambas
capas, conviene que sea un tipo independiente en lugar de estar anidado
dentro de `Juego`: si estuviera adentro, la vista tendría que referirse a
`Juego.Direccion` en todos lados, acoplándose innecesariamente al nombre
de una clase con la que en realidad no tiene que ver (la dirección de
movimiento no es un detalle interno de `Juego`, es parte del vocabulario
compartido entre el modelo y la vista). Mantenerla separada respeta el
pedido del enunciado de "buen encapsulamiento y buena cohesión, evitando
el acoplamiento entre clases": el tamaño de una clase no determina si está
bien diseñada, sino que represente un único concepto claro.

## 5. Control de versiones (recomendación para el trabajo en grupo)

Dado que el trabajo se entrega en grupos de 3 o 4 personas, se recomienda
usar el control de versiones visto en la teoría (SVN o Git, ambos
soportados por Eclipse mediante plugins):

- Hacer commits pequeños y frecuentes, no uno solo al final.
- Actualizar (`update`/`pull`) antes de empezar a trabajar cada vez.
- Nunca commitear archivos derivados: la carpeta `bin/` y `puntajes.txt`
  (que se genera y cambia en cada partida) no deberían formar parte del
  repositorio; solo el código fuente.
- Resolver los conflictos apenas aparecen, no dejarlos acumular.

## 6. Buenas prácticas aplicadas

- **Encapsulamiento:** `Tablero` es la única clase que conoce el arreglo
  interno; `HistorialDePuntajes` es la única que conoce el archivo en
  disco.
- **Responsabilidad única:** cada clase tiene un propósito claro.
- **Dependencia en un solo sentido:** la vista conoce al modelo, el
  modelo nunca conoce a la vista.
- **Métodos chicos y nombres declarativos:** por ejemplo,
  `moverYFusionarLinea`, `sePuedenFusionar`,
  `colocarNuevaFichaTrasElMovimiento`, `centrarColumnasDeLaTabla`.

## 7. Cómo ejecutar

Desde Eclipse: `File > Import > Existing Projects into Workspace`, y correr
`Main.java` como aplicación Java. La primera vez que termine una partida se
va a crear un archivo `puntajes.txt` en la carpeta del proyecto.

Desde línea de comandos:
```
javac -d bin $(find src -name "*.java")
java -cp bin threes.Main
```
