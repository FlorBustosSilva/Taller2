package logica;

import java.util.ArrayList;

public class altoMando {
    private int numero;
    private String nombre;
    private ArrayList<String> pokemons;
 
    public altoMando(int numero, String nombre, ArrayList<String> pokemons) {
        this.numero = numero;
        this.nombre = nombre;
        this.pokemons = pokemons;
    }
 
    public int getNumero() { return numero; }
    public String getNombre() { return nombre; }
    public ArrayList<String> getPokemons() { return pokemons; }
}
 