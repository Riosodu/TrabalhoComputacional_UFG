package com.ufgec.trabalhocomputacional.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Grafo<V, A> {
    private final List<V> listaVertices; //Lista de vértices
    private final List<List<V>> listaVizinhos; //Lista de vértices adjacentes a um vértice
    private final List<List<List<Aresta<A>>>> listaArestasDaqui; //Lista de dados relacionados de um vértice a um vértice adjacente
    private final List<List<List<Aresta<A>>>> listaArestasParaAqui; //Lista de arestas que são direcionadas de um vértice adjacente para um vértice
    private Map<V, Boolean> visitado;

    public Grafo(int minVerts) {
        listaVertices = new ArrayList<>(minVerts);
        listaVizinhos = new ArrayList<>(minVerts);
        listaArestasDaqui = new ArrayList<>();
        listaArestasParaAqui = new ArrayList<>();
        visitado = new HashMap<>();
    }

    public boolean adicionarVertice(V dataObject) {
        if(!listaVertices.contains(dataObject)) {
            listaVertices.add(dataObject); //Adiciona o objeto à lista de vértices inicializada no construtor.
            listaVizinhos.add(new ArrayList<>()); //inicializa a lista de vizinhos
            listaArestasDaqui.add(new ArrayList<>()); //inicializa a lista de lista de arestas daqui
            listaArestasParaAqui.add(new ArrayList<>()); //inicializa a lista de lista de arestas para aqui
            return true;
        }

        return false;
    }

    private void adicionarVerticeVizinho(V de, V para) {
        //obtém o index do vértice de origem na lista de vértices
        int indexDe = listaVertices.indexOf(de);
        //se o vértice de origem não possui o vértice de destino em sua lista de vizinhos:
        if(!listaVizinhos.get(indexDe).contains(para)) {
            //adiciona o vértice de destino na lista de vizinhos do vértice de origem
            listaVizinhos.get(indexDe).add(para);
            //inicializa a lista de arestas para o vértice de destino.
            listaArestasDaqui.get(indexDe).add(new ArrayList<>());
            //inicializa a lista de arestas que vêm do vértice de destino
            listaArestasParaAqui.get(indexDe).add(new ArrayList<>());
        }

        //obtém o index do vértice de destino na lista de vértices
        int indexPara = listaVertices.indexOf(para);
        //verifica se o vértice de destino possui o vértice de origem na sua lista de vizinhos
        if(!listaVizinhos.get(indexPara).contains(de)) {
            //se não possuir, chama este método novamente para adicioná-lo
            adicionarVerticeVizinho(para, de);
        }
    }

    public boolean adicionarAresta(V de, V para, A dados, double peso) {
        //------------------------------------------------------------------------------------------------------------//
        /* Esse bloco de código garante que os vértices existam e todas as listas estejam inicializadas antes
         * de inserir uma aresta
         */
        if(!listaVertices.contains(de))
            adicionarVertice(de);
        if(!listaVertices.contains(para))
            adicionarVertice(para);

        if(!listaVizinhos.get(listaVertices.indexOf(de)).contains(para)) {
            adicionarVerticeVizinho(de, para);
        }
        //------------------------------------------------------------------------------------------------------------//
        //obtém o index do vértice de origem na lista de vértices
        int indexDe = listaVertices.indexOf(de);
        //obtém o index do vértice de destino na lista de vizinhos do vertice de origem
        int indexPara = listaVizinhos.get(indexDe).indexOf(para);

        List<Aresta<A>> listaArstsDaqui = listaArestasDaqui.get(indexDe).get(indexPara);
        Aresta<A> aresta = new Aresta<>(peso, dados);
        boolean contem = listaArstsDaqui.contains(aresta);

        indexPara = listaVertices.indexOf(para);
        indexDe = listaVizinhos.get(indexPara).indexOf(de);
        List<Aresta<A>> listaArestasParaAquiDoDestino = listaArestasParaAqui.get(indexPara).get(indexDe);

        System.out.println(contem);
        if(!contem) {
            listaArstsDaqui.add(aresta);
            listaArestasParaAquiDoDestino.add(aresta);
            return true;
        }

        return false;
    }

    public void dfs(V em) {
        if(visitado.get(em))
            return;
        visitado.put(em, true);


    }

    public List<Aresta<A>> getArestasDePara(V de, V para) {
        return listaArestasDaqui.get(listaVertices.indexOf(de))
                .get(listaVizinhos.get(listaVertices.indexOf(de)).indexOf(para));
    }

    public List<Aresta<A>> getArestasDe(V de) {
        List<List<Aresta<A>>> daqui = listaArestasDaqui.get(listaVertices.indexOf(de));
        List<Aresta<A>> retorno = new ArrayList<>();

        for(List<Aresta<A>> listaArestas: daqui) {
            retorno.addAll(listaArestas);
        }

        return retorno;
    }

    public List<Aresta<A>> getArestasPara(V para) {
        List<List<Aresta<A>>> daqui = listaArestasParaAqui.get(listaVertices.indexOf(para));
        List<Aresta<A>> retorno = new ArrayList<>();

        for(List<Aresta<A>> listaArestas: daqui) {
            retorno.addAll(listaArestas);
        }

        return retorno;
    }

}
