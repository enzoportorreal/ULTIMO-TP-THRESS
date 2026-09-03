package threes.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa la grilla de 4x4 del juego.
 *
 * Es la unica clase que conoce como se almacenan las fichas
 * internamente (un arreglo de enteros). El resto del sistema
 * accede siempre a traves de sus metodos, nunca directamente
 * al arreglo.
 *
 * Convencion utilizada: el valor 0 representa una celda vacia.
 */
public class Tablero {

	public static final int TAMANIO = 4;

	private int[][] celdas;

	public Tablero() {
		celdas = new int[TAMANIO][TAMANIO];
	}

	public int obtenerValor(int fila, int columna) {
		return celdas[fila][columna];
	}

	public void asignarValor(int fila, int columna, int valor) {
		celdas[fila][columna] = valor;
	}

	public boolean estaVacia(int fila, int columna) {
		return celdas[fila][columna] == 0;
	}

	public void vaciarTablero() {
		for (int fila = 0; fila < TAMANIO; fila++) {
			for (int columna = 0; columna < TAMANIO; columna++) {
				celdas[fila][columna] = 0;
			}
		}
	}

	/**
	 * Devuelve una copia de la grilla. La vista siempre recibe esta
	 * copia (a traves del presentador) y nunca una referencia al
	 * arreglo interno, para que no pueda modificarlo por error.
	 */
	public int[][] obtenerCopia() {
		int[][] copia = new int[TAMANIO][TAMANIO];

		for (int fila = 0; fila < TAMANIO; fila++) {
			copia[fila] = celdas[fila].clone();
		}

		return copia;
	}

	public List<int[]> obtenerCeldasVacias() {
		List<int[]> celdasVacias = new ArrayList<>();

		for (int fila = 0; fila < TAMANIO; fila++) {
			for (int columna = 0; columna < TAMANIO; columna++) {
				if (celdas[fila][columna] == 0) {
					celdasVacias.add(new int[] { fila, columna });
				}
			}
		}

		return celdasVacias;
	}
}
