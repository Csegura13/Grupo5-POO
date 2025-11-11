package P3;

import java.util.ArrayList;

public class ListaEstudiantes {
    public static void main(String[] args) {

        // Crear una lista para almacenar nombres de estudiantes
        ArrayList<String> estudiantes = new ArrayList<>();

        // Agregar al menos 5 nombres
        estudiantes.add("Ana");
        estudiantes.add("Bruno");
        estudiantes.add("Carla");
        estudiantes.add("Diego");
        estudiantes.add("Elena");

        // Mostrar la lista completa
        System.out.println("Lista completa de estudiantes:");
        for (String nombre : estudiantes) {
            System.out.println(nombre);
        }

        // Eliminar el tercer nombre (índice 2)
        estudiantes.remove(2);

        // Mostrar la lista después de eliminar el tercer nombre
        System.out.println("\nLista después de eliminar el tercer nombre:");
        for (String nombre : estudiantes) {
            System.out.println(nombre);
        }
    }
}
