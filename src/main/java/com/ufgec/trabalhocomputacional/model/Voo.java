package com.ufgec.trabalhocomputacional.model;

import com.ufgec.trabalhocomputacional.classes.TipoVoo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.SortedMap;

@Entity
@Table(name="TB_VOO")
public class Voo {

    @Id
    private Long id;

    private Aeroporto localOrigem;

    private Aeroporto localDestino;

    private LocalDateTime horarioPartida;

    private LocalDateTime previsaoChegada;

    private Map<TipoVoo, SortedMap<Integer, Passageiro>> poltronas;

    public boolean reservarPoltrona(int poltrona, Passageiro passageiro, TipoVoo classe) {
        if(checarSePoltronaDisponivel(poltrona, classe))
            return false;
        else {
            poltronas.get(classe).put(poltrona, passageiro);
            return true;
        }
    }

    public boolean checarSePoltronaDisponivel(int poltrona, TipoVoo classe) {
        return !poltronas.get(classe).containsKey(poltrona);
    }





}
