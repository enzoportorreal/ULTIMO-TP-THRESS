package threes.vista;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import threes.modelo.Direccion;
import threes.modelo.HistorialDePuntajes;
import threes.modelo.Juego;

/**
 * Ventana principal del juego.
 *
 * Sigue la arquitectura "forms and controls" vista en la teoria: el
 * codigo de esta clase LLAMA directamente al codigo de negocio
 * (Juego, HistorialDePuntajes) para ejecutar las acciones que pide
 * el usuario, y para actualizar sus propios controles visuales
 * cuando el estado del sistema cambia.
 *
 * La relacion es en un solo sentido: esta clase conoce y usa al
 * modelo, pero ni Juego, ni Tablero, ni HistorialDePuntajes conocen
 * (ni podrian llamar) nada de esta clase. En ningun momento el
 * codigo de negocio llama al codigo de la interfaz.
 *
 * Esta clase esta escrita siguiendo el estilo que genera
 * WindowBuilder (layout null con setBounds), para poder seguir
 * editandola desde la pestaña "Design" de Eclipse.
 */
public class VentanaJuego extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final int CANTIDAD_FILAS = 4;
	private static final int CANTIDAD_COLUMNAS = 4;

	private static final Color COLOR_FONDO_VENTANA = new Color(250, 248, 239);
	private static final Color COLOR_FONDO_TABLERO = new Color(187, 173, 160);
	private static final Color COLOR_CELDA_VACIA = new Color(205, 193, 180);
	private static final Color COLOR_RECUADRO_INFO = new Color(238, 228, 218);

	private JPanel panelContenido;
	private JPanel panelTablero;
	private JLabel[][] etiquetasDeLasCeldas;

	private JLabel etiquetaTitulo;
	private JLabel etiquetaTextoPuntaje;
	private JLabel etiquetaValorPuntaje;
	private JLabel etiquetaTextoSiguiente;
	private JLabel etiquetaValorSiguiente;
	private JLabel etiquetaAyuda;
	private JButton botonNuevoJuego;

	private Juego juego;
	private HistorialDePuntajes historialDePuntajes;

	/**
	 * Punto de entrada de prueba, para poder abrir esta ventana sola
	 * desde el Design view de WindowBuilder sin necesitar el resto
	 * de la aplicacion.
	 */
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

	public VentanaJuego(Juego juego, HistorialDePuntajes historialDePuntajes) {
		this.juego = juego;
		this.historialDePuntajes = historialDePuntajes;

		aplicarLookAndFeelDelSistema();
		inicializarComponentes();
		actualizarTableroYPuntaje();
	}

	// -----------------------------------------------------------------
	// Construccion de la interfaz
	// -----------------------------------------------------------------

	/**
	 * Usa el look and feel del sistema operativo en lugar del look and
	 * feel por defecto de Java, tal como se explica en la teoria.
	 * Tiene que estar en un try/catch porque setLookAndFeel puede
	 * fallar si la clase indicada no esta disponible.
	 */
	private void aplicarLookAndFeelDelSistema() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception excepcion) {
			// Si no se puede aplicar, seguimos con el look and feel por defecto.
			excepcion.printStackTrace();
		}
	}

	private void inicializarComponentes() {
		setTitle("Threes!");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setBounds(100, 100, 480, 640);

		panelContenido = new JPanel();
		panelContenido.setBackground(COLOR_FONDO_VENTANA);
		panelContenido.setBorder(new EmptyBorder(5, 5, 5, 5));
		panelContenido.setLayout(null);
		setContentPane(panelContenido);

		agregarMenuPrincipal();
		agregarEncabezado();
		agregarTablero();
		agregarPiePagina();

		configurarControlesDeTeclado();
	}

	/**
	 * Agrega un menu principal con las mismas acciones que ya estan
	 * disponibles por boton o teclado, tal como se explica en la
	 * teoria ("es habitual que la ventana principal contenga un menu
	 * principal"). Un JMenuItem se comporta igual que un JButton.
	 */
	private void agregarMenuPrincipal() {
		JMenuBar barraDeMenu = new JMenuBar();
		setJMenuBar(barraDeMenu);

		JMenu menuJuego = new JMenu("Juego");
		barraDeMenu.add(menuJuego);

		JMenuItem itemNuevoJuego = new JMenuItem("Nuevo Juego");
		itemNuevoJuego.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evento) {
				iniciarNuevoJuego();
			}
		});
		menuJuego.add(itemNuevoJuego);

		JMenuItem itemVerPuntajes = new JMenuItem("Ver Puntajes");
		itemVerPuntajes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evento) {
				mostrarDialogoDePuntajes();
			}
		});
		menuJuego.add(itemVerPuntajes);

		menuJuego.addSeparator();

		JMenuItem itemSalir = new JMenuItem("Salir");
		itemSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evento) {
				System.exit(0);
			}
		});
		menuJuego.add(itemSalir);
	}

	private void agregarEncabezado() {
		etiquetaTitulo = new JLabel("THREES!");
		etiquetaTitulo.setFont(new Font("Arial", Font.BOLD, 28));
		etiquetaTitulo.setBounds(20, 15, 200, 35);
		panelContenido.add(etiquetaTitulo);

		etiquetaTextoPuntaje = new JLabel("Puntaje");
		etiquetaTextoPuntaje.setFont(new Font("Arial", Font.PLAIN, 12));
		etiquetaTextoPuntaje.setBounds(250, 15, 90, 15);
		panelContenido.add(etiquetaTextoPuntaje);

		etiquetaValorPuntaje = new JLabel("0");
		etiquetaValorPuntaje.setHorizontalAlignment(SwingConstants.CENTER);
		etiquetaValorPuntaje.setFont(new Font("Arial", Font.BOLD, 22));
		etiquetaValorPuntaje.setOpaque(true);
		etiquetaValorPuntaje.setBackground(COLOR_RECUADRO_INFO);
		etiquetaValorPuntaje.setBounds(250, 32, 90, 32);
		panelContenido.add(etiquetaValorPuntaje);

		etiquetaTextoSiguiente = new JLabel("Siguiente");
		etiquetaTextoSiguiente.setFont(new Font("Arial", Font.PLAIN, 12));
		etiquetaTextoSiguiente.setBounds(350, 15, 90, 15);
		panelContenido.add(etiquetaTextoSiguiente);

		etiquetaValorSiguiente = new JLabel("1");
		etiquetaValorSiguiente.setHorizontalAlignment(SwingConstants.CENTER);
		etiquetaValorSiguiente.setFont(new Font("Arial", Font.BOLD, 22));
		etiquetaValorSiguiente.setOpaque(true);
		etiquetaValorSiguiente.setBackground(COLOR_RECUADRO_INFO);
		etiquetaValorSiguiente.setBounds(350, 32, 90, 32);
		panelContenido.add(etiquetaValorSiguiente);
	}

	private void agregarTablero() {
		panelTablero = new JPanel();
		panelTablero.setBackground(COLOR_FONDO_TABLERO);
		panelTablero.setBounds(20, 80, 420, 420);
		panelTablero.setLayout(new GridLayout(CANTIDAD_FILAS, CANTIDAD_COLUMNAS, 8, 8));
		panelContenido.add(panelTablero);

		// Las 16 celdas se crean por codigo (y no arrastrando cada una
		// desde el Design view) porque su contenido cambia todo el
		// tiempo durante la partida: no tendria sentido "prearmarlas"
		// a mano si despues se van a reescribir en cada jugada. El
		// resto de la ventana (titulo, puntaje, menu, boton) si se
		// puede seguir editando visualmente desde WindowBuilder.
		etiquetasDeLasCeldas = new JLabel[CANTIDAD_FILAS][CANTIDAD_COLUMNAS];

		for (int fila = 0; fila < CANTIDAD_FILAS; fila++) {
			for (int columna = 0; columna < CANTIDAD_COLUMNAS; columna++) {
				JLabel etiquetaCelda = new JLabel("");
				etiquetaCelda.setOpaque(true);
				etiquetaCelda.setHorizontalAlignment(SwingConstants.CENTER);
				etiquetaCelda.setFont(new Font("Arial", Font.BOLD, 28));
				etiquetaCelda.setBackground(COLOR_CELDA_VACIA);

				panelTablero.add(etiquetaCelda);
				etiquetasDeLasCeldas[fila][columna] = etiquetaCelda;
			}
		}
	}

	private void agregarPiePagina() {
		botonNuevoJuego = new JButton("Nuevo Juego");
		botonNuevoJuego.setToolTipText("Comienza una partida nueva, descartando la actual");
		botonNuevoJuego.setFont(new Font("Arial", Font.BOLD, 14));
		botonNuevoJuego.setBounds(20, 520, 150, 35);
		botonNuevoJuego.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evento) {
				iniciarNuevoJuego();
			}
		});
		panelContenido.add(botonNuevoJuego);

		etiquetaAyuda = new JLabel("Usa las flechas del teclado para mover las fichas");
		etiquetaAyuda.setFont(new Font("Arial", Font.PLAIN, 12));
		etiquetaAyuda.setBounds(20, 559, 400, 20);
		panelContenido.add(etiquetaAyuda);
	}

	private void configurarControlesDeTeclado() {
		setFocusable(true);
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evento) {
				procesarTeclaPresionada(evento.getKeyCode());
			}
		});
	}

	// -----------------------------------------------------------------
	// Acciones solicitadas por el usuario: se llama directamente al
	// codigo de negocio (Juego / HistorialDePuntajes), tal como
	// indica la arquitectura "forms and controls".
	// -----------------------------------------------------------------

	private void procesarTeclaPresionada(int codigoDeTecla) {
		switch (codigoDeTecla) {
			case KeyEvent.VK_LEFT:
				moverFicha(Direccion.IZQUIERDA);
				break;
			case KeyEvent.VK_RIGHT:
				moverFicha(Direccion.DERECHA);
				break;
			case KeyEvent.VK_UP:
				moverFicha(Direccion.ARRIBA);
				break;
			case KeyEvent.VK_DOWN:
				moverFicha(Direccion.ABAJO);
				break;
			default:
				break;
		}
	}

	private void moverFicha(Direccion direccion) {
		boolean seMovioAlgunaFicha = juego.moverFicha(direccion);

		if (seMovioAlgunaFicha) {
			actualizarTableroYPuntaje();

			if (juego.estaTerminado()) {
				int puntajeFinal = juego.obtenerPuntaje();
				historialDePuntajes.agregarPuntaje(puntajeFinal);
				mostrarFinDeJuego(puntajeFinal);
			}
		}
	}

	private void iniciarNuevoJuego() {
		juego.iniciarNuevoJuego();
		actualizarTableroYPuntaje();
		requestFocusInWindow();
	}

	private void mostrarDialogoDePuntajes() {
		DialogoPuntajes dialogo = new DialogoPuntajes(this, historialDePuntajes);
		dialogo.setVisible(true);
	}

	// -----------------------------------------------------------------
	// Actualizacion de los controles visuales cuando cambia el estado
	// del sistema. Esta clase LEE el estado actual del modelo cada
	// vez que lo necesita; el modelo nunca la llama a ella.
	// -----------------------------------------------------------------

	private void actualizarTableroYPuntaje() {
		int[][] tablero = juego.obtenerTablero().obtenerCopia();

		for (int fila = 0; fila < CANTIDAD_FILAS; fila++) {
			for (int columna = 0; columna < CANTIDAD_COLUMNAS; columna++) {
				actualizarCelda(etiquetasDeLasCeldas[fila][columna], tablero[fila][columna]);
			}
		}

		etiquetaValorPuntaje.setText(String.valueOf(juego.obtenerPuntaje()));

		int valorSiguienteFicha = juego.obtenerValorSiguienteFicha();
		etiquetaValorSiguiente.setText(String.valueOf(valorSiguienteFicha));
		etiquetaValorSiguiente.setBackground(obtenerColorSegunValor(valorSiguienteFicha));
	}

	private void actualizarCelda(JLabel etiquetaCelda, int valorFicha) {
		if (valorFicha == 0) {
			etiquetaCelda.setText("");
			etiquetaCelda.setBackground(COLOR_CELDA_VACIA);
		} else {
			etiquetaCelda.setText(String.valueOf(valorFicha));
			etiquetaCelda.setBackground(obtenerColorSegunValor(valorFicha));
		}
	}

	/**
	 * Muestra el resultado final con dos opciones para elegir, tal
	 * como se explica en la teoria de cuadros de dialogo
	 * (JOptionPane.showOptionDialog con un arreglo de opciones).
	 */
	private void mostrarFinDeJuego(int puntajeFinal) {
		Object[] opciones = { "Ver puntajes", "Nuevo juego" };

		int opcionElegida = JOptionPane.showOptionDialog(
				this,
				"Juego terminado. Puntaje final: " + puntajeFinal,
				"Fin del juego",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE,
				null, // Icono
				opciones,
				opciones[1]); // Valor inicial

		if (opcionElegida == 0) {
			mostrarDialogoDePuntajes();
		} else {
			iniciarNuevoJuego();
		}
	}

	/**
	 * Devuelve el color de fondo que le corresponde a cada valor de
	 * ficha, para que el tablero sea facil de leer de un vistazo.
	 */
	private Color obtenerColorSegunValor(int valorFicha) {
		switch (valorFicha) {
			case 1:
				return new Color(242, 177, 172);
			case 2:
				return new Color(156, 195, 232);
			case 3:
				return new Color(238, 228, 218);
			case 6:
				return new Color(237, 214, 165);
			case 12:
				return new Color(242, 177, 121);
			case 24:
				return new Color(245, 149, 99);
			case 48:
				return new Color(246, 124, 95);
			case 96:
				return new Color(237, 194, 46);
			default:
				return new Color(205, 152, 46);
		}
	}
}
