package com.ufgec.trabalhocomputacional.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GrafoTest {
    @Test
    void testGrafo() {

        Grafo<Character, Integer> grafo = new Grafo<>(8);
        grafo.adicionarVertice('a');
        grafo.adicionarVertice('b');
        grafo.adicionarVertice('c');
        grafo.adicionarVertice('d');
        grafo.adicionarVertice('e');
        grafo.adicionarVertice('f');
        grafo.adicionarVertice('g');
        grafo.adicionarVertice('h');

        grafo.adicionarAresta('a','b', 1,0);
        grafo.adicionarAresta('b','c', 2,0);
        grafo.adicionarAresta('c','d', 3,0);
        grafo.adicionarAresta('d','e', 4,0);
        grafo.adicionarAresta('e','h', 5,0);
        grafo.adicionarAresta('c','f', 6,0);
        grafo.adicionarAresta('f','g', 7,0);
        grafo.adicionarAresta('h','c', 8,0);
        grafo.adicionarAresta('h', 'c', 9,0);

        for(Aresta<Integer> aresta: grafo.getArestasPara('c'))
            System.out.println(aresta.getObjetoDado());
    }

    @Test
    void testes() {
        System.out.println("Iniciado");
        int x = 70000;

        System.out.println("X inserido: " + x);
        x = (int) (50 * Math.sqrt(15 * x));
        System.out.println("Novo X: " + x);

        x = (int) Math.pow(x*15., 2) / 50;
        System.out.println("X reconstruído: " + x);
    }
}