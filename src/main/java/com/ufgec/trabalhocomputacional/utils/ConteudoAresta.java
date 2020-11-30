package com.ufgec.trabalhocomputacional.utils;

import java.util.Objects;

public class ConteudoAresta<A> {
    private final double peso;
    private final A objetoDado;

    public ConteudoAresta(double peso, A objeto) {
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
        if (!(o instanceof ConteudoAresta)) return false;
        ConteudoAresta<A> conteudoAresta = (ConteudoAresta<A>) o;
        return objetoDado.equals(conteudoAresta.objetoDado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objetoDado);
    }
}
