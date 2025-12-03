package ReservasMedicas;

import ReservasMedicas.conexion.GestorBD;
import GUI.MenuPrincipal;
import java.awt.EventQueue;
import javax.swing.JFrame;
public class Main {

    public static void main(String[] args) {

        // 1. Inicializar base de datos
        GestorBD gestor = new GestorBD("clinica.db");
        gestor.init();   // ya NO lanza SQLException

        // 2. Lanzar la GUI
        EventQueue.invokeLater(() -> {
            MenuPrincipal frame = new MenuPrincipal();
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        });
    }
}
