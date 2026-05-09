package logica;

import java.util.ArrayList;

public class Gimnasio {
    private int numero;
    private String lider;
    private String estado;
    private int cantPokemons;
    private ArrayList<String> pokemons;
 
    public Gimnasio(int numero, String lider, String estado, int cantPokemons, ArrayList<String> pokemons) {
        this.numero = numero;
        this.lider = lider;
        this.estado = estado;
        this.cantPokemons = cantPokemons;
        this.pokemons = pokemons;
    }
 
    public int getNumero() { return numero; }
    public String getLider() { return lider; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public int getCantPokemons() { return cantPokemons; }
    public ArrayList<String> getPokemons() { return pokemons; }
}
 