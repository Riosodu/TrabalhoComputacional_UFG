package com.ufgec.trabalhocomputacional.utils;

import java.time.LocalTime;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class ListaAleatoriaPonderada<T> {
    private final NavigableMap<Double, T> mapa = new TreeMap<>();
    private final Random random;
    private double total = 0;

    public ListaAleatoriaPonderada(){
        this.random = new Random();
    }

    public ListaAleatoriaPonderada(Random random) {
        this.random = random;
    }

    public ListaAleatoriaPonderada<T> adicionar(T item, double peso) {
        if(peso > 0) {
            total += peso;
            mapa.put(total, item);
        }
        return this;
    }

    public T obter() {
        double value = random.nextDouble() * total;
        return mapa.higherEntry(value).getValue();
    }
}
