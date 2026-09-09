package threes;

import java.awt.EventQueue;

import threesModelo.HistorialDePuntajes;
import threesModelo.Juego;
import threesVista.VentanaJuego;

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
