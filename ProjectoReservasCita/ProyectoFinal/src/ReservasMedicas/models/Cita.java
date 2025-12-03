package ReservasMedicas.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Cita {

    private Integer id;
    private Paciente paciente;
    private Medico medico;
    private LocalDate fecha;
    private LocalTime hora;
    private int duracionMin;
    private EstadoCita estado;
    private String motivoCancel;
    private String confirmedAt;

    // Constructor para crear nueva cita
    public Cita(Paciente paciente, Medico medico, LocalDate fecha,
                LocalTime hora, int duracionMin) {

        this.paciente = paciente;
        this.medico = medico;
        this.fecha = fecha;
        this.hora = hora;
        this.duracionMin = duracionMin;
        this.estado = EstadoCita.PROGRAMADA;
        this.motivoCancel = null;
        this.confirmedAt = null;
    }

    // Constructor completo desde BD
    public Cita(Integer id, Paciente paciente, Medico medico,
                LocalDate fecha, LocalTime hora, int duracionMin,
                EstadoCita estado, String motivoCancel, String confirmedAt) {

        this.id = id;
        this.paciente = paciente;
        this.medico = medico;
        this.fecha = fecha;
        this.hora = hora;
        this.duracionMin = duracionMin;
        this.estado = estado;
        this.motivoCancel = motivoCancel;
        this.confirmedAt = confirmedAt;
    }

    // Getters y setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Paciente getPaciente() { return paciente; }
    public Medico getMedico() { return medico; }

    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }

    public int getDuracionMin() { return duracionMin; }

    public EstadoCita getEstado() { return estado; }
    public void setEstado(EstadoCita estado) { this.estado = estado; }

    public String getMotivoCancel() { return motivoCancel; }
    public void setMotivoCancel(String motivoCancel) { this.motivoCancel = motivoCancel; }

    public String getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(String confirmedAt) { this.confirmedAt = confirmedAt; }
}
