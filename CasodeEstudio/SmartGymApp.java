package CasodeEstudio;

import CasodeEstudio.model.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class SmartGymApp {
    // Requisito: usar ArrayList
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Entrenador> entrenadores = new ArrayList<>();
    private final List<Reserva> reservas = new ArrayList<>();
    private final List<String> logErrores = new ArrayList<>();

    private final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        new SmartGymApp().run();
    }

    private void run() {
        int op;
        do {
            System.out.println("\n===== SMARTGYM =====");
            System.out.println("1) Registrar CLIENTE");
            System.out.println("2) Registrar ENTRENADOR");
            System.out.println("3) Crear RESERVA");
            System.out.println("4) Listar RESERVAS");
            System.out.println("5) Historial por CLIENTE");
            System.out.println("6) Ver LOG de errores");
            System.out.println("0) Salir");
            System.out.print("Opción: ");
            op = leerEntero();

            switch (op) {
                case 1 -> registrarClienteUI();
                case 2 -> registrarEntrenadorUI();
                case 3 -> crearReservaUI();
                case 4 -> listarReservasUI();
                case 5 -> historialClienteUI();
                case 6 -> verLogUI();
                case 0 -> System.out.println("Hasta pronto 👋");
                default -> System.out.println("Opción inválida.");
            }
        } while (op != 0);
    }

    // ========== UI ==========
    private void registrarClienteUI() {
        System.out.print("ID Cliente: ");
        String id = sc.nextLine().trim();
        System.out.print("Nombre: ");
        String nombre = sc.nextLine().trim();
        System.out.print("Edad: ");
        int edad = leerEntero();

        // Validación simple: ID único
        if (buscarCliente(id).isPresent()) {
            System.out.println("Ya existe un cliente con ese ID.");
            return;
        }
        clientes.add(new Cliente(id, nombre, edad));
        System.out.println("Cliente registrado.");
    }

    private void registrarEntrenadorUI() {
        System.out.print("ID Entrenador: ");
        String id = sc.nextLine().trim();
        System.out.print("Nombre: ");
        String nombre = sc.nextLine().trim();
        System.out.print("Especialidad: ");
        String esp = sc.nextLine().trim();

        if (buscarEntrenador(id).isPresent()) {
            System.out.println("Ya existe un entrenador con ese ID.");
            return;
        }
        entrenadores.add(new Entrenador(id, nombre, esp));
        System.out.println("Entrenador registrado.");
    }

    private void crearReservaUI() {
        try {
            if (clientes.isEmpty() || entrenadores.isEmpty()) {
                System.out.println("Antes registra al menos un cliente y un entrenador.");
                return;
            }

            System.out.print("ID Cliente: ");
            String idC = sc.nextLine().trim();
            Cliente c = buscarCliente(idC).orElseThrow(() ->
                    new IllegalArgumentException("Cliente no existe."));

            System.out.print("ID Entrenador: ");
            String idE = sc.nextLine().trim();
            Entrenador e = buscarEntrenador(idE).orElseThrow(() ->
                    new IllegalArgumentException("Entrenador no existe."));

            System.out.print("Fecha (AAAA-MM-DD): ");
            LocalDate fecha = LocalDate.parse(sc.nextLine().trim());
            System.out.print("Hora (HH:MM): ");
            LocalTime hora = LocalTime.parse(sc.nextLine().trim());

            Reserva.Horario h = new Reserva.Horario(fecha, hora);

            // Validación: entrenador libre en ese horario
            if (entrenadorOcupado(e, h)) {
                throw new IllegalStateException("El entrenador ya tiene reserva en ese horario.");
            }

            System.out.print("Comentario (opcional): ");
            String com = sc.nextLine();
            Reserva r = new Reserva(c, e, h, com);
            reservas.add(r);
            System.out.println("Reserva creada: " + r);

        } catch (Exception ex) {
            logErrores.add(now() + " " + ex.getMessage());
            System.out.println("No se pudo crear la reserva: " + ex.getMessage());
        }
    }

    private void listarReservasUI() {
        if (reservas.isEmpty()) { System.out.println("Sin reservas."); return; }
        System.out.println("=== Reservas registradas ===");
        for (Reserva r : reservas) System.out.println(r);
    }

    private void historialClienteUI() {
        System.out.print("ID Cliente: ");
        String id = sc.nextLine().trim();
        List<Reserva> hist = historialPorCliente(id);
        if (hist.isEmpty()) { System.out.println("Sin reservas para ese cliente."); return; }
        System.out.println("=== Historial de " + id + " ===");
        for (Reserva r : hist) System.out.println(r);
    }

    private void verLogUI() {
        if (logErrores.isEmpty()) { System.out.println("Log vacío."); return; }
        System.out.println("=== Log de errores ===");
        logErrores.forEach(System.out::println);
    }

    // ========== Lógica ==========
    private Optional<Cliente> buscarCliente(String id) {
        return clientes.stream().filter(c -> c.getId().equalsIgnoreCase(id)).findFirst();
    }
    private Optional<Entrenador> buscarEntrenador(String id) {
        return entrenadores.stream().filter(e -> e.getId().equalsIgnoreCase(id)).findFirst();
    }
    private boolean entrenadorOcupado(Entrenador e, Reserva.Horario h) {
        return reservas.stream().anyMatch(r ->
                r.getEntrenador().getId().equalsIgnoreCase(e.getId()) &&
                r.getHorario().toString().equals(h.toString()));
    }
    private List<Reserva> historialPorCliente(String idCliente) {
        List<Reserva> out = new ArrayList<>();
        for (Reserva r : reservas)
            if (r.getCliente().getId().equalsIgnoreCase(idCliente)) out.add(r);
        return out;
    }

    // Helpers
    private int leerEntero() {
        while (true) {
            String s = sc.nextLine().trim();
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e) { System.out.print("Número inválido, intenta de nuevo: "); }
        }
    }
    private String now() { return java.time.LocalDate.now() + " " + java.time.LocalTime.now().withNano(0); }
}
