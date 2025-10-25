package caso3;

import java.util.Scanner;

/**
 * Caso 3: Clase CuentaBancaria con validación.
 * Permite realizar depósitos y retiros, validando que no se retire más del saldo disponible.
 */
public class CuentaBancaria {

    // Atributos privados
    private String titular;
    private double saldo;

    // Constructor
    public CuentaBancaria(String titular, double saldoInicial) {
        this.titular = titular;
        this.saldo = saldoInicial;
    }

    // Métodos Getter
    public String getTitular() {
        return titular;
    }

    public double getSaldo() {
        return saldo;
    }

    // Método para depositar dinero
    public void depositar(double monto) {
        if (monto > 0) {
            saldo += monto;
            System.out.println("Depósito realizado con éxito. Nuevo saldo: S/ " + String.format("%.2f", saldo));
        } else {
            System.out.println("El monto a depositar debe ser positivo.");
        }
    }

    // Método para retirar dinero
    public void retirar(double monto) {
        if (monto <= 0) {
            System.out.println("El monto a retirar debe ser positivo.");
        } else if (monto > saldo) {
            System.out.println("Fondos insuficientes. No se puede retirar más del saldo actual.");
        } else {
            saldo -= monto;
            System.out.println("Retiro realizado con éxito. Nuevo saldo: S/ " + String.format("%.2f", saldo));
        }
    }

    // Mostrar datos de la cuenta
    public void mostrarDatos() {
        System.out.println("\n--- Datos de la Cuenta Bancaria ---");
        System.out.println("Titular: " + titular);
        System.out.println("Saldo actual: S/ " + String.format("%.2f", saldo));
    }

    // Método principal
    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);

        System.out.println("=== Bienvenido al Sistema Bancario ===");
        System.out.print("Ingrese el nombre del titular: ");
        String nombre = entrada.nextLine();

        System.out.print("Ingrese el saldo inicial: ");
        double saldoInicial = entrada.nextDouble();

        CuentaBancaria cuenta = new CuentaBancaria(nombre, saldoInicial);

        int opcion;
        do {
            System.out.println("\n--- Menú de Operaciones ---");
            System.out.println("1. Depositar");
            System.out.println("2. Retirar");
            System.out.println("3. Ver saldo");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = entrada.nextInt();

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el monto a depositar: ");
                    double deposito = entrada.nextDouble();
                    cuenta.depositar(deposito);
                    break;
                case 2:
                    System.out.print("Ingrese el monto a retirar: ");
                    double retiro = entrada.nextDouble();
                    cuenta.retirar(retiro);
                    break;
                case 3:
                    cuenta.mostrarDatos();
                    break;
                case 4:
                    System.out.println("Gracias por usar el sistema bancario. ¡Hasta pronto!");
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente, Por favor.");
            }

        } while (opcion != 4);

        entrada.close();
    }
}
