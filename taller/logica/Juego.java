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
 
    private void revisarEquipo() {
        ArrayList<Pokemon> equipo = getEquipo();
        if (equipo.isEmpty()) {
            System.out.println("No tienes ningun Pokemon en tu equipo.");
            return;
        }
        System.out.println("\nEquipo Actual:");
        for (int i = 0; i < equipo.size(); i++) {
            Pokemon pk = equipo.get(i);
            System.out.println((i + 1) + ") " + pk.getNombre() + "|" + pk.getTipo()
                    + "|Stats totales: " + pk.getStatsTotal() + " [" + pk.getEstado() + "]");
        }
    }
 
    private void salirACapturar() {
        System.out.println("\nDonde deseas ir a explorar?\n\nZonas disponibles:");
        for (int i = 0; i < habitats.size(); i++) {
            System.out.println((i + 1) + ") " + habitats.get(i));
        }
        System.out.println((habitats.size() + 1) + ") Volver al menu.");
        System.out.print("Ingrese Zona: ");
        int op;
        try {
            op = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Opcion invalida.");
            return;
        }
        if (op == habitats.size() + 1) return;
        if (op < 1 || op > habitats.size()) {
            System.out.println("Zona invalida.");
            return;
        }
        String zonaElegida = habitats.get(op - 1);
 
        ArrayList<Pokemon> pokemonsZona = new ArrayList<>();
        for (Pokemon pk : pokedex) {
            if (pk.getHabitat().equalsIgnoreCase(zonaElegida) && pk.getPorcentajeAparicion() > 0) {
                pokemonsZona.add(pk);
            }
        }
        if (pokemonsZona.isEmpty()) {
            System.out.println("No hay Pokemon en esa zona.");
            return;
        }
        Random rand = new Random();
        double roll = rand.nextDouble();
        double acum = 0;
        Pokemon aparecido = pokemonsZona.get(pokemonsZona.size() - 1); // fallback
        for (Pokemon pk : pokemonsZona) {
            acum += pk.getPorcentajeAparicion();
            if (roll <= acum) {
                aparecido = pk;
                break;
            }
        }
 
        System.out.println("\nOh!! Ha aparecido un increible " + aparecido.getNombre() + "!!\n");
        System.out.println("Que deseas hacer?\n1) Capturar\n2) Huir");
        System.out.print("Ingrese Opcion: ");
        int elec;
        try {
            elec = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Opcion invalida.");
            return;
        }
        if (elec == 1) {
            boolean yaCapturado = false;
            for (Pokemon pk : pokemonsJugador) {
                if (pk.getNombre().equalsIgnoreCase(aparecido.getNombre())) {
                    yaCapturado = true;
                    break;
                }
            }
            if (yaCapturado) {
                System.out.println("Ya tienes a " + aparecido.getNombre() + " en tu lista!");
            } else {
                Pokemon nuevo = clonarPokemon(aparecido);
                pokemonsJugador.add(nuevo);
                System.out.println(aparecido.getNombre() + " capturado con exito!!");
                if (pokemonsJugador.size() <= 6) {
                    System.out.println(aparecido.getNombre() + " ha sido agregado a tu equipo!");
                } else {
                    System.out.println(aparecido.getNombre() + " ha sido guardado en el PC.");
                }
            }
        } else {
            System.out.println("Huiste del combate.");
        }
    }
 
    private void accesoPC() {
        if (pokemonsJugador.isEmpty()) {
            System.out.println("No tienes ningun Pokemon.");
            return;
        }
        System.out.println("\nTus Pokemon:");
        for (int i = 0; i < pokemonsJugador.size(); i++) {
            Pokemon pk = pokemonsJugador.get(i);
            String tag = (i < 6) ? " [EQUIPO]" : " [PC]";
            System.out.println((i + 1) + ") " + pk.getNombre() + " | " + pk.getTipo()
                    + " | " + pk.getEstado() + tag);
        }
        System.out.println("\n1) Cambiar Pokemon.\n2) Salir.");
        int op = leerOpcion();
        if (op == 2) return;
        if (op != 1) { System.out.println("Opcion invalida."); return; }
 
        System.out.print("Ingrese el numero del primer Pokemon a intercambiar: ");
        int a, b;
        try {
            a = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Ingrese el numero del segundo Pokemon a intercambiar: ");
            b = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida.");
            return;
        }
        if (a < 1 || a > pokemonsJugador.size() || b < 1 || b > pokemonsJugador.size()) {
            System.out.println("Numeros fuera de rango.");
            return;
        }
        Collections.swap(pokemonsJugador, a - 1, b - 1);
        System.out.println("Pokemon intercambiados correctamente.");
    }
 
    private void retarGimnasio() {
        System.out.println("\nA cual Lider deseas retar??\n");
        for (int i = 0; i < gimnasios.size(); i++) {
            Gimnasio g = gimnasios.get(i);
            System.out.println((i + 1) + ") " + g.getLider() + " - Estado: " + g.getEstado());
        }
        System.out.println((gimnasios.size() + 1) + ") Volver al menu.");
        int op = leerOpcion();
        if (op == gimnasios.size() + 1) return;
        if (op < 1 || op > gimnasios.size()) { System.out.println("Opcion invalida."); return; }
 
        Gimnasio objetivo = gimnasios.get(op - 1);
        for (int i = 0; i < op - 1; i++) {
            if (!gimnasios.get(i).getEstado().equals("Derrotado")) {
                System.out.println("Calmado Entrenador!!! No puedes retar a " + objetivo.getLider()
                        + " sin haber derrotado a los lideres anteriores!!");
                return;
            }
        }
 
        if (objetivo.getEstado().equals("Derrotado")) {
            System.out.println("Ya has derrotado a " + objetivo.getLider() + "!");
            return;
        }
 
        if (pokemonsJugador.isEmpty() || equipoSinVida()) {
            System.out.println("No tienes Pokemon disponibles para combatir.");
            return;
        }
 
        System.out.println("Desafiando a " + objetivo.getLider() + "!!");
        boolean victoria = combatirOponente(objetivo.getLider(), objetivo.getPokemons());
        if (victoria) {
            objetivo.setEstado("Derrotado");
            System.out.println("Has derrotado a " + objetivo.getLider() + "! Medalla obtenida!");
        }
    }
 
    private void desafioAltoMando() {
        for (Gimnasio g : gimnasios) {
            if (!g.getEstado().equals("Derrotado")) {
                System.out.println("Debes derrotar los 8 gimnasios antes de retar al Alto Mando!");
                return;
            }
        }
        if (equipoSinVida()) {
            System.out.println("No tienes Pokemon disponibles.");
            return;
        }
 
        System.out.println("\nBienvenido al Alto Mando! Enfrentaras a " + altoMando.size() + " oponentes consecutivos.");
        for (AltoMando am : altoMando) {
            if (equipoSinVida()) {
                System.out.println("No tienes Pokemon disponibles. Has sido derrotado.");
                return;
            }
            System.out.println("\nDesafiando a " + am.getNombre() + "!!");
            boolean victoria = combatirOponente(am.getNombre(), am.getPokemons());
            if (!victoria) {
                System.out.println("Has sido derrotado. Regresando al menu...");
                return;
            }
        }
        System.out.println("\n*** FELICITACIONES " + nombreJugador.toUpperCase() + "!! Eres el nuevo CAMPEON!! ***");
    }
 
    private void curarPokemons() {
        boolean habiaDebilitados = false;
        for (Pokemon pk : pokemonsJugador) {
            if (pk.getEstado().equals("Debilitado")) {
                pk.setEstado("Vivo");
                habiaDebilitados = true;
            }
        }
        if (habiaDebilitados) {
            System.out.println("Tu equipo se ha recuperado!");
        } else {
            System.out.println("Todos tus Pokemon ya estan sanos.");
        }
    }
    private boolean combatirOponente(String nombreOponente, ArrayList<String> listaPokemonsOponente) {
        ArrayList<Pokemon> pokemonsOponente = new ArrayList<>();
        for (String nombre : listaPokemonsOponente) {
            Pokemon pk = buscarEnPokedex(nombre);
            if (pk != null) {
                pokemonsOponente.add(clonarPokemon(pk));
            } else {
                System.out.println("[Aviso: " + nombre + " no esta en la Pokedex, se omite]");
            }
        }
        if (pokemonsOponente.isEmpty()) {
            System.out.println("El oponente no tiene Pokemon validos.");
            return true;
        }
 
        int indexOponente = 0;
        Pokemon pokemonOponente = pokemonsOponente.get(indexOponente);
        System.out.println(nombreOponente + " saca a " + pokemonOponente.getNombre() + "!");
 
        Pokemon pokemonJugador = getPokemonActivoEquipo();
        System.out.println(nombreJugador + " saca a " + pokemonJugador.getNombre() + "!");
 
        while (true) {
            System.out.println("\nQue deseas hacer?");
            System.out.println("1) Atacar");
            System.out.println("2) Cambiar de pokemon");
            System.out.println("3) Rendirse");
            int op = leerOpcion();
 
            if (op == 3) {
                System.out.println("Te has rendido. Volviendo al menu...");
                return false;
            } else if (op == 2) {
                pokemonJugador = cambiarPokemonEnCombate(pokemonJugador);
            } else if (op == 1) {
                int statsJugador = pokemonJugador.getStatsTotal();
                int statsOponente = pokemonOponente.getStatsTotal();
 
                System.out.println(pokemonJugador.getNombre() + " -> " + statsJugador + " puntos");
                System.out.println(pokemonOponente.getNombre() + " -> " + statsOponente + " puntos");
 
                double efectividad = tablaTipos.getEfectividad(pokemonJugador.getTipo(), pokemonOponente.getTipo());
                double statsJugadorFinal = statsJugador;
                double statsOponenteFinal = statsOponente;
 
                if (efectividad == 2.0) {
                    System.out.println(pokemonJugador.getNombre() + " es super efectivo contra " + pokemonOponente.getNombre() + "!");
                    statsJugadorFinal *= 2;
                } else if (efectividad == 0.5) {
                    System.out.println(pokemonJugador.getNombre() + " no es efectivo contra " + pokemonOponente.getNombre() + "!");
                    statsJugadorFinal /= 2;
                } else if (efectividad == 0.0) {
                    System.out.println(pokemonJugador.getNombre() + " no tiene efecto contra " + pokemonOponente.getNombre() + "!");
                    statsJugadorFinal = 0;
                }
 
                System.out.println("Nuevo puntaje:");
                System.out.println(pokemonJugador.getNombre() + " -> " + (int) statsJugadorFinal + " puntos");
                System.out.println(pokemonOponente.getNombre() + " -> " + (int) statsOponenteFinal + " puntos");
 
                if (statsJugadorFinal > statsOponenteFinal) {
                    System.out.println("Ha ganado " + pokemonJugador.getNombre() + "! "
                            + pokemonOponente.getNombre() + " ha sido derrotado...");
                    indexOponente++;
                    if (indexOponente >= pokemonsOponente.size()) {
                        System.out.println("Has derrotado a " + nombreOponente + "!!");
                        return true;
                    }
                    pokemonOponente = pokemonsOponente.get(indexOponente);
                    System.out.println(nombreOponente + " saca a " + pokemonOponente.getNombre() + "!");
                } else {
                    System.out.println("Ha ganado " + pokemonOponente.getNombre() + "! "
                            + pokemonJugador.getNombre() + " ha sido derrotado...");
                    pokemonJugador.setEstado("Debilitado");
 
                    Pokemon siguiente = getPokemonActivoEquipo();
                    if (siguiente == null) {
                        System.out.println("Te has quedado sin pokemons en tu equipo!");
                        System.out.println("Volviendo al menu...");
                        return false;
                    }
                    pokemonJugador = siguiente;
                    System.out.println(nombreJugador + " saca a " + pokemonJugador.getNombre() + "!");
                }
            } else {
                System.out.println("Opcion invalida.");
            }
        }
    }
 
    private Pokemon cambiarPokemonEnCombate(Pokemon actual) {
        ArrayList<Pokemon> equipo = getEquipo();
        System.out.println("\nElige un Pokemon:");
        for (int i = 0; i < equipo.size(); i++) {
            Pokemon pk = equipo.get(i);
            System.out.println((i + 1) + ") " + pk.getNombre() + " [" + pk.getEstado() + "]");
        }
        while (true) {
            int op = leerOpcion();
            if (op < 1 || op > equipo.size()) {
                System.out.println("Opcion invalida.");
                continue;
            }
            Pokemon elegido = equipo.get(op - 1);
            if (elegido.getEstado().equals("Debilitado")) {
                System.out.println(elegido.getNombre() + " esta debilitado, elige otro.");
                continue;
            }
            System.out.println("Enviaste a " + elegido.getNombre() + "!");
            return elegido;
        }
    }
}