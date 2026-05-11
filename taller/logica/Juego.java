package logica;

import java.util.*;
 
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
}