package com.ufgec.trabalhocomputacional.model;

import org.apache.tomcat.jni.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Rota implements Comparable<Rota> {
    private final Deque<Aeroporto.Voo> trajetoVoos;

    private static LocalDate dataInicial, dataFinal;
    private static List<List<Aeroporto.Voo>> voosDoDia;
    private static Deque<Aeroporto.Voo> trajeto;
    private static List<Rota> rotas;
    private static int indexExterno;

    private Rota(Deque<Aeroporto.Voo> trajeto) {
        trajetoVoos = new LinkedList<>(trajeto);
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
}
