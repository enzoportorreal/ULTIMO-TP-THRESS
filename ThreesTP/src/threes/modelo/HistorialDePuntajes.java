package threes.modelo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Se encarga de guardar y consultar el historial de puntajes de
 * partidas ya finalizadas.
 *
 * Dentro de la arquitectura en tres capas vista en la teoria, esta
 * clase cumple el rol del "nivel de datos": es la unica responsable
 * de saber donde y como se almacena la informacion (en este caso,
 * un archivo de texto plano), separada de las reglas del juego
 * (que estan en Juego) y de la interfaz (que esta en el paquete
 * vista).
 */
public class HistorialDePuntajes {

	private static final String NOMBRE_DEL_ARCHIVO = "puntajes.txt";
	private static final int CANTIDAD_MAXIMA_A_MOSTRAR = 10;

	/** Agrega un puntaje al historial y lo deja guardado en el archivo. */
	public void agregarPuntaje(int puntaje) {
		List<Integer> puntajesGuardados = leerPuntajesDelArchivo();
		puntajesGuardados.add(puntaje);
		guardarPuntajesEnElArchivo(puntajesGuardados);
	}

	/**
	 * Devuelve los mejores puntajes guardados, ordenados de mayor a
	 * menor, limitados a una cantidad maxima para que la tabla de
	 * posiciones no crezca indefinidamente.
	 */
	public List<Integer> obtenerMejoresPuntajes() {
		List<Integer> puntajes = leerPuntajesDelArchivo();
		Collections.sort(puntajes, Collections.reverseOrder());

		if (puntajes.size() > CANTIDAD_MAXIMA_A_MOSTRAR) {
			puntajes = puntajes.subList(0, CANTIDAD_MAXIMA_A_MOSTRAR);
		}

		return puntajes;
	}

	private List<Integer> leerPuntajesDelArchivo() {
		List<Integer> puntajes = new ArrayList<>();
		File archivo = new File(NOMBRE_DEL_ARCHIVO);

		if (!archivo.exists()) {
			return puntajes;
		}

		try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
			String linea = lector.readLine();

			while (linea != null) {
				if (!linea.trim().isEmpty()) {
					puntajes.add(Integer.parseInt(linea.trim()));
				}
				linea = lector.readLine();
			}
		} catch (IOException excepcion) {
			// Si no se puede leer el archivo, seguimos con el historial vacio.
			excepcion.printStackTrace();
		}

		return puntajes;
	}

	private void guardarPuntajesEnElArchivo(List<Integer> puntajes) {
		try (BufferedWriter escritor = new BufferedWriter(new FileWriter(NOMBRE_DEL_ARCHIVO))) {
			for (Integer puntaje : puntajes) {
				escritor.write(String.valueOf(puntaje));
				escritor.newLine();
			}
		} catch (IOException excepcion) {
			excepcion.printStackTrace();
		}
	}
}
