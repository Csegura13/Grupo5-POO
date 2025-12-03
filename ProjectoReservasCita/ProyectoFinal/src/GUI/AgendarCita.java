package GUI;

import java.awt.EventQueue;
import java.awt.Font;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import ReservasMedicas.conexion.GestorBD;
import ReservasMedicas.models.Cita;
import ReservasMedicas.models.Medico;
import ReservasMedicas.service.GestorCitas;

public class AgendarCita extends JInternalFrame {

    private static final long serialVersionUID = 1L;

    private JTextField txtDniPaciente;
    private JTextField txtFecha;
    private JTextField txtHora;
    private JTable table;
    private JComboBox<Medico> cboDoctor;

    private GestorCitas gestor;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                AgendarCita frame = new AgendarCita();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public AgendarCita() {
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        setTitle("Agendar Citas");
        setBounds(100, 100, 697, 518);
        getContentPane().setLayout(null);

        JLabel lblTitulo = new JLabel("Agendar Citas");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblTitulo.setBounds(230, 11, 200, 25);
        getContentPane().add(lblTitulo);

        JLabel lblPac = new JLabel("DNI Paciente:");
        lblPac.setBounds(30, 55, 80, 14);
        getContentPane().add(lblPac);

        JLabel lblDoc = new JLabel("Doctor:");
        lblDoc.setBounds(30, 80, 80, 14);
        getContentPane().add(lblDoc);

        JLabel lblFecha = new JLabel("Fecha (yyyy-MM-dd):");
        lblFecha.setBounds(30, 105, 130, 14);
        getContentPane().add(lblFecha);

        JLabel lblHora = new JLabel("Hora (HH:mm):");
        lblHora.setBounds(30, 130, 130, 14);
        getContentPane().add(lblHora);

        txtDniPaciente = new JTextField();
        txtDniPaciente.setBounds(170, 52, 120, 20);
        getContentPane().add(txtDniPaciente);

        cboDoctor = new JComboBox<>();
        cboDoctor.setBounds(170, 76, 250, 22);
        getContentPane().add(cboDoctor);

        txtFecha = new JTextField();
        txtFecha.setBounds(170, 102, 120, 20);
        getContentPane().add(txtFecha);

        txtHora = new JTextField();
        txtHora.setBounds(170, 127, 80, 20);
        getContentPane().add(txtHora);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(320, 170, 100, 23);
        getContentPane().add(btnCerrar);

        JButton btnAgendar = new JButton("Agendar Cita");
        btnAgendar.setBounds(170, 170, 130, 23);
        getContentPane().add(btnAgendar);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(30, 220, 620, 230);
        getContentPane().add(scrollPane);

        table = new JTable();
        table.setModel(new DefaultTableModel(
                new Object[][] {},
                new String[] { "ID Cita", "Paciente", "Doctor", "Fecha", "Hora", "Estado" }
        ));
        scrollPane.setViewportView(table);

        // ==========================
        //    INICIALIZAR SERVICIO
        // ==========================
        GestorBD gestorBD = new GestorBD("clinica.db");
        gestor = new GestorCitas(gestorBD);

        cargarDoctoresEnCombo();
        cargarCitasEnTabla();

        // ==========================
        //       EVENTOS
        // ==========================
        btnAgendar.addActionListener(e -> agendarCita());
        btnCerrar.addActionListener(e -> dispose());
    }

    // ------------ MÉTODOS AUXILIARES ----------------

    private void cargarDoctoresEnCombo() {
        try {
            cboDoctor.removeAllItems();
            List<Medico> medicos = gestor.listarMedicos();
            for (Medico m : medicos) {
                cboDoctor.addItem(m); // toString() muestra "nombre - especialidad"
            }
            cboDoctor.setSelectedIndex(-1); // ninguno seleccionado al inicio
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al cargar doctores: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarCitasEnTabla() {
        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0); // limpia la tabla

            List<Cita> citas = gestor.listarCitas();
            for (Cita c : citas) {
                model.addRow(new Object[] {
                        c.getId(),
                        c.getPaciente().getPersona().getNombre(),
                        c.getMedico().getPersona().getNombre() + " - " + c.getMedico().getEspecialidad(),
                        c.getFecha().toString(),
                        c.getHora().toString(),
                        c.getEstado().name()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al cargar citas: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agendarCita() {
        String dniPac = txtDniPaciente.getText().trim();
        String fechaStr = txtFecha.getText().trim();
        String horaStr  = txtHora.getText().trim();
        Medico seleccionado = (Medico) cboDoctor.getSelectedItem();

        if (dniPac.isEmpty() || fechaStr.isEmpty() || horaStr.isEmpty() || seleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Complete DNI, doctor, fecha y hora.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate fecha = LocalDate.parse(fechaStr);
            LocalTime hora  = LocalTime.parse(horaStr);

            // usar id del médico seleccionado
            int medicoId = seleccionado.getId();

            gestor.agendarCita(dniPac, medicoId, fecha, hora);

            // recargar TODAS las citas (ahora verás todas, no solo la última)
            cargarCitasEnTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Cita agendada correctamente.");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al agendar cita: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtDniPaciente.setText("");
        txtFecha.setText("");
        txtHora.setText("");
        cboDoctor.setSelectedIndex(-1);   // importantísimo para que el doctor se “des-seleccione”
        txtDniPaciente.requestFocus();
    }
}
