package ReservasMedicas.models;

public class Medico {

    private Integer id;
    private Persona persona;
    private String especialidad;
    private String horarioConfig;

    // Constructor sin ID (nuevo doctor)
    public Medico(Persona persona, String especialidad, String horarioConfig) {
        this.persona = persona;
        this.especialidad = especialidad;
        this.horarioConfig = horarioConfig;
    }

    // Constructor completo (recuperado de BD)
    public Medico(Integer id, Persona persona, String especialidad, String horarioConfig) {
        this.id = id;
        this.persona = persona;
        this.especialidad = especialidad;
        this.horarioConfig = horarioConfig;
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

    public String getEspecialidad() {
        return especialidad;
    }
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getHorarioConfig() {
        return horarioConfig;
    }
    public void setHorarioConfig(String horarioConfig) {
        this.horarioConfig = horarioConfig;
    }

    @Override
    public String toString() {
        return persona.getNombre() + " - " + especialidad;
    }
}
