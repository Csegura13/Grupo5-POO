package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import ReservasMedicas.conexion.GestorBD;
import ReservasMedicas.models.Cita;
import ReservasMedicas.service.GestorCitas;

import java.awt.Font;
import java.util.List;

public class ListarCitasPorPaciente extends JInternalFrame {

    private static final long serialVersionUID = 1L;
    private JTextField txtDni;
    private JTable table;
    private GestorCitas gestor;

    public ListarCitasPorPaciente() {
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Listar Citas por Paciente");
        setBounds(100, 100, 700, 450);
        getContentPane().setLayout(null);

        JLabel lblTitulo = new JLabel("Listado de Citas por Paciente");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitulo.setBounds(190, 10, 320, 25);
        getContentPane().add(lblTitulo);

        JLabel lblDni = new JLabel("DNI Paciente:");
        lblDni.setBounds(20, 55, 80, 20);
        getContentPane().add(lblDni);

        txtDni = new JTextField();
        txtDni.setBounds(110, 55, 120, 20);
        getContentPane().add(txtDni);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(250, 54, 90, 23);
        getContentPane().add(btnBuscar);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(350, 54, 90, 23);
        getContentPane().add(btnCerrar);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(20, 100, 650, 280);
        getContentPane().add(scrollPane);

        table = new JTable();
        table.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Cita", "Paciente", "Doctor", "Fecha", "Hora", "Estado"}
        ));
        scrollPane.setViewportView(table);

        // servicio
        GestorBD gestorBD = new GestorBD("clinica.db");
        gestor = new GestorCitas(gestorBD);

        btnBuscar.addActionListener(e -> buscarPorPaciente());
        btnCerrar.addActionListener(e -> dispose());
    }

    private void buscarPorPaciente() {
        String dni = txtDni.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese DNI del paciente.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Cita> todas = gestor.listarCitas();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            for (Cita c : todas) {
                if (c.getPaciente().getPersona().getDni().equals(dni)) {
                    model.addRow(new Object[]{
                            c.getId(),
                            c.getPaciente().getPersona().getNombre(),
                            c.getMedico().getPersona().getNombre()
                                    + " - " + c.getMedico().getEspecialidad(),
                            c.getFecha(),
                            c.getHora(),
                            c.getEstado().name()
                    });
                }
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "No hay citas para ese paciente.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al buscar citas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
