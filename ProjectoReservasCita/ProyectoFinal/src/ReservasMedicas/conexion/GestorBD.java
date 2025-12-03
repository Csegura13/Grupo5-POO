package ReservasMedicas.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;


import java.time.LocalDate;
import java.time.LocalTime;

import ReservasMedicas.models.Cita;
import ReservasMedicas.models.EstadoCita;
import ReservasMedicas.models.Medico;
import ReservasMedicas.models.Paciente;
import ReservasMedicas.models.Persona;

public class GestorBD {

    private final String url;

    public GestorBD(String dbFile) {
        if (dbFile == null) {
            throw new IllegalArgumentException("dbFile null");
        }
        if (dbFile.startsWith("jdbc:")) {
            this.url = dbFile;
        } else {
            this.url = "jdbc:sqlite:" + dbFile;
        }
    }

    /** Devuelve una nueva conexión */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    /** Crea tablas si no existen */
    public void init() {
        try (Connection cn = getConnection();
             Statement st = cn.createStatement()) {

            st.execute("PRAGMA foreign_keys = ON");

            st.execute("""
                CREATE TABLE IF NOT EXISTS persona (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    dni TEXT UNIQUE NOT NULL,
                    nombre TEXT NOT NULL,
                    telefono TEXT NOT NULL
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS paciente (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    persona_id INTEGER NOT NULL,
                    historial TEXT,
                    FOREIGN KEY (persona_id) REFERENCES persona(id)
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS medico (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    persona_id INTEGER NOT NULL,
                    especialidad TEXT NOT NULL,
                    horario_config TEXT,
                    FOREIGN KEY (persona_id) REFERENCES persona(id)
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS cita (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    paciente_id INTEGER NOT NULL,
                    medico_id INTEGER NOT NULL,
                    fecha TEXT NOT NULL,
                    hora TEXT NOT NULL,
                    duracion_min INTEGER NOT NULL,
                    estado TEXT NOT NULL,
                    motivo_cancel TEXT,
                    confirmed_at TEXT,
                    FOREIGN KEY (paciente_id) REFERENCES paciente(id),
                    FOREIGN KEY (medico_id) REFERENCES medico(id)
                )
            """);

        } catch (SQLException e) {
            throw new RuntimeException("Error inicializando BD", e);
        }
    }

    /* ==========================================================
       PERSONA / PACIENTE
       ========================================================== */

    private Integer insertPersona(Connection cn, Persona per) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "INSERT INTO persona(dni, nombre, telefono) VALUES (?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, per.getDni());
            ps.setString(2, per.getNombre());
            ps.setString(3, per.getTelefono());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener id generado de persona");
    }

    /** Inserta paciente nuevo (inserta persona + paciente) */
    public Paciente savePaciente(Paciente p) throws SQLException {
        if (p == null || p.getPersona() == null) {
            throw new IllegalArgumentException("Paciente o persona nulos");
        }

        try (Connection cn = getConnection()) {
            cn.setAutoCommit(false);
            try {
                Integer personaId = insertPersona(cn, p.getPersona());

                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO paciente(persona_id, historial) VALUES (?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, personaId);
                    ps.setString(2, p.getHistorial() == null ? "" : p.getHistorial());
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            p.setId(rs.getInt(1));
                        }
                    }
                }

                p.getPersona().setId(personaId);
                cn.commit();
                return p;

            } catch (SQLException e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }

    /** Busca paciente + persona por DNI */
    public Paciente findPacienteByDni(String dni) throws SQLException {
        String sql = """
            SELECT pa.id AS paciente_id,
                   pe.id AS persona_id,
                   pe.dni,
                   pe.nombre,
                   pe.telefono,
                   pa.historial
            FROM paciente pa
            JOIN persona pe ON pa.persona_id = pe.id
            WHERE pe.dni = ?
        """;

        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Persona per = new Persona(
                            rs.getInt("persona_id"),
                            rs.getString("dni"),
                            rs.getString("nombre"),
                            rs.getString("telefono")
                    );

                    Paciente p = new Paciente(
                            rs.getInt("paciente_id"),
                            per,
                            rs.getString("historial")
                    );
                    return p;
                }
            }
        }
        return null;
    }

    /** Actualiza nombre / teléfono / historial */
    public boolean updatePaciente(Paciente p) throws SQLException {
        if (p == null || p.getId() == null ||
            p.getPersona() == null || p.getPersona().getId() == null) {
            throw new IllegalArgumentException("Paciente sin id");
        }

        try (Connection cn = getConnection()) {
            cn.setAutoCommit(false);
            try {
                // actualizar persona
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE persona SET nombre = ?, telefono = ? WHERE id = ?")) {
                    ps.setString(1, p.getPersona().getNombre());
                    ps.setString(2, p.getPersona().getTelefono());
                    ps.setInt(3, p.getPersona().getId());
                    ps.executeUpdate();
                }

                // actualizar paciente
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE paciente SET historial = ? WHERE id = ?")) {
                    ps.setString(1, p.getHistorial());
                    ps.setInt(2, p.getId());
                    ps.executeUpdate();
                }

                cn.commit();
                return true;

            } catch (SQLException e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }

    /** Borra paciente (y su persona) solo si no tiene citas */
    public boolean deletePacienteIfNoFutureAppointments(Paciente p) throws SQLException {
        if (p == null || p.getId() == null) {
            throw new IllegalArgumentException("Paciente sin id");
        }

        try (Connection cn = getConnection()) {

            // comprobar si tiene citas
            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT COUNT(*) FROM cita WHERE paciente_id = ?")) {
                ps.setInt(1, p.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return false; // tiene citas, no se borra
                    }
                }
            }

            cn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = cn.prepareStatement(
                        "DELETE FROM paciente WHERE id = ?")) {
                    ps.setInt(1, p.getId());
                    ps.executeUpdate();
                }

                if (p.getPersona() != null && p.getPersona().getId() != null) {
                    try (PreparedStatement ps = cn.prepareStatement(
                            "DELETE FROM persona WHERE id = ?")) {
                        ps.setInt(1, p.getPersona().getId());
                        ps.executeUpdate();
                    }
                }

                cn.commit();
                return true;

            } catch (SQLException e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }
    // Devuelve todos los pacientes (con su persona)
    public List<Paciente> findAllPacientes() throws SQLException {
        String sql = """
            SELECT pa.id AS paciente_id,
                   pe.id AS persona_id,
                   pe.dni,
                   pe.nombre,
                   pe.telefono,
                   pa.historial
            FROM paciente pa
            JOIN persona pe ON pa.persona_id = pe.id
            ORDER BY pe.nombre
        """;

        List<Paciente> lista = new ArrayList<>();

        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Persona per = new Persona(
                        rs.getInt("persona_id"),
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("telefono")
                );

                Paciente p = new Paciente(
                        rs.getInt("paciente_id"),
                        per,
                        rs.getString("historial")
                );
                lista.add(p);
            }
        }
        return lista;
    }

    /* ==========================================================
       MEDICO
       ========================================================== */

    public Medico saveMedico(Medico m) throws SQLException {
        if (m == null || m.getPersona() == null) {
            throw new IllegalArgumentException("Médico o persona nulos");
        }

        try (Connection cn = getConnection()) {
            cn.setAutoCommit(false);
            try {
                Integer personaId = insertPersona(cn, m.getPersona());

                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO medico(persona_id, especialidad, horario_config) VALUES (?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, personaId);
                    ps.setString(2, m.getEspecialidad());
                    ps.setString(3, m.getHorarioConfig());
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            m.setId(rs.getInt(1));
                        }
                    }
                }

                m.getPersona().setId(personaId);
                cn.commit();
                return m;

            } catch (SQLException e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }

    public boolean updateMedicoHorario(int medId, String horarioConfig) throws SQLException {
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "UPDATE medico SET horario_config = ? WHERE id = ?")) {
            ps.setString(1, horarioConfig);
            ps.setInt(2, medId);
            return ps.executeUpdate() > 0;
        }
    }

    public Medico findMedicoById(int id) throws SQLException {
        String sql = """
            SELECT m.id AS medico_id,
                   p.id AS persona_id,
                   p.dni,
                   p.nombre,
                   p.telefono,
                   m.especialidad,
                   m.horario_config
            FROM medico m
            JOIN persona p ON m.persona_id = p.id
            WHERE m.id = ?
        """;

        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Persona per = new Persona(
                            rs.getInt("persona_id"),
                            rs.getString("dni"),
                            rs.getString("nombre"),
                            rs.getString("telefono")
                    );

                    Medico m = new Medico(
                            rs.getInt("medico_id"),
                            per,
                            rs.getString("especialidad"),
                            rs.getString("horario_config")
                    );
                    return m;
                }
            }
        }
        return null;
    }
 // Busca un médico por DNI
    public Medico findMedicoByDni(String dni) throws SQLException {
        String sql = "SELECT m.id, m.persona_id, m.especialidad, m.horario_config, " +
                     "p.dni, p.nombre, p.telefono " +
                     "FROM medico m JOIN persona p ON m.persona_id = p.id " +
                     "WHERE p.dni = ?";

        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Persona per = new Persona(
                            rs.getInt("persona_id"),
                            rs.getString("dni"),
                            rs.getString("nombre"),
                            rs.getString("telefono")
                    );
                    Medico m = new Medico(
                            rs.getInt("id"),
                            per,
                            rs.getString("especialidad"),
                            rs.getString("horario_config")
                    );
                    return m;
                }
                return null;
            }
        }
    }
 // Actualiza todos los datos del médico (persona + medico)
    public boolean updateMedico(Medico m) throws SQLException {
        if (m == null ||
            m.getId() == null ||
            m.getPersona() == null ||
            m.getPersona().getId() == null) {
            throw new IllegalArgumentException("Médico sin id");
        }

        try (Connection cn = getConnection()) {
            cn.setAutoCommit(false);
            try {
                // actualizar persona
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE persona SET nombre = ?, telefono = ? WHERE id = ?")) {
                    ps.setString(1, m.getPersona().getNombre());
                    ps.setString(2, m.getPersona().getTelefono());
                    ps.setInt(3, m.getPersona().getId());
                    ps.executeUpdate();
                }

                // actualizar médico
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE medico SET especialidad = ?, horario_config = ? WHERE id = ?")) {
                    ps.setString(1, m.getEspecialidad());
                    ps.setString(2, m.getHorarioConfig());
                    ps.setInt(3, m.getId());
                    ps.executeUpdate();
                }

                cn.commit();
                return true;

            } catch (SQLException e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }
 // Borra médico (y opcionalmente su persona) sólo si no tiene citas
    public boolean deleteMedicoIfNoFutureAppointments(Medico m) throws SQLException {
        if (m == null || m.getId() == null) {
            throw new IllegalArgumentException("Médico sin id");
        }

        try (Connection cn = getConnection()) {

            // comprobar si tiene citas
            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT COUNT(*) FROM cita WHERE medico_id = ?")) {
                ps.setInt(1, m.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        // tiene citas -> no borramos
                        return false;
                    }
                }
            }

            cn.setAutoCommit(false);
            try {
                // borrar de medico
                try (PreparedStatement ps = cn.prepareStatement(
                        "DELETE FROM medico WHERE id = ?")) {
                    ps.setInt(1, m.getId());
                    ps.executeUpdate();
                }

                // si quieres borrar también la persona asociada
                if (m.getPersona() != null && m.getPersona().getId() != null) {
                    try (PreparedStatement ps = cn.prepareStatement(
                            "DELETE FROM persona WHERE id = ?")) {
                        ps.setInt(1, m.getPersona().getId());
                        ps.executeUpdate();
                    }
                }

                cn.commit();
                return true;

            } catch (SQLException e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }

 // Devuelve todos los médicos con sus datos de persona
    public List<Medico> findAllMedicos() throws SQLException {

        String sql = """
            SELECT m.id               AS medico_id,
                   p.id               AS persona_id,
                   p.dni,
                   p.nombre,
                   p.telefono,
                   m.especialidad,
                   m.horario_config
            FROM medico m
            JOIN persona p ON m.persona_id = p.id
            ORDER BY p.nombre
        """;

        List<Medico> lista = new ArrayList<>();

        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Persona per = new Persona(
                        rs.getInt("persona_id"),
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("telefono")
                );

                Medico m = new Medico(
                        rs.getInt("medico_id"),
                        per,
                        rs.getString("especialidad"),
                        rs.getString("horario_config")
                );

                lista.add(m);
            }
        }

        return lista;
    }

    /* ==========================================================
       CITA
       ========================================================== */

    public Cita saveCita(Cita c) throws SQLException {
        if (c == null || c.getPaciente() == null || c.getMedico() == null) {
            throw new IllegalArgumentException("Cita inválida");
        }

        String sql = """
            INSERT INTO cita(
                paciente_id, medico_id, fecha, hora,
                duracion_min, estado, motivo_cancel, confirmed_at
            ) VALUES (?,?,?,?,?,?,?,?)
        """;

        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, c.getPaciente().getId());
            ps.setInt(2, c.getMedico().getId());
            ps.setString(3, c.getFecha().toString());
            ps.setString(4, c.getHora().toString());
            ps.setInt(5, c.getDuracionMin());
            ps.setString(6, c.getEstado().name());   // si EstadoCita es enum
            ps.setString(7, c.getMotivoCancel());
            ps.setString(8, c.getConfirmedAt());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setId(rs.getInt(1));
                }
            }
            return c;
        }
    }
 // En GestorBD.java

    public boolean cancelarCita(int id, String motivo) throws SQLException {
        String sql = "UPDATE cita SET estado = ?, motivo_cancel = ? WHERE id = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, EstadoCita.CANCELADA.name()); // si EstadoCita es enum
            ps.setString(2, motivo);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean confirmarCita(int id, String confirmedAt) throws SQLException {
        String sql = "UPDATE cita SET estado = ?, confirmed_at = ? WHERE id = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, EstadoCita.CONFIRMADA.name()); // ajusta a tu enum
            ps.setString(2, confirmedAt);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }
 // Devuelve todas las citas con sus pacientes y médicos
    public List<Cita> findAllCitas() throws SQLException {

        String sql = """
            SELECT
                c.id              AS cita_id,
                c.fecha,
                c.hora,
                c.duracion_min,
                c.estado,
                c.motivo_cancel,
                c.confirmed_at,

                pa.id             AS paciente_id,
                p1.id             AS persona_pac_id,
                p1.dni            AS pac_dni,
                p1.nombre         AS pac_nombre,
                p1.telefono       AS pac_tel,
                pa.historial      AS pac_historial,

                m.id              AS medico_id,
                p2.id             AS persona_med_id,
                p2.dni            AS med_dni,
                p2.nombre         AS med_nombre,
                p2.telefono       AS med_tel,
                m.especialidad,
                m.horario_config
            FROM cita c
            JOIN paciente pa ON c.paciente_id = pa.id
            JOIN persona p1  ON pa.persona_id = p1.id
            JOIN medico  m   ON c.medico_id   = m.id
            JOIN persona p2  ON m.persona_id  = p2.id
            ORDER BY c.id
        """;

        List<Cita> lista = new ArrayList<>();

        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                // Persona del paciente
                Persona perPac = new Persona(
                        rs.getInt("persona_pac_id"),
                        rs.getString("pac_dni"),
                        rs.getString("pac_nombre"),
                        rs.getString("pac_tel")
                );

                // Paciente
                Paciente pac = new Paciente(
                        rs.getInt("paciente_id"),
                        perPac,
                        rs.getString("pac_historial")
                );

                // Persona del médico
                Persona perMed = new Persona(
                        rs.getInt("persona_med_id"),
                        rs.getString("med_dni"),
                        rs.getString("med_nombre"),
                        rs.getString("med_tel")
                );

                // Médico
                Medico med = new Medico(
                        rs.getInt("medico_id"),
                        perMed,
                        rs.getString("especialidad"),
                        rs.getString("horario_config")
                );

             // Cita
                Cita c = new Cita(
                        pac,
                        med,
                        LocalDate.parse(rs.getString("fecha")),
                        LocalTime.parse(rs.getString("hora")),
                        rs.getInt("duracion_min")
                );

                // completar campos adicionales
                c.setId(rs.getInt("cita_id"));
                c.setEstado(EstadoCita.valueOf(rs.getString("estado")));
                c.setMotivoCancel(rs.getString("motivo_cancel"));
                c.setConfirmedAt(rs.getString("confirmed_at"));

                lista.add(c);
            }
        }

        return lista;
    }


 
}
