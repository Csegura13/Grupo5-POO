package P2;

import java.util.Scanner;

public class Division {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Ingrese el primer número entero: ");
            int num1 = scanner.nextInt();

            System.out.print("Ingrese el segundo número entero: ");
            int num2 = scanner.nextInt();

            int resultado = num1 / num2;
            System.out.println("Resultado de la división: " + resultado);

        } catch (ArithmeticException e) {
            System.out.println("Error: división entre cero no permitida.");
        } catch (Exception e) {
            System.out.println("Error: entrada inválida.");
        } finally {
            scanner.close();
        }
    }
}
