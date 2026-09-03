package threes.vista;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import threes.modelo.HistorialDePuntajes;

/**
 * Dialogo modal que muestra la tabla de mejores puntajes historicos.
 *
 * Al igual que VentanaJuego, esta clase conoce directamente al
 * modelo (HistorialDePuntajes) y le pide los datos que necesita
 * mostrar. El modelo, en cambio, no tiene ninguna referencia hacia
 * este dialogo.
 *
 * Es un JDialog (y no una segunda ventana independiente) porque,
 * segun la teoria, un dialogo "no permite continuar hasta que no
 * sea cerrado". Tiene sentido que sea asi: no seria buena idea que
 * el usuario pueda seguir jugando (y que el tablero cambie) mientras
 * esta mirando la tabla de puntajes.
 */
public class DialogoPuntajes extends JDialog {

	private static final long serialVersionUID = 1L;

	private JTable tablaDePuntajes;

	public DialogoPuntajes(JFrame ventanaPadre, HistorialDePuntajes historialDePuntajes) {
		super(ventanaPadre, true); // true = modal
		inicializarComponentes();
		cargarPuntajesEnLaTabla(historialDePuntajes.obtenerMejoresPuntajes());
	}

	private void inicializarComponentes() {
		setTitle("Mejores puntajes");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setBounds(150, 150, 300, 400);

		getContentPane().setLayout(null);

		JLabel etiquetaTitulo = new JLabel("Tabla de posiciones");
		etiquetaTitulo.setFont(new Font("Arial", Font.BOLD, 16));
		etiquetaTitulo.setBounds(20, 15, 250, 25);
		getContentPane().add(etiquetaTitulo);

		tablaDePuntajes = new JTable();

		JScrollPane scrollDeLaTabla = new JScrollPane(tablaDePuntajes);
		scrollDeLaTabla.setBounds(20, 50, 250, 260);
		scrollDeLaTabla.setBorder(new TitledBorder("Historial"));
		getContentPane().add(scrollDeLaTabla);

		JButton botonCerrar = new JButton("Cerrar");
		botonCerrar.setToolTipText("Cierra esta ventana y vuelve al juego");
		botonCerrar.setBounds(105, 320, 80, 30);
		botonCerrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evento) {
				dispose();
			}
		});
		getContentPane().add(botonCerrar);
	}

	private void cargarPuntajesEnLaTabla(List<Integer> mejoresPuntajes) {
		DefaultTableModel modeloDeLaTabla = new DefaultTableModel();
		modeloDeLaTabla.addColumn("Posición");
		modeloDeLaTabla.addColumn("Puntaje");

		if (mejoresPuntajes.isEmpty()) {
			modeloDeLaTabla.addRow(new Object[] { "-", "Sin partidas registradas" });
		} else {
			int posicion = 1;
			for (Integer puntaje : mejoresPuntajes) {
				modeloDeLaTabla.addRow(new Object[] { posicion, puntaje });
				posicion++;
			}
		}

		tablaDePuntajes.setModel(modeloDeLaTabla);
		centrarColumnasDeLaTabla();
	}

	private void centrarColumnasDeLaTabla() {
		DefaultTableCellRenderer renderizadorCentrado = new DefaultTableCellRenderer();
		renderizadorCentrado.setHorizontalAlignment(SwingConstants.CENTER);

		tablaDePuntajes.getColumnModel().getColumn(0).setCellRenderer(renderizadorCentrado);
		tablaDePuntajes.getColumnModel().getColumn(1).setCellRenderer(renderizadorCentrado);

		tablaDePuntajes.getColumnModel().getColumn(0).setPreferredWidth(80);
		tablaDePuntajes.getColumnModel().getColumn(1).setPreferredWidth(160);
	}
}
