package P1;

public class Calculadora {
    // Método que suma dos enteros
    public int sumar(int a, int b) {
        return a + b;
    }

    // Método que suma tres enteros
    public int sumar(int a, int b, int c) {
        return a + b + c;
    }

    // Método que suma dos números decimales
    public double sumar(double a, double b) {
        return a + b;
    }

    // Método principal para probar los tres casos
    public static void main(String[] args) {
        Calculadora calc = new Calculadora();

        System.out.println("Suma de dos enteros: " + calc.sumar(5, 10));
        System.out.println("Suma de tres enteros: " + calc.sumar(3, 4, 5));
        System.out.println("Suma de dos decimales: " + calc.sumar(2.5, 4.7));
    }
}
