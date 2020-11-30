package com.ufgec.trabalhocomputacional.utils;

import com.ufgec.trabalhocomputacional.model.Aeroporto;
import org.junit.jupiter.api.Test;

import java.util.Deque;
import java.util.List;

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
        grafo.adicionarAresta('b','f', 3, 0);
        grafo.adicionarAresta('c','d', 4,0);
        grafo.adicionarAresta('d','e', 5,0);
        grafo.adicionarAresta('e','h', 6,0);
        grafo.adicionarAresta('h','c', 7,0);
        grafo.adicionarAresta('c','f', 8,0);
        grafo.adicionarAresta('f','g', 9,0);
        grafo.adicionarAresta('f', 'h', 10, 0);
        grafo.adicionarAresta('h', 'c', 11,0);


        for(ConteudoAresta<Integer> conteudoAresta : grafo.getArestasPara('c'))
            System.out.println(conteudoAresta.getObjetoDado());

        System.out.println("\n\n--------------------------------------");
        List<Deque<Character>> lista = grafo.rotaViaDFS('a', 'h', 3);
        System.out.println("Quantidade de rotas: " + lista.size());
        for(Deque<Character> rota: lista) {
            System.out.println("Quantidade de elementos: " + rota.size());
            for(Character c: rota)
                System.out.print(c + " -> ");
        }
    }

    @Test
    void testes() {
        System.out.println((Math.pow(2, 4)*(4+Math.pow(5,6)))/365);
    }


}