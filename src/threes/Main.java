package threes;

import threesModelo.HistorialDePuntajes;
import threesModelo.Juego;
import threesVista.VentanaJuego;

public class Main {
    public static void main(String[] args) {
        Juego juego = new Juego();
        HistorialDePuntajes historialDePuntajes = new HistorialDePuntajes();
        VentanaJuego ventana = new VentanaJuego(juego, historialDePuntajes);
        ventana.setVisible(true);
    }
}
