package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import ReservasMedicas.conexion.GestorBD;
import ReservasMedicas.models.Cita;
import ReservasMedicas.service.GestorCitas;

import java.awt.Font;
import java.time.LocalDate;
import java.util.List;

public class ListarCitasPorFecha extends JInternalFrame {

    private static final long serialVersionUID = 1L;
    private JTextField txtFecha;
    private JTable table;
    private GestorCitas gestor;

    public ListarCitasPorFecha() {
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Listar Citas por Fecha");
        setBounds(100, 100, 700, 450);
        getContentPane().setLayout(null);

        JLabel lblTitulo = new JLabel("Listado de Citas por Fecha");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitulo.setBounds(200, 10, 300, 25);
        getContentPane().add(lblTitulo);

        JLabel lblFecha = new JLabel("Fecha (yyyy-MM-dd):");
        lblFecha.setBounds(20, 55, 130, 20);
        getContentPane().add(lblFecha);

        txtFecha = new JTextField();
        txtFecha.setBounds(160, 55, 100, 20);
        getContentPane().add(txtFecha);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(280, 54, 90, 23);
        getContentPane().add(btnBuscar);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(380, 54, 90, 23);
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

        btnBuscar.addActionListener(e -> buscarPorFecha());
        btnCerrar.addActionListener(e -> dispose());
    }

    private void buscarPorFecha() {
        String fechaStr = txtFecha.getText().trim();
        if (fechaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese una fecha.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate fecha = LocalDate.parse(fechaStr);

            List<Cita> todas = gestor.listarCitas(); // todas las citas
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            for (Cita c : todas) {
                if (c.getFecha().equals(fecha)) {
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
                JOptionPane.showMessageDialog(this, "No hay citas para esa fecha.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al buscar citas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
