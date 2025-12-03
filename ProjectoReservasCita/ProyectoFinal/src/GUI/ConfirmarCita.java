package GUI;

import java.awt.EventQueue;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import ReservasMedicas.conexion.GestorBD;
import ReservasMedicas.models.Cita;
import ReservasMedicas.models.EstadoCita;
import ReservasMedicas.service.GestorCitas;

public class ConfirmarCita extends JInternalFrame {

    private static final long serialVersionUID = 1L;

    private JTable table;
    private JButton btnConfirmar;
    private JButton btnCerrar;
    private JButton btnRefrescar;

    private GestorCitas gestor;

    // SOLO para pruebas aisladas
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ConfirmarCita frame = new ConfirmarCita();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ConfirmarCita() {
        setTitle("Confirmar Citas");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setBounds(100, 100, 600, 450);
        getContentPane().setLayout(null);

        JLabel lblTitulo = new JLabel("Confirmar Citas");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblTitulo.setBounds(200, 10, 200, 25);
        getContentPane().add(lblTitulo);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(20, 60, 550, 280);
        getContentPane().add(scrollPane);

        table = new JTable();
        table.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] { "ID Cita", "Paciente", "Doctor", "Fecha", "Hora", "Estado" }
        ));
        scrollPane.setViewportView(table);

        btnRefrescar = new JButton("Refrescar");
        btnRefrescar.setBounds(20, 360, 100, 23);
        getContentPane().add(btnRefrescar);

        btnConfirmar = new JButton("Confirmar");
        btnConfirmar.setBounds(250, 360, 100, 23);
        getContentPane().add(btnConfirmar);

        btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(470, 360, 100, 23);
        getContentPane().add(btnCerrar);

        // ==========================
        //  INICIALIZAR SERVICIO BD
        // ==========================
        GestorBD gestorBD = new GestorBD("clinica.db");
        gestor = new GestorCitas(gestorBD);

        // cargar citas en la tabla
        cargarCitasProgramadas();

        // ==========================
        //  EVENTOS
        // ==========================

        btnRefrescar.addActionListener(e -> cargarCitasProgramadas());

        btnConfirmar.addActionListener(e -> confirmarSeleccionada());

        btnCerrar.addActionListener(e -> dispose());
    }

    /** Carga SOLO las citas en estado PROGRAMADA */
    private void cargarCitasProgramadas() {
        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0); // limpiar

            List<Cita> citas = gestor.listarCitas();   // usa repo.findAllCitas()

            DateTimeFormatter fFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter fHora  = DateTimeFormatter.ofPattern("HH:mm");

            for (Cita c : citas) {
                if (c.getEstado() == EstadoCita.PROGRAMADA) {
                    model.addRow(new Object[] {
                        c.getId(),
                        c.getPaciente().getPersona().getNombre(),
                        c.getMedico().getPersona().getNombre() + " - " + c.getMedico().getEspecialidad(),
                        c.getFecha().format(fFecha),
                        c.getHora().format(fHora),
                        c.getEstado().name()
                    });
                }
            }

            if (model.getRowCount() == 0) {
                // opcional: mensaje suave si no hay nada
                // JOptionPane.showMessageDialog(this, "No hay citas programadas para confirmar.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Error al cargar citas: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /** Confirma la cita seleccionada en la tabla */
    private void confirmarSeleccionada() {
        int fila = table.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Seleccione una cita de la tabla.",
                "Sin selección",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
            this,
            "¿Confirmar la cita seleccionada?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION
        );
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int idCita = (int) model.getValueAt(fila, 0);

        try {
            boolean ok = gestor.confirmarCita(idCita);  // ya lo tienes en GestorCitas

            if (ok) {
                JOptionPane.showMessageDialog(this, "Cita confirmada correctamente.");
                cargarCitasProgramadas();   // recargar lista
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "No se pudo confirmar la cita.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Error al confirmar cita: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
