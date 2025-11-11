package CasodeEstudio.model;

public class Entrenador {
    private String id;
    private String nombre;
    private String especialidad;

    public Entrenador(String id, String nombre, String especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEspecialidad() { return especialidad; }

    @Override
    public String toString() {
        return nombre + " - " + especialidad;
    }
}
