package caso1;

import java.util.Scanner;

public class UsuarioSimple {
    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);

        System.out.println("=== Registro de Usuario ===");

        System.out.print("Ingrese su nombre: ");
        String nombre = entrada.nextLine();

        System.out.print("Ingrese su edad: ");
        int edad = entrada.nextInt();

        System.out.print("Ingrese su estatura (en metros): ");
        double estatura = entrada.nextDouble();

        System.out.println("\n--- Datos Ingresados ---");
        System.out.println("Nombre: " + nombre);
        System.out.println("Edad: " + edad + " años");
        System.out.println("Estatura: " + estatura + " m");

        entrada.close();
    }
}
