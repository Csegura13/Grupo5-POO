package CasodeEstudio.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {
        private static int contador = 0;
    private int codigo;
    private Cliente cliente;
    private Entrenador entrenador;
    private Horario horario;
    private String comentario;

    // Clase anidada
    public static class Horario {
        private LocalDate fecha;
        private LocalTime hora;

        public Horario(LocalDate fecha, LocalTime hora) {
            this.fecha = fecha;
            this.hora = hora;
        }

        public LocalDate getFecha() { return fecha; }
        public LocalTime getHora() { return hora; }

        @Override
        public String toString() {
            return fecha + " " + hora;
        }
    }

    public Reserva(Cliente cliente, Entrenador entrenador, Horario horario, String comentario) {
        this.codigo = ++contador;
        this.cliente = cliente;
        this.entrenador = entrenador;
        this.horario = horario;
        this.comentario = comentario;
    }

    public int getCodigo() { return codigo; }
    public Cliente getCliente() { return cliente; }
    public Entrenador getEntrenador() { return entrenador; }
    public Horario getHorario() { return horario; }

    @Override
    public String toString() {
        return "Reserva #" + codigo + " [" + cliente.getNombre() + " con " +
               entrenador.getNombre() + " - " + horario + "]";
    }
}
