package threes;

import java.awt.EventQueue;

import threes.modelo.HistorialDePuntajes;
import threes.modelo.Juego;
import threes.vista.VentanaJuego;

public class Main {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Juego juego = new Juego();
					HistorialDePuntajes historialDePuntajes = new HistorialDePuntajes();

					VentanaJuego ventana = new VentanaJuego(juego, historialDePuntajes);
					ventana.setVisible(true);
				} catch (Exception excepcion) {
					excepcion.printStackTrace();
				}
			}
		});
	}
}
