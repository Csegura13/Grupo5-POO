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

public class CancelarCita extends JInternalFrame {

    private static final long serialVersionUID = 1L;

    private JTable table;
    private JButton btnCancelarCita;
    private JButton btnCerrar;
    private JButton btnRefrescar;

    private GestorCitas gestor;

    // SOLO para probar este formulario aparte
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                CancelarCita frame = new CancelarCita();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public CancelarCita() {
        setTitle("Cancelar Citas");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setBounds(100, 100, 650, 450);
        getContentPane().setLayout(null);

        JLabel lblTitulo = new JLabel("Cancelar Citas");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblTitulo.setBounds(230, 10, 200, 25);
        getContentPane().add(lblTitulo);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(20, 60, 600, 280);
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

        btnCancelarCita = new JButton("Cancelar Cita");
        btnCancelarCita.setBounds(250, 360, 130, 23);
        getContentPane().add(btnCancelarCita);

        btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(520, 360, 100, 23);
        getContentPane().add(btnCerrar);

        // ==========================
        //  INICIALIZAR SERVICIO BD
        // ==========================
        GestorBD gestorBD = new GestorBD("clinica.db");
        gestor = new GestorCitas(gestorBD);

        cargarCitasPendientes();  // muestra las citas no canceladas

        // ==========================
        //  EVENTOS
        // ==========================
        btnRefrescar.addActionListener(e -> cargarCitasPendientes());

        btnCancelarCita.addActionListener(e -> cancelarSeleccionada());

        btnCerrar.addActionListener(e -> dispose());
    }

    /** Carga citas que NO estén en estado CANCELADA */
    private void cargarCitasPendientes() {
        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0); // limpiar

            List<Cita> citas = gestor.listarCitas();   // usa repo.findAllCitas()

            DateTimeFormatter fFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter fHora  = DateTimeFormatter.ofPattern("HH:mm");

            for (Cita c : citas) {
                // mostramos todas menos las ya canceladas
                if (c.getEstado() != EstadoCita.CANCELADA) {
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

    /** Cancela la cita seleccionada en la tabla */
    private void cancelarSeleccionada() {
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

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int idCita = (int) model.getValueAt(fila, 0);

        String motivo = JOptionPane.showInputDialog(
            this,
            "Ingrese motivo de cancelación:",
            "Motivo",
            JOptionPane.QUESTION_MESSAGE
        );

        if (motivo == null) {
            // usuario canceló el diálogo
            return;
        }

        motivo = motivo.trim();
        if (motivo.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Debe ingresar un motivo.",
                "Motivo requerido",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
            this,
            "¿Cancelar la cita seleccionada?",
            "Confirmar cancelación",
            JOptionPane.YES_NO_OPTION
        );
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean ok = gestor.cancelarCita(idCita, motivo);  // ya lo tienes en GestorCitas

            if (ok) {
                JOptionPane.showMessageDialog(this, "Cita cancelada correctamente.");
                cargarCitasPendientes();   // recargar lista
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "No se pudo cancelar la cita.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Error al cancelar cita: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
