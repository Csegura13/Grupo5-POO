package padre;

public class Persona {
// Atributos protegidos

    protected String nombre, apellido;

    protected int edad;



    // Constructor

    public Persona(String nombre, String apellido, int edad) {

        this.nombre = nombre;

        this.apellido = apellido;

        this.edad = edad;

    }



    // Método que genera correo

    public String generarCorreo() {

        return nombre.toLowerCase() + "." + apellido.toLowerCase() + "@upn.pe";

    }



    // Método que devuelve los datos

    public String datosDeLaPersona() {

        return "Nombre: " + nombre + "\nApellido: " + apellido + "\nEdad: " + edad;

    }
}
