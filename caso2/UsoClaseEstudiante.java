
public class UsoClaseEstudiante {
    public static void main(String[] args) {
        Estudiante student = new Estudiante("Grupo 5", 405073, 25, "09993865");

        System.out.println("Nombre estudiante : " + student.getNombre());
        System.out.println("Código estudiante : " + student.getCodigo());
        System.out.println("Edad estudiante : " + student.getEdad());
        System.out.println("DNI estudiante : " + student.getDni());
    }
}
