package com.ufgec.trabalhocomputacional.model;

import com.ufgec.trabalhocomputacional.utils.ConteudoAresta;
import com.ufgec.trabalhocomputacional.utils.Grafo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Rota implements Comparable<Rota> {
    public static final int PRECO = 0;
    public static final int DISTANCIA = 1;
    public static final int TEMPO = 2;

    private static final Grafo<Aeroporto, Rota> ROTAS_PRECO = new Grafo<>(100);
    private static final Grafo<Aeroporto, Rota> ROTAS_DISTANCIA = new Grafo<>(100);
    private static final Grafo<Aeroporto, Rota> ROTAS_TEMPO = new Grafo<>(100);

    private final Deque<Aeroporto.Voo> trajetoVoos;
    private final double preco;

    private static LocalDate dataInicial, dataFinal;
    private static List<List<Aeroporto.Voo>> voosDoDia;
    private static Deque<Aeroporto.Voo> trajeto;
    private static List<Rota> rotas;
    private static int indexExterno;

    private Rota(Deque<Aeroporto.Voo> trajeto) {
        trajetoVoos = new LinkedList<>(trajeto);

        if(trajetoVoos.size() == 1)
            preco = 1.5 * getLocalOrigem().distancia(getLocalDestino());
        else
            preco = 0.2 * trajetoVoos.size() * getLocalOrigem().distancia(getLocalDestino());

        ROTAS_PRECO.adicionarAresta(getLocalOrigem(), getLocalDestino(), this, preco);
        ROTAS_TEMPO.adicionarAresta(getLocalOrigem(), getLocalDestino(), this, getTempoRotaMinutos());
        ROTAS_DISTANCIA.adicionarAresta(getLocalOrigem(), getLocalDestino(), this, getDistanciaTotalRota());
    }

    public Deque<Aeroporto.Voo> getTrajeto() {
        return trajetoVoos;
    }

    public Aeroporto getLocalOrigem() {
        return trajetoVoos.getFirst().getLocalOrigem();
    }

    public Aeroporto getLocalDestino() {
        return trajetoVoos.getLast().getLocalDestino();
    }

    public LocalDateTime getHorarioPartida() {
        return trajetoVoos.getFirst().getHorarioPartida();
    }

    public LocalDateTime getPrevisaoChegada() {
        return trajetoVoos.getLast().getPrevisaoChegada();
    }

    /**
     * @param de é o Aeroporto de origem.
     * @param para é o Aeroporto de destino final.
     * @param ordenacao é uma constante que definirá a organização da lista. As constantes são PRECO, DISTANCIA, TEMPO.
     * @return este método retorna uma lista de rotas ordenadas por seus pesos
     */
    public static List<Rota> getRotasDePara(Aeroporto de, Aeroporto para, int ordenacao) {
        List<ConteudoAresta<Rota>> pesos;
        List<Rota> lista = new ArrayList<>();
        switch (ordenacao) {
            case 0 -> {
                pesos = ROTAS_PRECO.getArestasDePara(de, para);
                pesos.sort(Comparator.comparing(ConteudoAresta::getPeso));
                for (ConteudoAresta<Rota> aresta : pesos)
                    lista.add(aresta.getObjetoDado());
            }
            case 1 -> {
                pesos = ROTAS_DISTANCIA.getArestasDePara(de, para);
                pesos.sort(Comparator.comparing(ConteudoAresta::getPeso));
                for (ConteudoAresta<Rota> aresta : pesos)
                    lista.add(aresta.getObjetoDado());
            }
            case 2 -> {
                pesos = ROTAS_TEMPO.getArestasDePara(de, para);
                pesos.sort(Comparator.comparing(ConteudoAresta::getPeso));
                for (ConteudoAresta<Rota> aresta : pesos)
                    lista.add(aresta.getObjetoDado());
            }
            default -> throw new IllegalArgumentException("Valor do parâmetro ordenação não é válido!");
        }

        return lista;
    }

    /**
     * @param de é o Aeroporto de origem.
     * @param para é o Aeroporto de destino final.
     * @param ordenacao é uma constante que definirá a organização da lista. As constantes são PRECO, DISTANCIA, TEMPO.
     * @return este método retorna uma lista de rotas ordenadas por seus pesos
     */
    public static List<Rota> getRotasDeParaNumIntervalo(Aeroporto de, Aeroporto para,
                                                        LocalDate dataInicial, LocalDate dataFinal,
                                                        int ordenacao) {
        List<ConteudoAresta<Rota>> pesos;
        List<Rota> lista = new ArrayList<>();
        switch (ordenacao) {
            case 0 -> {
                pesos = ROTAS_PRECO.getArestasDePara(de, para);
                pesos.sort(Comparator.comparing(ConteudoAresta::getPeso));

                LocalDate tempDate;
                boolean estaNoIntervalo;
                for (ConteudoAresta<Rota> aresta : pesos) {
                    tempDate = aresta.getObjetoDado().getHorarioPartida().toLocalDate();
                    estaNoIntervalo = (tempDate.isEqual(dataInicial) || tempDate.isAfter(dataInicial))
                            && (tempDate.isEqual(dataFinal) || tempDate.isBefore(dataFinal));
                    if (estaNoIntervalo)
                        lista.add(aresta.getObjetoDado());
                }
            }
            case 1 -> {
                pesos = ROTAS_DISTANCIA.getArestasDePara(de, para);
                pesos.sort(Comparator.comparing(ConteudoAresta::getPeso));

                LocalDate tempDate;
                boolean estaNoIntervalo;
                for (ConteudoAresta<Rota> aresta : pesos) {
                    tempDate = aresta.getObjetoDado().getHorarioPartida().toLocalDate();
                    estaNoIntervalo = (tempDate.isEqual(dataInicial) || tempDate.isAfter(dataInicial))
                            && (tempDate.isEqual(dataFinal) || tempDate.isBefore(dataFinal));
                    if (estaNoIntervalo)
                        lista.add(aresta.getObjetoDado());
                }
            }
            case 2 -> {
                pesos = ROTAS_TEMPO.getArestasDePara(de, para);
                pesos.sort(Comparator.comparing(ConteudoAresta::getPeso));

                LocalDate tempDate;
                boolean estaNoIntervalo;
                for (ConteudoAresta<Rota> aresta : pesos) {
                    tempDate = aresta.getObjetoDado().getHorarioPartida().toLocalDate();
                    estaNoIntervalo = (tempDate.isEqual(dataInicial) || tempDate.isAfter(dataInicial))
                            && (tempDate.isEqual(dataFinal) || tempDate.isBefore(dataFinal));
                    if (estaNoIntervalo)
                        lista.add(aresta.getObjetoDado());
                }
            }
            default -> throw new IllegalArgumentException("Valor do parâmetro ordenação não é válido!");
        }

        return lista;
    }


    public int getTempoRotaMinutos() {
        return (getPrevisaoChegada().getHour()*60 + getPrevisaoChegada().getMinute()) -
                (getHorarioPartida().getHour()*60 + getHorarioPartida().getMinute());
    }

    public double getDistanciaTotalRota() {
        int distancia = 0;
        for(Aeroporto.Voo voo: trajetoVoos) {
            distancia += voo.getLocalOrigem().distancia(voo.getLocalDestino());
        }

        return distancia;
    }

    public static List<Rota> gerarRotas(Aeroporto localOrigem, Aeroporto localDestino,
                                        LocalDate dataInicial, LocalDate dataFinal) {
        List<Deque<Aeroporto>> listaPossiveisRotas = Aeroporto.getPossiveisRotas(localOrigem, localDestino, 3);
        Rota.dataInicial = dataInicial;
        Rota.dataFinal = dataFinal;
        for(Deque<Aeroporto> rota: listaPossiveisRotas)
            gerarRotasValidas(new ArrayList<>(rota));

        return rotas;
    }

    private static void gerarRotasValidas(List<Aeroporto> rota) {
        boolean rotaPossuiAeroportoRepetido = false;
        for(Aeroporto aeroporto: rota) { //Verificar se a rota não possui repetição de aeroportos
            int contador = 0;
            for (Aeroporto item : rota){
                if (item.equals(aeroporto))
                    contador++;
            }

            if(contador > 1) {
                rotaPossuiAeroportoRepetido = true;
                break;
            }
        }

        if(!rotaPossuiAeroportoRepetido) {
            voosDoDia = new ArrayList<>();
            trajeto = new LinkedList<>();
            rotas = new ArrayList<>();
            indexExterno = 0;


            for(int i = 0; i < rota.size()-1; i++) {
                voosDoDia.add(rota.get(i).voosDaquiParaNumIntervalo(rota.get(i+1), dataInicial, dataFinal));
            }

            recursivo();
        }
    }

    private static void recursivo() {
        //cada chamada recursiva aumenta o index externo, enquanto o interno é aumentado por um loop no método
        for(int i = 0; i < voosDoDia.get(indexExterno).size(); i++) {
            Aeroporto.Voo voo = voosDoDia.get(indexExterno).get(i);
            Aeroporto.Voo peek = trajeto.peek();

            if(peek == null) { //se o trajeto está vazio
                trajeto.push(voo); //adiciono o primeiro elemento a ele
            } else { //se não está vazio
                //checo se este voo acontece depois de 45 minutos a partir da chegada do ultimo voo do trajeto
                if(voo.getHorarioPartida().isEqual(peek.getPrevisaoChegada().plusMinutes(45)) ||
                voo.getHorarioPartida().isAfter(peek.getPrevisaoChegada().plusMinutes(45))) {
                    trajeto.push(voosDoDia.get(indexExterno).get(i)); // Se sim, adiciona o voo no trajeto;
                }
            }

            if(indexExterno+1 < voosDoDia.size()) { //checo se ainda há indexExterno para incrementar
                indexExterno++;
                recursivo();
            } else { //se não tiver
                if(trajeto.size() > 0) //crio uma nova rota com o trajeto atual caso ele possua elementos
                    rotas.add(new Rota(trajeto));
                trajeto.pop(); //e removo o item superior da pilha
            }
        }
        indexExterno--;
    }

    @Override
    public int compareTo(Rota o) {
        return Comparator.comparing(Rota::getHorarioPartida)
                .thenComparing(Rota::getPrevisaoChegada)
                .compare(this, o);
    }

    @Override
    public String toString(){
        StringBuilder toString = new StringBuilder();
        for(Aeroporto.Voo voo: trajetoVoos) {
            toString.append(voo.toString());
            toString.append("\n>\n>\n>");
        }
        return toString.toString();
    }
}
