package logica;

public class Main {
    public static void main(String[] args) {
        String rutaPokedex   = "Pokedex.txt";
        String rutaHabitats  = "Habitats.txt";
        String rutaGimnasios = "Gimnasios.txt";
        String rutaAltoMando = "Alto Mando.txt";
        String rutaRegistros = "Registros.txt";
 
        Juego juego = new Juego();
        try {
            juego.cargarPokedex(rutaPokedex);
            juego.cargarHabitats(rutaHabitats);
            juego.cargarGimnasios(rutaGimnasios);
            juego.cargarAltoMando(rutaAltoMando);
 
            System.out.println("=== Bienvenido al Juego Pokemon ===");
            juego.menuInicial(rutaRegistros);
 
        } catch (Exception e) {
            System.out.println("Error al cargar archivos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}