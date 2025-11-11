package hijo;
import padre.Persona;
public class Estudiante extends Persona {
    // Atributos privados
    private int nota1;
    private int nota2;
    private int nota3;

    // Constructor
    public Estudiante(String nombre, String apellido, int edad, int nota1, int nota2, int nota3) {
        super(nombre, apellido, edad);  // Llama al constructor de Persona
        this.nota1 = nota1;
        this.nota2 = nota2;
        this.nota3 = nota3;
    }

    // Método para calcular el promedio
    public double calcularPromedio() {
        return (nota1 + nota2 + nota3) / 3.0;
    }

    // Método para mostrar los datos completos del estudiante
    public String datosCompletos() {
        return datosDeLaPersona() + 
               "\nNota 1: " + nota1 + 
               "\nNota 2: " + nota2 + 
               "\nNota 3: " + nota3 + 
               "\nPromedio: " + calcularPromedio();
    }
}