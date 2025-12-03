package ReservasMedicas.validacion;

public class ValidacionHoraria {

    /**
     * Verifica si una hora (HH:mm) está dentro de un rango "08:00 - 12:30".
     */
    public static boolean horaDentroDeRango(String hora, String rango) {

        try {
            String[] partes = rango.split("-");
            String inicio = partes[0].trim();
            String fin = partes[1].trim();

            int h = convertirMinutos(hora);
            int hInicio = convertirMinutos(inicio);
            int hFin = convertirMinutos(fin);

            return h >= hInicio && h <= hFin;

        } catch (Exception e) {
            return false;
        }
    }

    /** Convierte "08:30" → 510 minutos */
    private static int convertirMinutos(String hhmm) {
        String[] partes = hhmm.split(":");
        int h = Integer.parseInt(partes[0].trim());
        int m = Integer.parseInt(partes[1].trim());
        return h * 60 + m;
    }
}
