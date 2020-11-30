package com.ufgec.trabalhocomputacional.utils;

import com.ufgec.trabalhocomputacional.model.Aeroporto;

import java.util.*;

public class Grafo<V, A> {
    private final List<V> listaVertices; //Lista de vértices
    private final List<List<V>> listaVizinhos; //Lista de vértices adjacentes a um vértice
    private final List<List<List<ConteudoAresta<A>>>> listaArestasDaqui; //Lista de dados relacionados de um vértice a um vértice adjacente
    private final List<List<List<ConteudoAresta<A>>>> listaArestasParaAqui; //Lista de arestas que são direcionadas de um vértice adjacente para um vértice
    private Map<V, Boolean> visitado;

    public Grafo(int minVerts) {
        listaVertices = new ArrayList<>(minVerts);
        listaVizinhos = new ArrayList<>(minVerts);
        listaArestasDaqui = new ArrayList<>();
        listaArestasParaAqui = new ArrayList<>();
        visitado = new HashMap<>();
    }

    public List<V> getListaVertices() {
        return listaVertices;
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

        List<ConteudoAresta<A>> listaArstsDaqui = listaArestasDaqui.get(indexDe).get(indexPara);
        ConteudoAresta<A> conteudoAresta = new ConteudoAresta<>(peso, dados);
        boolean contem = listaArstsDaqui.contains(conteudoAresta);

        indexPara = listaVertices.indexOf(para);
        indexDe = listaVizinhos.get(indexPara).indexOf(de);
        List<ConteudoAresta<A>> listaArestasParaAquiDoDestino = listaArestasParaAqui.get(indexPara).get(indexDe);

        if(!contem) {
            listaArstsDaqui.add(conteudoAresta);
            listaArestasParaAquiDoDestino.add(conteudoAresta);
            return true;
        }

        return false;
    }

    public List<Deque<V>> rotaViaDFS(V de, V para, int distanciaMaxima) {
        Deque<V> pilhaVisita = new LinkedList<>(); //pilha que será usada para realizar a busca
        List<Deque<V>> rotas = new ArrayList<>(); //lista de rotas possíveis

        //------------------------------------------------------------------------------------------------------------//
        //Declara as variáveis que serão usadas para buscar o próximo vértice a ser visitado
        List<V> listVizinhos;
        Map<V, Integer> proximoIndexParaChecar = new HashMap<>();
        V peek;
        V verticeBusca;
        V anterior;
        //------------------------------------------------------------------------------------------------------------//
        //Inicia a busca por rotas
        rotas.add(new LinkedList<>());
        int indexListaRotas = 0;
        int profundidade = 0;

        limparMapaVisitado();
        visitado.put(de, true);

        pilhaVisita.push(de);
        rotas.get(indexListaRotas).add(de);

        while(!pilhaVisita.isEmpty()) {
            System.out.println("Começo do while");
            System.out.println(pilhaVisita.peek());
            System.out.println("Profundidade: " + profundidade);
            verticeBusca = null;
            if(profundidade < distanciaMaxima) {
                //--------------------------------------------------------------------------------------------------------//
                //Inicializa as variáveis que serão utilizadas na busca do próximo vértice adjacente não visitado
                peek = pilhaVisita.peek();
                proximoIndexParaChecar.putIfAbsent(peek, 0);
                listVizinhos = listaVizinhos.get(listaVertices.indexOf(peek));
                System.out.println("Próximo index a checar: " + proximoIndexParaChecar.get(peek));

                //--------------------------------------------------------------------------------------------------------//
                //Busca o próximo vértice adjacente não visitado que possua arestas conectadas
                for (int i = proximoIndexParaChecar.get(peek); i < listVizinhos.size(); i++) {
                    System.out.println("Começo do for. Index " + i);
                    //vértice adjacente possui alguma aresta com conteúdo (objeto de dados)?
                    if (listaArestasDaqui.get(listaVertices.indexOf(peek)).get(i).size() > 0) {
                        System.out.println("Index " + i + " possui aresta com conteúdo. Quant. conteudo: " + listaArestasDaqui.get(listaVertices.indexOf(peek)).get(i).size());
                        verticeBusca = listaVertices.get(
                                listaVertices.indexOf(
                                        listaVizinhos.get(listaVertices.indexOf(peek))
                                                .get(i)
                                )
                        );
                        System.out.println("Vertice de busca encontrado: " + verticeBusca);
                        proximoIndexParaChecar.put(peek, i+1);
                        break;
                    }
                    System.out.println("Index " + i + " não possui aresta com conteúdo.");
                    proximoIndexParaChecar.put(peek, i+1);
                }

                System.out.println("Vértice adjacente: " + verticeBusca);
            }
            //--------------------------------------------------------------------------------------------------------//
            //Se não existir um vértice adjacente que possua uma conexão com dados ou
            //a profundidade/distância máxima tenha sido atingida
            if(verticeBusca == null) {
                pilhaVisita.pop(); //vá para o próximo item da pilha
                profundidade--; //diminua a profundidade

                //Se o último elemento da rota é o vertice de destino, esta é uma rota válida, então:
                if(rotas.get(indexListaRotas).peekLast() != null && rotas.get(indexListaRotas).peekLast().equals(para)) {
                    System.out.println("!!!!!!!!!!!!!Achou uma rota válida!!!!!!!!!!!!!!!!!!!!");
                    for(V v: rotas.get(indexListaRotas))
                        System.out.print(v + "->");


                    rotas.add(new LinkedList<>()); //crie uma nova rota a partir da rota antiga
                    for(V v: rotas.get(indexListaRotas++))
                        rotas.get(indexListaRotas).add(v);

                    System.out.println("Index de rota atual: " + indexListaRotas);
                    System.out.println("Removendo: " + rotas.get(indexListaRotas).pollLast());//sem o último elemento
                } else { //não é uma rota válida
                    System.out.println("Index de rota atual: " + indexListaRotas);
                    System.out.println("Rota não válida. Removendo: " + rotas.get(indexListaRotas).pollLast()); //descarte o último elemento;
                }
            } else { //se existir ou profundidade/distancia < máximo
                pilhaVisita.push(verticeBusca); //adiciona na pilha
                System.out.println(rotas.get(indexListaRotas).add(verticeBusca)); //adiciona na rota;
                profundidade++; //aumenta a profundidade
            }
        }

        //------------------------------------------------------------------------------------------------------------//
        //A pilha está vazia.
        //Deleta a ultima lista de rota que sempre estará vazia
        rotas.remove(rotas.get(rotas.size()-1));
        return rotas;
    }


    public Deque<Integer> bfs(V em, V para) {
        int indiceComeco = listaVertices.indexOf(em);
        int indiceFim = listaVertices.indexOf(para);

        Deque<Integer> listaVisita = new LinkedList<>();
        listaVisita.addLast(indiceComeco);

        limparMapaVisitado();
        visitado.put(listaVertices.get(indiceComeco), true);

        Deque<Integer> anterior = new LinkedList<>();
        List<Integer> vizinhos = new ArrayList<>();
        int node;
        while(!listaVisita.isEmpty()) {
            node = listaVisita.pollFirst();
            for(V vizinho: listaVizinhos.get(node))
                vizinhos.add(listaVertices.indexOf(vizinho));

            for(int proximo: vizinhos) {
                if(!visitado.get(listaVertices.get(proximo))) {
                    listaVisita.addLast(proximo);
                    visitado.replace(listaVertices.get(proximo), true);
                    //prev[next] = node -> anterior.add(node) ou anterior.add(listaVertices.indexOf(proximo), node)?
                    anterior.addLast(node);
                }
            }
        }

        Deque<Integer> caminho = new LinkedList<>();
        for(Integer i = indiceFim; i != null; i = anterior.pollFirst())
            caminho.addLast(i);

        if(caminho.getFirst() == indiceComeco)
            return caminho;
        return null;
    }

    private void limparMapaVisitado() {
        visitado = new HashMap<>();
    }

    public List<ConteudoAresta<A>> getArestasDePara(V de, V para) {
        return listaArestasDaqui.get(listaVertices.indexOf(de))
                .get(listaVizinhos.get(listaVertices.indexOf(de)).indexOf(para));
    }

    public List<ConteudoAresta<A>> getArestasDe(V de) {
        List<List<ConteudoAresta<A>>> daqui = listaArestasDaqui.get(listaVertices.indexOf(de));
        List<ConteudoAresta<A>> retorno = new ArrayList<>();

        for(List<ConteudoAresta<A>> listaConteudoArestas : daqui) {
            retorno.addAll(listaConteudoArestas);
        }

        return retorno;
    }

    public List<ConteudoAresta<A>> getArestasPara(V para) {
        List<List<ConteudoAresta<A>>> daqui = listaArestasParaAqui.get(listaVertices.indexOf(para));
        List<ConteudoAresta<A>> retorno = new ArrayList<>();

        for(List<ConteudoAresta<A>> listaConteudoArestas : daqui) {
            retorno.addAll(listaConteudoArestas);
        }

        return retorno;
    }

}
