package GUI;

import javax.swing.*;

import ReservasMedicas.conexion.GestorBD;
import ReservasMedicas.models.Cita;
import ReservasMedicas.service.GestorCitas;

import java.awt.Font;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

public class ExportarCitasExcel extends JInternalFrame {

    private static final long serialVersionUID = 1L;
    private GestorCitas gestor;

    public ExportarCitasExcel() {
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Exportar Citas a Excel (CSV)");
        setBounds(100, 100, 400, 200);
        getContentPane().setLayout(null);

        JLabel lblTitulo = new JLabel("Exportar Reporte de Citas");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitulo.setBounds(60, 20, 280, 25);
        getContentPane().add(lblTitulo);

        JButton btnExportar = new JButton("Exportar a Excel (CSV)");
        btnExportar.setBounds(90, 70, 220, 30);
        getContentPane().add(btnExportar);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(150, 120, 90, 25);
        getContentPane().add(btnCerrar);

        GestorBD gestorBD = new GestorBD("clinica.db");
        gestor = new GestorCitas(gestorBD);

        btnExportar.addActionListener(e -> exportar());
        btnCerrar.addActionListener(e -> dispose());
    }

    private void exportar() {
        try {
            List<Cita> citas = gestor.listarCitas();

            if (citas.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No hay citas para exportar.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Guardar reporte");
            chooser.setSelectedFile(new File("reporte_citas.csv"));

            int result = chooser.showSaveDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File file = chooser.getSelectedFile();
            // aseguramos extensión .csv
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getParentFile(), file.getName() + ".csv");
            }

            try (PrintWriter pw = new PrintWriter(new FileWriter(file, false))) {
                // cabeceras
                pw.println("ID Cita;Paciente;Doctor;Especialidad;Fecha;Hora;Estado");

                for (Cita c : citas) {
                    String linea = String.join(";",
                            String.valueOf(c.getId()),
                            c.getPaciente().getPersona().getNombre(),
                            c.getMedico().getPersona().getNombre(),
                            c.getMedico().getEspecialidad(),
                            c.getFecha().toString(),
                            c.getHora().toString(),
                            c.getEstado().name()
                    );
                    pw.println(linea);
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Reporte exportado correctamente.\nArchivo: " + file.getAbsolutePath(),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al exportar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
