package logica;

import java.util.*;
import java.io.*;
 
public class Juego {
 
    private String nombreJugador;
    private String medallas; 
    private ArrayList<Pokemon> pokemonsJugador; 
    private ArrayList<Pokemon> pokedex;          
    private ArrayList<String> habitats;
    private ArrayList<Gimnasio> gimnasios;
    private ArrayList<altoMando> altoMando;
    private Scanner sc;
 
    public Juego() {
        pokemonsJugador = new ArrayList<>();
        pokedex = new ArrayList<>();
        habitats = new ArrayList<>();
        gimnasios = new ArrayList<>();
        altoMando = new ArrayList<>();
        sc = new Scanner(System.in);
    }
 
    public void cargarPokedex(String ruta) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(ruta));
        String linea;
        while ((linea = br.readLine()) != null) {
            linea = linea.trim();
            if (linea.isEmpty()) continue;
            String[] p = linea.split(";");
            Pokemon pk = new Pokemon(p[0], p[1], Double.parseDouble(p[2]),
                    Integer.parseInt(p[3]), Integer.parseInt(p[4]), Integer.parseInt(p[5]),
                    Integer.parseInt(p[6]), Integer.parseInt(p[7]), Integer.parseInt(p[8]), p[9]);
            pokedex.add(pk);
        }
        br.close();
    }
 
    public void cargarHabitats(String ruta) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(ruta));
        String linea;
        while ((linea = br.readLine()) != null) {
            linea = linea.trim();
            if (!linea.isEmpty()) habitats.add(linea);
        }
        br.close();
    }
 
    public void cargarGimnasios(String ruta) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(ruta));
        String linea;
        while ((linea = br.readLine()) != null) {
            linea = linea.trim();
            if (linea.isEmpty()) continue;
            String[] p = linea.split(";");
            int cant = Integer.parseInt(p[3]);
            ArrayList<String> poks = new ArrayList<>();
            for (int i = 4; i < 4 + cant; i++) poks.add(p[i]);
            gimnasios.add(new Gimnasio(Integer.parseInt(p[0]), p[1], p[2], cant, poks));
        }
        br.close();
    }
 
    public void cargarAltoMando(String ruta) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(ruta));
        String linea;
        while ((linea = br.readLine()) != null) {
            linea = linea.trim();
            if (linea.isEmpty()) continue;
            String[] p = linea.split(";");
            ArrayList<String> poks = new ArrayList<>();
            for (int i = 2; i < p.length; i++) poks.add(p[i]);
            altoMando.add(new altoMando(Integer.parseInt(p[0]), p[1], poks));
        }
        br.close();
    }
 
    public boolean cargarRegistros(String ruta) throws IOException {
        File f = new File(ruta);
        if (!f.exists()) return false;
        BufferedReader br = new BufferedReader(new FileReader(ruta));
        String primera = br.readLine();
        if (primera == null || primera.trim().isEmpty()) { br.close(); return false; }
        String[] header = primera.trim().split(";");
        nombreJugador = header[0];
        medallas = (header.length > 1) ? header[1] : "none";
 
        if (!medallas.equals("none")) {
            String[] lideresDerotados = medallas.split(",");
            for (String lider : lideresDerotados) {
                for (Gimnasio g : gimnasios) {
                    if (g.getLider().equals(lider.trim())) {
                        g.setEstado("Derrotado");
                    }
                }
            }
        }
 
        String linea;
        while ((linea = br.readLine()) != null) {
            linea = linea.trim();
            if (linea.isEmpty()) continue;
            String[] p = linea.split(";");
            String nombrePok = p[0];
            String estadoPok = p[1];
            Pokemon pk = buscarEnPokedex(nombrePok);
            if (pk != null) {
                Pokemon copia = clonarPokemon(pk);
                copia.setEstado(estadoPok);
                pokemonsJugador.add(copia);
            }
        }
        br.close();
        return true;
    }
 
    public void guardarRegistros(String ruta) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(ruta, false));
        StringBuilder lideresDerotados = new StringBuilder();
        for (Gimnasio g : gimnasios) {
            if (g.getEstado().equals("Derrotado")) {
                if (lideresDerotados.length() > 0) lideresDerotados.append(",");
                lideresDerotados.append(g.getLider());
            }
        }
        String medallaStr = lideresDerotados.length() == 0 ? "none" : lideresDerotados.toString();
        bw.write(nombreJugador + ";" + medallaStr);
        bw.newLine();
        for (Pokemon pk : pokemonsJugador) {
            bw.write(pk.getNombre() + ";" + pk.getEstado());
            bw.newLine();
        }
        bw.close();
        System.out.println("Partida guardada correctamente.");
    }
 
    private Pokemon buscarEnPokedex(String nombre) {
        for (Pokemon pk : pokedex) {
            if (pk.getNombre().equalsIgnoreCase(nombre)) return pk;
        }
        return null;
    }
 
    private Pokemon clonarPokemon(Pokemon p) {
        return new Pokemon(p.getNombre(), p.getHabitat(), p.getPorcentajeAparicion(),
                p.getVida(), p.getAtaque(), p.getDefensa(),
                p.getAtaqueEspecial(), p.getDefensaEspecial(), p.getVelocidad(), p.getTipo());
    }
 
    private ArrayList<Pokemon> getEquipo() {
        ArrayList<Pokemon> equipo = new ArrayList<>();
        for (int i = 0; i < Math.min(6, pokemonsJugador.size()); i++) {
            equipo.add(pokemonsJugador.get(i));
        }
        return equipo;
    }
 
    private Pokemon getPokemonActivoEquipo() {
        for (Pokemon pk : getEquipo()) {
            if (pk.getEstado().equals("Vivo")) return pk;
        }
        return null;
    }
 
    private boolean equipoSinVida() {
        return getPokemonActivoEquipo() == null;
    }
 
    private int leerOpcion() {
        while (true) {
            System.out.print("Ingrese Opcion: ");
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Opcion invalida. Intente de nuevo.");
            }
        }
    }
 
    public void menuInicial(String rutaRegistros) throws IOException {
        while (true) {
            System.out.println("\n1) Continuar.");
            System.out.println("2) Nueva Partida.");
            System.out.println("3) Salir.");
            int op = leerOpcion();
            switch (op) {
                case 1:
                    boolean cargado = cargarRegistros(rutaRegistros);
                    if (!cargado || nombreJugador == null || nombreJugador.isEmpty()) {
                        System.out.println("No se encontro una partida guardada. Inicie una nueva.");
                    } else {
                        System.out.println("Bienvenido de nuevo " + nombreJugador + "!!");
                        menuPrincipal(rutaRegistros);
                    }
                    break;
                case 2:
                    System.out.print("Ingrese Apodo: ");
                    nombreJugador = sc.nextLine().trim();
                    medallas = "none";
                    pokemonsJugador.clear();
                    for (Gimnasio g : gimnasios) g.setEstado("Sin derrotar");
                    System.out.println("Bienvenido " + nombreJugador + "!!");
                    menuPrincipal(rutaRegistros);
                    break;
                case 3:
                    System.out.println("Hasta luego!");
                    return;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }
 
    public void menuPrincipal(String rutaRegistros) throws IOException {
        while (true) {
            System.out.println("\n" + nombreJugador + ", que deseas hacer?");
            System.out.println("1) Revisar equipo.");
            System.out.println("2) Salir a capturar.");
            System.out.println("3) Acceso al PC (cambiar Pokemon del equipo).");
            System.out.println("4) Retar un gimnasio.");
            System.out.println("5) Desafio al Alto Mando.");
            System.out.println("6) Curar Pokemon.");
            System.out.println("7) Guardar.");
            System.out.println("8) Guardar y Salir.");
            int op = leerOpcion();
            switch (op) {
                case 1: revisarEquipo(); break;
                case 2: salirACapturar(); break;
                case 3: accesoPC(); break;
                case 4: retarGimnasio(); break;
                case 5: desafioAltoMando(); break;
                case 6: curarPokemons(); break;
                case 7: guardarRegistros(rutaRegistros); break;
                case 8:
                    guardarRegistros(rutaRegistros);
                    System.out.println("Nos vemos entrenador...");
                    return;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }
}