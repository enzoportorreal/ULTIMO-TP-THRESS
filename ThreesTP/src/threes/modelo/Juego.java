package threes.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Contiene toda la logica de negocio del juego Threes: el
 * movimiento de las fichas, las fusiones, la generacion de fichas
 * nuevas, el calculo del puntaje y la deteccion del fin del juego.
 *
 * Esta clase no importa ninguna clase de Swing ni conoce nada de
 * la interfaz grafica. Esto respeta el principio de "separated
 * presentation" visto en la teoria de la materia: el codigo de
 * negocio nunca debe conocer la identidad ni la tecnologia
 * utilizada por la interfaz.
 */
public class Juego {

	private Tablero tablero;
	private Random generadorAleatorio;
	private int valorSiguienteFicha;
	private boolean juegoTerminado;

	public Juego() {
		tablero = new Tablero();
		generadorAleatorio = new Random();
		iniciarNuevoJuego();
	}

	public void iniciarNuevoJuego() {
		tablero.vaciarTablero();
		juegoTerminado = false;

		colocarFichaAleatoriaEnCeldaVacia();
		colocarFichaAleatoriaEnCeldaVacia();
		colocarFichaAleatoriaEnCeldaVacia();

		valorSiguienteFicha = generarValorFichaAleatorio();
	}

	public Tablero obtenerTablero() {
		return tablero;
	}

	public boolean estaTerminado() {
		return juegoTerminado;
	}

	public int obtenerValorSiguienteFicha() {
		return valorSiguienteFicha;
	}

	/**
	 * Mueve todas las fichas del tablero en la direccion indicada.
	 *
	 * @return true si el tablero cambio (el movimiento era valido),
	 *         false si ninguna ficha pudo desplazarse.
	 */
	public boolean moverFicha(Direccion direccion) {
		if (juegoTerminado) {
			return false;
		}

		boolean seMovioAlgunaFicha = moverTodasLasFichas(direccion);

		if (seMovioAlgunaFicha) {
			colocarNuevaFichaTrasElMovimiento(direccion);
			valorSiguienteFicha = generarValorFichaAleatorio();

			if (!hayCeldasVacias() && !hayFusionesPosibles()) {
				juegoTerminado = true;
			}
		}

		return seMovioAlgunaFicha;
	}

	/**
	 * Calcula el puntaje total sumando el aporte de cada ficha del
	 * tablero, siguiendo la regla oficial de Threes: las fichas 1 y 2
	 * no valen puntos, y cada multiplo de 3 vale 3 elevado a la
	 * cantidad de "duplicaciones" que tuvo desde el 3 original.
	 */
	public int obtenerPuntaje() {
		int puntajeTotal = 0;

		for (int fila = 0; fila < Tablero.TAMANIO; fila++) {
			for (int columna = 0; columna < Tablero.TAMANIO; columna++) {
				puntajeTotal += calcularPuntajeDeFicha(tablero.obtenerValor(fila, columna));
			}
		}

		return puntajeTotal;
	}

	// -----------------------------------------------------------------
	// Movimiento y fusion de fichas
	// -----------------------------------------------------------------

	private boolean moverTodasLasFichas(Direccion direccion) {
		boolean huboMovimiento = false;

		for (int indiceLinea = 0; indiceLinea < Tablero.TAMANIO; indiceLinea++) {
			int[] lineaOriginal = extraerLinea(direccion, indiceLinea);
			int[] lineaMovida = moverYFusionarLinea(lineaOriginal);

			if (!lineasSonIguales(lineaOriginal, lineaMovida)) {
				huboMovimiento = true;
			}

			escribirLinea(direccion, indiceLinea, lineaMovida);
		}

		return huboMovimiento;
	}

	/**
	 * Toma una linea de 4 fichas (donde la posicion 0 es el borde
	 * hacia el que se esta moviendo) y devuelve la linea resultante de
	 * desplazar las fichas y fusionar como maximo una vez cada una,
	 * tal como funciona el movimiento en Threes.
	 */
	private int[] moverYFusionarLinea(int[] lineaOriginal) {
		int[] linea = lineaOriginal.clone();

		for (int posicion = 0; posicion < linea.length - 1; posicion++) {
			int fichaActual = linea[posicion];
			int fichaSiguiente = linea[posicion + 1];

			if (fichaActual == 0 && fichaSiguiente != 0) {
				linea[posicion] = fichaSiguiente;
				linea[posicion + 1] = 0;
			} else if (sePuedenFusionar(fichaActual, fichaSiguiente)) {
				linea[posicion] = fichaActual + fichaSiguiente;
				linea[posicion + 1] = 0;
			}
		}

		return linea;
	}

	private boolean sePuedenFusionar(int valorA, int valorB) {
		if (valorA == 0 || valorB == 0) {
			return false;
		}

		boolean esUnoConDos = (valorA == 1 && valorB == 2) || (valorA == 2 && valorB == 1);
		boolean sonMultiplosDeTresIguales = (valorA == valorB) && (valorA >= 3);

		return esUnoConDos || sonMultiplosDeTresIguales;
	}

	private int[] extraerLinea(Direccion direccion, int indiceLinea) {
		int[] linea = new int[Tablero.TAMANIO];

		for (int posicion = 0; posicion < Tablero.TAMANIO; posicion++) {
			int fila = calcularFila(direccion, indiceLinea, posicion);
			int columna = calcularColumna(direccion, indiceLinea, posicion);
			linea[posicion] = tablero.obtenerValor(fila, columna);
		}

		return linea;
	}

	private void escribirLinea(Direccion direccion, int indiceLinea, int[] linea) {
		for (int posicion = 0; posicion < Tablero.TAMANIO; posicion++) {
			int fila = calcularFila(direccion, indiceLinea, posicion);
			int columna = calcularColumna(direccion, indiceLinea, posicion);
			tablero.asignarValor(fila, columna, linea[posicion]);
		}
	}

	/**
	 * Traduce (direccion, indice de linea, posicion dentro de la
	 * linea) a la fila real del tablero. La posicion 0 de la linea
	 * siempre corresponde al borde hacia el que se mueve el jugador.
	 */
	private int calcularFila(Direccion direccion, int indiceLinea, int posicion) {
		if (direccion == Direccion.IZQUIERDA || direccion == Direccion.DERECHA) {
			return indiceLinea;
		} else if (direccion == Direccion.ARRIBA) {
			return posicion;
		} else {
			return Tablero.TAMANIO - 1 - posicion;
		}
	}

	private int calcularColumna(Direccion direccion, int indiceLinea, int posicion) {
		if (direccion == Direccion.IZQUIERDA) {
			return posicion;
		} else if (direccion == Direccion.DERECHA) {
			return Tablero.TAMANIO - 1 - posicion;
		} else {
			return indiceLinea;
		}
	}

	private boolean lineasSonIguales(int[] lineaA, int[] lineaB) {
		for (int posicion = 0; posicion < lineaA.length; posicion++) {
			if (lineaA[posicion] != lineaB[posicion]) {
				return false;
			}
		}
		return true;
	}

	// -----------------------------------------------------------------
	// Generacion de nuevas fichas
	// -----------------------------------------------------------------

	private void colocarFichaAleatoriaEnCeldaVacia() {
		List<int[]> celdasVacias = tablero.obtenerCeldasVacias();

		if (celdasVacias.isEmpty()) {
			return;
		}

		int[] celdaElegida = celdasVacias.get(generadorAleatorio.nextInt(celdasVacias.size()));
		tablero.asignarValor(celdaElegida[0], celdaElegida[1], generarValorFichaAleatorio());
	}

	/**
	 * Coloca la ficha "siguiente" en una celda vacia del borde
	 * opuesto a la direccion del movimiento, tal como pide el
	 * enunciado. Si no hay celdas vacias en ese borde en particular,
	 * se usa cualquier celda vacia del tablero como alternativa.
	 */
	private void colocarNuevaFichaTrasElMovimiento(Direccion direccion) {
		List<int[]> celdasCandidatas = obtenerCeldasVaciasEnBordeOpuesto(direccion);

		if (celdasCandidatas.isEmpty()) {
			celdasCandidatas = tablero.obtenerCeldasVacias();
		}

		if (celdasCandidatas.isEmpty()) {
			return;
		}

		int[] celdaElegida = celdasCandidatas.get(generadorAleatorio.nextInt(celdasCandidatas.size()));
		tablero.asignarValor(celdaElegida[0], celdaElegida[1], valorSiguienteFicha);
	}

	private List<int[]> obtenerCeldasVaciasEnBordeOpuesto(Direccion direccion) {
		List<int[]> celdasEnElBorde = new ArrayList<>();

		for (int fila = 0; fila < Tablero.TAMANIO; fila++) {
			for (int columna = 0; columna < Tablero.TAMANIO; columna++) {
				if (tablero.estaVacia(fila, columna) && esBordeOpuesto(direccion, fila, columna)) {
					celdasEnElBorde.add(new int[] { fila, columna });
				}
			}
		}

		return celdasEnElBorde;
	}

	private boolean esBordeOpuesto(Direccion direccion, int fila, int columna) {
		switch (direccion) {
			case IZQUIERDA:
				return columna == Tablero.TAMANIO - 1;
			case DERECHA:
				return columna == 0;
			case ARRIBA:
				return fila == Tablero.TAMANIO - 1;
			case ABAJO:
				return fila == 0;
			default:
				return false;
		}
	}

	private int generarValorFichaAleatorio() {
		// Genera 1, 2 o 3 con la misma probabilidad.
		return generadorAleatorio.nextInt(3) + 1;
	}

	// -----------------------------------------------------------------
	// Deteccion de fin de juego
	// -----------------------------------------------------------------

	private boolean hayCeldasVacias() {
		return !tablero.obtenerCeldasVacias().isEmpty();
	}

	private boolean hayFusionesPosibles() {
		for (int fila = 0; fila < Tablero.TAMANIO; fila++) {
			for (int columna = 0; columna < Tablero.TAMANIO; columna++) {
				int valorActual = tablero.obtenerValor(fila, columna);

				boolean hayFichaALaDerecha = columna + 1 < Tablero.TAMANIO;
				if (hayFichaALaDerecha && sePuedenFusionar(valorActual, tablero.obtenerValor(fila, columna + 1))) {
					return true;
				}

				boolean hayFichaAbajo = fila + 1 < Tablero.TAMANIO;
				if (hayFichaAbajo && sePuedenFusionar(valorActual, tablero.obtenerValor(fila + 1, columna))) {
					return true;
				}
			}
		}
		return false;
	}

	// -----------------------------------------------------------------
	// Calculo de puntaje
	// -----------------------------------------------------------------

	private int calcularPuntajeDeFicha(int valor) {
		if (valor < 3) {
			return 0;
		}

		int cantidadDeDuplicaciones = 1;
		int valorRestante = valor;

		while (valorRestante > 3) {
			valorRestante = valorRestante / 2;
			cantidadDeDuplicaciones++;
		}

		return (int) Math.pow(3, cantidadDeDuplicaciones);
	}
}
