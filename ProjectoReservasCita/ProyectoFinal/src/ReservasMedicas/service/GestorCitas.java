package ReservasMedicas.service;

import ReservasMedicas.models.*;
import ReservasMedicas.conexion.GestorBD;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import ReservasMedicas.models.Cita;
public class GestorCitas {

    private final GestorBD repo;
    private int defaultDuracionMin = 20;

    public GestorCitas(GestorBD repo) {
        this.repo = repo;
    }

    /* ==========================================================
       PACIENTES
       ========================================================== */

    public Paciente registrarPaciente(String dni, String nombre, String telefono) throws Exception {
        Persona per = new Persona(dni, nombre, telefono);
        Paciente p = new Paciente(per);
        return repo.savePaciente(p);
    }

    public boolean editarPaciente(String dni, String nuevoNombre, String nuevoTelefono) throws Exception {
        Paciente p = repo.findPacienteByDni(dni);
        if (p == null) {
            throw new IllegalArgumentException("Paciente no existe");
        }

        p.getPersona().setNombre(nuevoNombre);
        p.getPersona().setTelefono(nuevoTelefono);

        return repo.updatePaciente(p);
    }

    public boolean eliminarPaciente(String dni) throws Exception {
        Paciente p = repo.findPacienteByDni(dni);
        if (p == null) {
            // nada que borrar
            return false;
        }
        return repo.deletePacienteIfNoFutureAppointments(p);
    }

    // buscar un paciente por DNI (para consulta)
    public Paciente buscarPacientePorDni(String dni) throws Exception {
        return repo.findPacienteByDni(dni);
    }

    // lista completa
    public List<Paciente> listarPacientes() throws Exception {
        return repo.findAllPacientes();
    }

    /* ==========================================================
       MÉDICOS
       ========================================================== */

    public Medico registrarMedico(
            String dni,
            String nombre,
            String telefono,
            String especialidad,
            String horarioConfig
    ) throws Exception {

        Persona per = new Persona(dni, nombre, telefono);
        Medico m = new Medico(per, especialidad, horarioConfig);
        return repo.saveMedico(m);
    }

    // editar solo horario por id (lo que ya tenías)
    public boolean editarHorarioMedico(int medicoId, String horarioConfig) throws Exception {
        return repo.updateMedicoHorario(medicoId, horarioConfig);
    }

    // editar todos los datos del médico usando su DNI
    public boolean editarMedico(
            String dni,
            String nombre,
            String telefono,
            String especialidad,
            String horarioConfig
    ) throws Exception {

        Medico m = repo.findMedicoByDni(dni);
        if (m == null) {
            throw new IllegalArgumentException("Médico no existe");
        }

        m.getPersona().setNombre(nombre);
        m.getPersona().setTelefono(telefono);
        m.setEspecialidad(especialidad);
        m.setHorarioConfig(horarioConfig);

        return repo.updateMedico(m);
    }

    public boolean eliminarMedico(String dni) throws Exception {
        Medico m = repo.findMedicoByDni(dni);
        if (m == null) {
            return false;
        }
        // igual que con paciente, pero para médicos
        return repo.deleteMedicoIfNoFutureAppointments(m);
    }

    public Medico buscarMedicoPorDni(String dni) throws Exception {
        return repo.findMedicoByDni(dni);
    }

    public List<Medico> listarMedicos() throws Exception {
        return repo.findAllMedicos();
    }

    /* ==========================================================
       CITAS
       ========================================================== */

    public Cita agendarCita(
            String pacienteDni,
            int medicoId,
            LocalDate fecha,
            LocalTime hora
    ) throws Exception {

        Paciente p = repo.findPacienteByDni(pacienteDni);
        if (p == null) {
            throw new IllegalArgumentException("Paciente no encontrado");
        }

        Medico m = repo.findMedicoById(medicoId);
        if (m == null) {
            throw new IllegalArgumentException("Médico no encontrado");
        }

        int dur = defaultDuracionMin;

        // Usa el constructor que tú ya tienes: Cita(paciente, medico, fecha, hora, duracionMin)
        Cita c = new Cita(p, m, fecha, hora, dur);

        return repo.saveCita(c);
    }

    public boolean cancelarCita(int id, String motivo) throws Exception {
        return repo.cancelarCita(id, motivo);
    }

    public boolean confirmarCita(int id) throws Exception {
        String now = LocalDateTime.now().toString();
        return repo.confirmarCita(id, now);
    }
    public List<Cita> listarCitas() throws Exception {
        return repo.findAllCitas();
    }
    

}
