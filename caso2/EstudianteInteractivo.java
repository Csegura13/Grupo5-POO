package caso2;

import java.util.Scanner;

public class EstudianteInteractivo {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Ingrese el nombre del estudiante: ");
        String nombre = sc.nextLine();

        System.out.print("Ingrese el código del estudiante: ");
        int codigoa = sc.nextInt();

        System.out.print("Ingrese la edad del estudiante: ");
        int edad = sc.nextInt();

        sc.nextLine();
        
        System.out.print("Ingrese el DNI del estudiante: ");
        String dni = sc.nextLine();

        Estudiante student = new Estudiante(nombre, codigoa, edad, dni);

        System.out.println("\n--- Datos del estudiante ---");
        System.out.println("Nombre estudiante : " + student.getNombre());
        System.out.println("Código estudiante : " + student.getCodigo());
        System.out.println("Edad estudiante : " + student.getEdad());
        System.out.println("DNI estudiante : " + student.getDni());

        sc.close();
    }
}

class Estudiante {

    private String nombre, dni;
    private int codigo, edad;

    public Estudiante(String nombre, int codigo, int edad, String dni) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.edad = edad;
        this.dni = dni;
    }

    public void setNombre(String nom) {
        nombre = nom;
    }

    public void setCodigo(int cod) {
        codigo = cod;
    }

    public void setEdad(int age) {
        edad = age;
    }

    public void setDni(String identificador) {
        dni = identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCodigo() {
        return codigo;
    }

    public int getEdad() {
        return edad;
    }

    public String getDni() {
        return dni;
    }

}
