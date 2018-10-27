package com.example.mislugaresapp;

public interface Lugares {
    Lugar elemento(int id); //returns the specified element indicated by its id
    void anyade(Lugar lugar); //Adds the specified element
    int nuevo(); //Adds a new empty element and returns its id
    void borrar(int id); //deletes the element with the specified id
    int tamanyo(); //returns the number of elements
    void actualiza(int id, Lugar lugar); //replace an element
}
