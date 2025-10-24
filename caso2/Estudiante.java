public class Estudiante {

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
