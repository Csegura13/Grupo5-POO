package ReservasMedicas.models;

public class Paciente {

    private Integer id;
    private Persona persona;
    private String historial;

    // Constructor sin ID (nuevo paciente)
    public Paciente(Persona persona) {
        this.persona = persona;
        this.historial = "";
    }

    // Constructor completo (recuperado de BD)
    public Paciente(Integer id, Persona persona, String historial) {
        this.id = id;
        this.persona = persona;
        this.historial = historial;
    }

    // Getters y setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Persona getPersona() {
        return persona;
    }
    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public String getHistorial() {
        return historial;
    }
    public void setHistorial(String historial) {
        this.historial = historial;
    }

    @Override
    public String toString() {
        return persona.getNombre() + " (" + persona.getDni() + ")";
    }
}
