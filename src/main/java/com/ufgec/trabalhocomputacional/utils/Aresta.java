package com.ufgec.trabalhocomputacional.utils;

import java.util.Objects;

public class Aresta<A> {
    private final double peso;
    private final A objetoDado;

    public Aresta(double peso, A objeto) {
        this.peso = peso;
        this.objetoDado = objeto;
    }

    public double getPeso() {
        return peso;
    }

    public A getObjetoDado() {
        return objetoDado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Aresta)) return false;
        Aresta<A> aresta = (Aresta<A>) o;
        return objetoDado.equals(aresta.objetoDado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objetoDado);
    }
}
