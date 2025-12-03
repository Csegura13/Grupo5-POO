package GUI;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import ReservasMedicas.conexion.GestorBD;
import ReservasMedicas.models.Paciente;
import ReservasMedicas.service.GestorCitas;

public class MantenimientoPersona extends JInternalFrame {

    private static final long serialVersionUID = 1L;

    private JTextField txtDni;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTable table;

    private GestorCitas gestor;

    // DNI del paciente actualmente seleccionado (clave para modificar / eliminar)
    private String dniSeleccionado = null;

    public MantenimientoPersona() {
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Mantenimiento de Personas / Pacientes");
        setBounds(100, 100, 650, 500);
        getContentPane().setLayout(null);

        JLabel lblTitulo = new JLabel("Mantenimiento de Pacientes");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 22));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(10, 10, 614, 30);
        getContentPane().add(lblTitulo);

        JLabel lblDni = new JLabel("DNI:");
        lblDni.setBounds(50, 70, 80, 20);
        getContentPane().add(lblDni);

        JLabel lblNombre = new JLabel("Nombres:");
        lblNombre.setBounds(50, 100, 80, 20);
        getContentPane().add(lblNombre);

        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setBounds(50, 130, 80, 20);
        getContentPane().add(lblTelefono);

        txtDni = new JTextField();
        txtDni.setBounds(140, 70, 150, 20);
        getContentPane().add(txtDni);

        txtNombre = new JTextField();
        txtNombre.setBounds(140, 100, 250, 20);
        getContentPane().add(txtNombre);

        txtTelefono = new JTextField();
        txtTelefono.setBounds(140, 130, 150, 20);
        getContentPane().add(txtTelefono);

        JButton btnNuevo = new JButton("Nuevo");
        btnNuevo.setBounds(50, 170, 100, 25);
        getContentPane().add(btnNuevo);

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(190, 170, 100, 25);
        getContentPane().add(btnAgregar);

        JButton btnModificar = new JButton("Modificar");
        btnModificar.setBounds(356, 170, 100, 25);
        getContentPane().add(btnModificar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(490, 170, 100, 25);
        getContentPane().add(btnEliminar);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(50, 215, 540, 200);
        getContentPane().add(scrollPane);

        table = new JTable();
        table.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] { "DNI", "Nombres", "Teléfono" }
        ));
        scrollPane.setViewportView(table);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(260, 430, 100, 25);
        getContentPane().add(btnCerrar);

        // ======================
        //  INICIALIZAR SERVICIO
        // ======================
        GestorBD gestorBD = new GestorBD("clinica.db");
        gestor = new GestorCitas(gestorBD);

        cargarPacientesEnTabla();

        // ======================
        //   EVENTOS
        // ======================

        // Click en la tabla -> cargar datos y fijar dniSeleccionado (DNI NO editable)
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = table.getSelectedRow();
                if (fila >= 0) {
                    String dni = table.getValueAt(fila, 0).toString();
                    String nombre = table.getValueAt(fila, 1).toString();
                    String telefono = table.getValueAt(fila, 2).toString();

                    dniSeleccionado = dni;

                    txtDni.setText(dni);
                    txtNombre.setText(nombre);
                    txtTelefono.setText(telefono);

                    // Cuando estamos editando un paciente, el DNI no se debe cambiar
                    txtDni.setEditable(false);
                }
            }
        });

        btnNuevo.addActionListener(e -> limpiarCampos());

        btnAgregar.addActionListener(e -> agregarPaciente());

        btnModificar.addActionListener(e -> modificarPaciente());

        btnEliminar.addActionListener(e -> eliminarPaciente());

        btnCerrar.addActionListener(e -> dispose());
    }

    // ======================
    //   MÉTODOS AUXILIARES
    // ======================

    private void limpiarCampos() {
        txtDni.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        txtDni.setEditable(true);  // volvemos a permitir escribir DNI para un nuevo registro
        dniSeleccionado = null;
        table.clearSelection();
        txtDni.requestFocus();
    }

    private void cargarPacientesEnTabla() {
        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            List<Paciente> pacientes = gestor.listarPacientes();
            for (Paciente p : pacientes) {
                model.addRow(new Object[] {
                    p.getPersona().getDni(),
                    p.getPersona().getNombre(),
                    p.getPersona().getTelefono()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Error al cargar pacientes: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private boolean camposObligatoriosVacios() {
        return txtDni.getText().trim().isEmpty()
                || txtNombre.getText().trim().isEmpty()
                || txtTelefono.getText().trim().isEmpty();
    }

    private void agregarPaciente() {
        if (camposObligatoriosVacios()) {
            JOptionPane.showMessageDialog(
                this,
                "Complete DNI, Nombres y Teléfono.",
                "Datos incompletos",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String dni = txtDni.getText().trim();
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();

        try {
            gestor.registrarPaciente(dni, nombre, telefono);
            JOptionPane.showMessageDialog(this, "Paciente registrado correctamente.");
            cargarPacientesEnTabla();
            limpiarCampos();
        } catch (Exception ex) {
            ex.printStackTrace();

            String msg = ex.getMessage();
            if (msg != null && msg.contains("UNIQUE constraint failed: persona.dni")) {
                // Mensaje amigable cuando el DNI ya existe en la tabla persona
                JOptionPane.showMessageDialog(
                    this,
                    "Ya existe un paciente (o persona) registrado con ese DNI.",
                    "Paciente ya existe",
                    JOptionPane.WARNING_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Error al registrar paciente: " + msg,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void modificarPaciente() {
        if (dniSeleccionado == null) {
            JOptionPane.showMessageDialog(
                this,
                "Seleccione un paciente de la tabla para modificar.",
                "Aviso",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        if (camposObligatoriosVacios()) {
            JOptionPane.showMessageDialog(
                this,
                "Complete Nombres y Teléfono.",
                "Datos incompletos",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Usamos el DNI ORIGINAL seleccionado, no el que haya en el campo (no permitimos cambiarlo)
        String dni = dniSeleccionado;
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();

        try {
            boolean ok = gestor.editarPaciente(dni, nombre, telefono);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Paciente modificado correctamente.");
                cargarPacientesEnTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "No se pudo modificar el paciente.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Error al modificar paciente: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void eliminarPaciente() {
        if (dniSeleccionado == null) {
            JOptionPane.showMessageDialog(
                this,
                "Seleccione un paciente de la tabla para eliminar.",
                "Aviso",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        int resp = JOptionPane.showConfirmDialog(
            this,
            "¿Seguro que desea eliminar al paciente con DNI " + dniSeleccionado + "?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION
        );

        if (resp != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean ok = gestor.eliminarPaciente(dniSeleccionado);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Paciente eliminado correctamente.");
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "No se pudo eliminar: el paciente tiene citas asociadas.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
                );
            }
            cargarPacientesEnTabla();
            limpiarCampos();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Error al eliminar paciente: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
