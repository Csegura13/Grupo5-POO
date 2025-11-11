package padre;

import hijo.Estudiante;

public class Main {
    public static void main(String[] args) {
        Persona p1 = new Persona("Martín", "Torres", 35);
        System.out.println(p1.generarCorreo());
        System.out.println(p1.datosDeLaPersona());
        
        Estudiante estudiante1 = new Estudiante("Ana", "García", 22, 14, 16, 18);
        System.out.println(estudiante1.datosCompletos());
    }
}
