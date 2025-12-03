package GUI;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.beans.PropertyVetoException;
import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.CardLayout;
import java.awt.Color;

public class MenuPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JDesktopPane escritorio;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MenuPrincipal frame = new MenuPrincipal();
                    frame.setExtendedState(MAXIMIZED_BOTH);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public MenuPrincipal() {

        setTitle("Reservas de Citas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        /* =============== SISTEMAS =============== */
        JMenu mnSistemas = new JMenu("Sistemas");
        mnSistemas.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        menuBar.add(mnSistemas);

        JMenuItem mntmSalir = new JMenuItem("Salir");
        mntmSalir.addActionListener(e -> System.exit(0));
        mnSistemas.add(mntmSalir);

        /* =============== PERSONAS / PACIENTES =============== */
        
        JMenu mnPersonas = new JMenu("Personas");
        mnPersonas.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        menuBar.add(mnPersonas);
        JMenuItem mntmMantPersonas = new JMenuItem("Mantenimiento");
        mntmMantPersonas.addActionListener(e -> abrirFormulario(new MantenimientoPersona(), "MantPersonas"));
        mnPersonas.add(mntmMantPersonas);

        /* =============== DOCTORES =============== */
        JMenu mnDoctores = new JMenu("Doctores");
        mnDoctores.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        menuBar.add(mnDoctores);
        JMenuItem mntmMantDoctores = new JMenuItem("Mantenimiento");
        mntmMantDoctores.addActionListener(
            e -> abrirFormulario(new MantenimientoDoctor(), "MantDoctores")
        );
        mnDoctores.add(mntmMantDoctores);        

        /* =============== CITAS =============== */
        JMenu mnCitas = new JMenu("Citas");
        mnCitas.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        menuBar.add(mnCitas);

        JMenuItem mntmAgendarCitas = new JMenuItem("Agendar Citas");
        mntmAgendarCitas.addActionListener(e -> abrirFormulario(new AgendarCita(), "AgendarCita"));
        mnCitas.add(mntmAgendarCitas);

        JMenuItem mntmConfirmarCitas = new JMenuItem("Confirmar Citas");
        mntmConfirmarCitas.addActionListener(e -> abrirFormulario(new ConfirmarCita(), "ConfirmarCita"));
        mnCitas.add(mntmConfirmarCitas);

        JMenuItem mntmCancelarCitas = new JMenuItem("Cancelar Citas");
        mntmCancelarCitas.addActionListener(e -> abrirFormulario(new CancelarCita(), "CancelarCita"));
        mnCitas.add(mntmCancelarCitas);

        /* =============== REPORTES / LISTADOS =============== */
        JMenu mnReportes = new JMenu("Reportes");
        mnReportes.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        menuBar.add(mnReportes);

        JMenuItem mntmListarPorFecha = new JMenuItem("Listar citas por fecha");
        mntmListarPorFecha.addActionListener(e ->
                abrirFormulario(new ListarCitasPorFecha(), "ListarCitasPorFecha"));
        mnReportes.add(mntmListarPorFecha);

        JMenuItem mntmListarPorPaciente = new JMenuItem("Listar citas por paciente");
        mntmListarPorPaciente.addActionListener(e ->
                abrirFormulario(new ListarCitasPorPaciente(), "ListarCitasPorPaciente"));
        mnReportes.add(mntmListarPorPaciente);

        JMenuItem mntmExportarExcel = new JMenuItem("Exportar reporte a Excel");
        mntmExportarExcel.addActionListener(e ->
                abrirFormulario(new ExportarCitasExcel(), "ExportarCitasExcel"));
        mnReportes.add(mntmExportarExcel);

        /* =============== PANEL BASE =============== */
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new CardLayout(0, 0));

        escritorio = new JDesktopPane();
        escritorio.setBackground(new Color(176, 196, 222));
        contentPane.add(escritorio, "name_Desktop");
    }

    /* ============================================================
     *   MÉTODO PARA ABRIR FORMULARIOS SIN DUPLICAR POR NOMBRE
     * ============================================================ */
    private void abrirFormulario(JInternalFrame form, String nombre) {

        // Verificar si ya existe una ventana con ese nombre
        for (JInternalFrame f : escritorio.getAllFrames()) {
            if (nombre.equals(f.getName())) {

                try {
                    f.setIcon(false);
                    f.setSelected(true);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();
                }

                // Centrarla nuevamente
                Dimension desk = escritorio.getSize();
                Dimension frm = f.getSize();
                f.setLocation((desk.width - frm.width) / 2, (desk.height - frm.height) / 2);

                return; // NO abrir otra
            }
        }

        // Abrir una nueva
        form.setName(nombre);  // MUY IMPORTANTE
        escritorio.add(form);

        Dimension desk = escritorio.getSize();
        Dimension frm = form.getSize();
        form.setLocation((desk.width - frm.width) / 2, (desk.height - frm.height) / 2);

        form.setVisible(true);
    }
}
